import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {
    static int n = 0;

    public static void save2file(BufferedImage img) throws IOException {
        File outputfile = new File("output/frames/" + System.currentTimeMillis() + ".png");
        ImageIO.write(img, "png", outputfile);
        n++;
    }

    public static void save2file1(BufferedImage img) throws IOException {
        File outputfile = new File("output/outputFrames/" + System.currentTimeMillis() + ".png");
        ImageIO.write(img, "png", outputfile);
    }

    public static void save2file3(BufferedImage img) throws IOException {
        File outputfile = new File("output/backgroundFrames/" + System.currentTimeMillis() + ".png");
        ImageIO.write(img, "png", outputfile);
    }

    public static BufferedImage resizeImage(int scaledHeight, int scaledWidth, BufferedImage inputImage) throws Exception {
        // System.out.println("Height:"+inputImage.getHeight()+"  width:"+inputImage.getWidth());
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        // File outputfile = new File("output/outputFrames/Resized.png");
        // ImageIO.write(outputImage, "png", outputfile);
        return outputImage;
    }


    public static List<BufferedImage> ImgFromVideo(String filepath) throws Exception {

        Mat frame = new Mat();
        List<BufferedImage> vidList = new ArrayList<BufferedImage>();
        VideoCapture camera = new VideoCapture(filepath);

        // VideoCapture camera = new VideoCapture("/Users/ldixit/Downloads/ButterflyBackground.mp4");

        BufferedImage img;
        int n = 0;
        while (true) {
            if (camera.read(frame)) {
                img = ReadFrameProducer.Mat2BufferedImage(frame);
                img = Utils.resizeImage(305, 535, img);
                //   vidList.add(img);
                save2file3(img);
                //  System.out.println(frame);
                n++;

                // System.out.println(camera);
                if (camera.read(frame) == false || n == 200) {
                    break;

                }
            } else if (frame.toString().contains("0*0*")) {
                break;
            }
        }

        System.out.println("Exit from loop");
        //System.exit(0);
        return vidList;


    }

    public static List<String> getBackgroundFilesList(String folderName) {

        File fileDir = new File(folderName);

        if (fileDir.isDirectory()) {
            List listFile = Arrays.asList(fileDir.list());
            System.out.println("Listing files unsorted");
            Collections.sort(listFile);
            return listFile;

        }
        return null;

    }
}
