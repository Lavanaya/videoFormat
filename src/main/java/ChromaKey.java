
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ChromaKey {
    int[] pix;
    int picw, pich;

    public static final ChromaKey getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static final ChromaKey INSTANCE = new ChromaKey();
    }

    public BufferedImage replaceBackground(BufferedImage bitmap, int red, int green, int blue, String filename) throws Exception{
        //bitmap = Utils.resizeImage(490,490, bitmap);

        ColoSpaceConvertor coloSpaceConvertor = ColoSpaceConvertor.getInstance();
        float[] lsb1 = coloSpaceConvertor.fromRGB(new float[]{red, green, blue});
        return ChromaKey.getInstance().replaceBgColor(bitmap, lsb1, filename);
    }

    public BufferedImage replaceBgColor(BufferedImage bitmap, float[] lsb1, String filename) {

        BufferedImage bgImage = null, resultImage = null;
        ColoSpaceConvertor coloSpaceConvertor = ColoSpaceConvertor.getInstance();

        if (filename == null) throw new IllegalArgumentException("constructor argument is null");

        try {
            // try to read from file in working directory
            File file = new File(filename);
            if (file.isFile()) {
                bgImage = ImageIO.read(file);

            }

            if (bgImage == null) {
                throw new IllegalArgumentException("could not read image file: " + filename);
            }

        } catch (IOException ioe) {
            throw new IllegalArgumentException("could not open image file: " + filename, ioe);
        }
        if (bitmap != null && bgImage != null) {
            picw = bitmap.getWidth();
            pich = bitmap.getHeight();
            if (pix == null) {
                pix = new int[picw * pich];
            }
            double distance;

            if (resultImage == null) {
                resultImage = new BufferedImage(picw, pich, BufferedImage.TYPE_INT_RGB);
            }

            //y->col->X

           /* Color space comparison using LSB and per pixel
           for (int y = 0; y < picw; y++) {
                for (int x = 0; x < pich; x++) {
                    index = y * pich + x;
                    int cur_pix = bitmap.getRGB(y, x);

                    red2 = (int) ((cur_pix & 0x00FF0000) >>> 16); // Color.red(cur_pix);
                    green2 = (int) ((cur_pix & 0x0000FF00) >>> 8); //Color.green(cur_pix);
                    blue2 = (int) (cur_pix & 0x000000FF); //

                    float[] lsb2 = coloSpaceConvertor.fromRGB(new float[]{red2, green2, blue2});
                    distance = Math.sqrt((lsb1[0] - lsb2[0]) * (lsb1[0] - lsb2[0]) + (lsb1[1] - lsb2[1]) * (lsb1[1] - lsb2[1]) + (lsb1[2] - lsb2[2]) * (lsb1[2] - lsb2[2]));

                    if (distance < 0.1f) {
                        resultImage.setRGB(y, x, bgImage.getRGB(x, y));
                    } else {
                        resultImage.setRGB(y, x, cur_pix);
                    }
                }
            }*/

            int redAvg, blueAvg, greenAvg;
            //color space comparison  using matrix of 2*1
            for (int i = 0; i < picw - 2 + 1; i = i + 2) {
                // column of first cell in current sub-square of size k x k
                for (int j = 0; j < pich - 2 + 1; j = j + 2) {
                    // Calculate average rgb sum of current sub-square
                    redAvg = 0;
                    greenAvg = 0;
                    blueAvg = 0;
                    for (int p = i; p < 2 + i; p++) {
                        for (int q = j; q < 2 + j; q++) {
                            // sum += mat[p][q];
                            int rgb = bitmap.getRGB(p, q);
                            redAvg += (int) ((rgb & 0x00FF0000) >>> 16);
                            greenAvg += (int) ((rgb & 0x0000FF00) >>> 8);
                            blueAvg += (int) (rgb & 0x000000FF); //
                        }
                    }
                    //take average of all three channels
                    redAvg /= 4;
                    greenAvg /= 4;
                    blueAvg /= 4;
                    float[] lsb2 = coloSpaceConvertor.fromRGB(new float[]{redAvg, greenAvg, blueAvg});
                    distance = Math.sqrt((lsb1[0] - lsb2[0]) * (lsb1[0] - lsb2[0]) + (lsb1[1] - lsb2[1]) * (lsb1[1] - lsb2[1]) + (lsb1[2] - lsb2[2]) * (lsb1[2] - lsb2[2]));

                    //set corresponding values in resultImage
                    for (int p = i; p < 2 + i; p++) {
                        for (int q = j; q < 2 + j; q++) {
                            if (distance < 0.06f) {
                             //   System.out.println(p);
                             //   System.out.println(q);
                                resultImage.setRGB(p, q, bgImage.getRGB(p, q));
                            } else {
                                resultImage.setRGB(p, q, bitmap.getRGB(p, q));
                            }
                        }
                    }

                }

            }

          //  CleanFileByName("output/backgroundFrames",filename.split("/")[2]);
            return resultImage;
        }
        return null;
    }


    public static void CleanFileByName(String Filepath)
    {
        File f=new File(Filepath);

        for(File file: f.listFiles())
        {if (!file.isDirectory() )
            file.delete();}


    }
}
