package com.deyuan.client.utils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtil {
    public static byte[] imageToByteArray(String filePath) throws IOException {
        ClassLoader classLoader = ImageUtil.class.getClassLoader();

            // Read image file
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("资源未找到: " + filePath);
            }

            // 读取图片并转换为字节数组
            BufferedImage image = ImageIO.read(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
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
