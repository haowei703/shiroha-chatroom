package com.shiroha.chatroom.utils;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageAndAudioUtils {

    // 获取图像宽高信息
    public static int[] getImageInfo(byte[] imageBytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IOException("Unable to read image from byte array");
            }
            int width = image.getWidth();
            int height = image.getHeight();

            return new int[]{width, height};
        }catch (IOException e) {
            return new int[2];
        }
    }
}

