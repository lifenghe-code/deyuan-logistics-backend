package com.monitor.transfer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;

import javax.annotation.processing.FilerException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class DiskBackupQueue {
    private final String queueDir;
    private final AtomicLong writeCounter = new AtomicLong(0);
    private final ReentrantLock lock = new ReentrantLock();

    // 每个队列文件最大100MB
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    public DiskBackupQueue(String queueName) {
        this.queueDir = "deyuan-transfer-platform/kafka_fallback/backup-queue/" + queueName;
        initDir();
    }

    private void initDir() {
        File dir = new File(queueDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create queue directory");
        }
    }

    public void add(ProducerRecord<byte[], byte[]> record) throws IOException {
        lock.lock();
        try {
            // 1. 序列化记录
            byte[] data = serializeRecord(record);

            // 2. 获取当前写入文件
            File currentFile = getCurrentFile();

            // 3. 写入文件(同步刷盘)
            try (FileChannel channel = FileChannel.open(
                    currentFile.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.WRITE)) {

                ByteBuffer buffer = ByteBuffer.wrap(data);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.force(true); // 强制刷盘
            }

            writeCounter.incrementAndGet();
        } catch (Exception e) {
            log.error("Failed to backup record to disk", e);
            throw new FilerException("Backup failed");
        } finally {
            lock.unlock();
        }
    }

    private byte[] serializeRecord(ProducerRecord<byte[], byte[]> record) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            // 写入元数据
            dos.writeUTF(record.topic());
            if (record.key() != null) {
                dos.writeInt(record.key().length);
                dos.write(record.key());
            } else {
                dos.writeInt(-1);
            }

            // 写入消息体
            dos.writeInt(record.value().length);
            dos.write(record.value());

            // 写入时间戳
            dos.writeLong(record.timestamp());

            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Record serialization failed", e);
        }
    }

    private File getCurrentFile() throws IOException {
        // 查找最新的文件
        File[] files = new File(queueDir).listFiles((dir, name) ->
                name.startsWith("data-") && name.endsWith(".log"));

        if (files == null || files.length == 0) {
            return new File(queueDir, "data-0000000000.log");
        }

        File lastFile = files[files.length - 1];
        if (lastFile.length() < MAX_FILE_SIZE) {
            return lastFile;
        }

        // 滚动新文件
        String newFileName = String.format("data-%010d.log",
                files.length);
        return new File(queueDir, newFileName);
    }

    // 恢复消费的方法
    public Iterable<ProducerRecord<byte[], byte[]>> getRecords() {
        return new RecordIterable(queueDir);
    }

    // 删除已处理文件的方法
    public void cleanProcessedFiles(long processedOffset) {
        // 实现略...
    }
}
