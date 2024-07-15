package utils;


import functions.ImageOperation;

import java.awt.image.BufferedImage;

public class RgbMaster {

    public RgbMaster(BufferedImage image) {
        this.image = image;
        wigth = image.getWidth();
        heidht = image.getHeight();
        hasAlphaChannel = image.getAlphaRaster()!=null;
        pixels = image.getRGB(0,0, wigth, heidht, null, 0,wigth);
    }
    private     BufferedImage image;
    private int wigth;
    private int heidht;
    private boolean hasAlphaChannel;
    private int[] pixels;

    public BufferedImage getImage(){
        return image;
    }

    public void changeImage(ImageOperation operation) throws Exception {
        for (int i = 0; i < pixels.length; i++) {
            float[] pixel = ImageUtils.rgbIntToArray(pixels[i]);
            float[] newPixel = operation.execute(pixel);
            pixels[i] = ImageUtils.arrayToRgbInt(newPixel);
        }
        image.setRGB(0,0,wigth, heidht, pixels,0,wigth);
    }
}
