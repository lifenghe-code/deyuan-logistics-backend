package com.deyuan.client.utils;

import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtil {
    public static byte[] imageToByteArray(String filePath) throws IOException {
            // Read image file
        BufferedImage image = ImageIO.read(new File(filePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos); // 格式需与图片一致（如 PNG、JPG）
        return baos.toByteArray();
    }
    public static BufferedImage byteArrayToImage(byte[] imageData) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new IOException("Could not decode image data");
            }
            return image;
        }
    }
    public static void saveByteArrayAsImage(byte[] imageData, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(imageData);
        }
    }
//    public static void main(String[] args) {
//        try {
//            byte[] imageBytes = imageToByteArray("assets/img/spiderman.jpg"); // 修改为你的图片路径
//            System.out.println("Image converted to byte array successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
