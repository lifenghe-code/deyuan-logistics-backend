package com.deyuan.client.utils;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtil {
    public static byte[] imageToByteArray(String filePath) throws IOException {

        try {
            // Read image file
            File imageFile = new File(filePath);
            if (!imageFile.exists()) {
                throw new IOException("Image file does not exist: " + filePath);
            }

            BufferedImage image = Imaging.getBufferedImage(imageFile);
            if (image == null) {
                throw new IOException("Image could not be read or is corrupted");
            }

            // Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (!ImageIO.write(image, "jpg", baos)) {  // Can change to "png" if needed
                throw new IOException("No appropriate writer found for image format");
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IOException("Error processing image file: " + filePath, e);
        } catch (ImageReadException e) {
            throw new RuntimeException(e);
        } finally {
            // No need to close File object as it's not a stream
            // ByteArrayOutputStream doesn't need to be closed
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
