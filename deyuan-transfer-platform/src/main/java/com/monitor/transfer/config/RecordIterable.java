package com.monitor.transfer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Slf4j
// 记录迭代器实现
class RecordIterable implements Iterable<ProducerRecord<byte[], byte[]>> {
    private final String queueDir;

    public RecordIterable(String queueDir) {
        this.queueDir = queueDir;
    }

    @Override
    public Iterator<ProducerRecord<byte[], byte[]>> iterator() {
        try {
            return new RecordIterator(queueDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

@Slf4j
class RecordIterator implements Iterator<ProducerRecord<byte[], byte[]>> {
    private final File[] files;
    private int currentFileIndex = 0;
    private DataInputStream currentStream;
    private ProducerRecord<byte[], byte[]> nextRecord;

    public RecordIterator(String queueDir) throws IOException {
        this.files = new File(queueDir).listFiles((dir, name) ->
                name.startsWith("data-") && name.endsWith(".log"));
        Arrays.sort(this.files);
        openNextFile();
    }

    private void openNextFile() throws IOException {
        closeCurrentStream();
        if (currentFileIndex < files.length) {
            try {
                currentStream = new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(files[currentFileIndex])));
                currentFileIndex++;
                readNextRecord();
            } catch (IOException e) {
                throw new IOException("Failed to open queue file", e);
            }
        }
    }

    private void readNextRecord() throws IOException {
        try {
            if (currentStream == null) {
                nextRecord = null;
                return;
            }

            // 反序列化逻辑
            String topic = currentStream.readUTF();

            int keyLength = currentStream.readInt();
            byte[] key = null;
            if (keyLength >= 0) {
                key = new byte[keyLength];
                currentStream.readFully(key);
            }

            int valueLength = currentStream.readInt();
            byte[] value = new byte[valueLength];
            currentStream.readFully(value);

            long timestamp = currentStream.readLong();

            nextRecord = new ProducerRecord<>(
                    topic, null, timestamp, key, value);

        } catch (EOFException e) {
            // 文件结束，尝试打开下一个文件
            openNextFile();
        } catch (IOException e) {
            throw new IOException("Failed to read record", e);
        }
    }

    @Override
    public boolean hasNext() {
        return nextRecord != null;
    }

    @Override
    public ProducerRecord<byte[], byte[]> next() {
        if (!hasNext()) throw new NoSuchElementException();
        ProducerRecord<byte[], byte[]> record = nextRecord;
        try {
            readNextRecord();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return record;
    }

    private void closeCurrentStream() {
        if (currentStream != null) {
            try {
                currentStream.close();
            } catch (IOException e) {
                log.warn("Failed to close queue file");
            }
            currentStream = null;
        }
    }
}
