
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class ReadFrameProducer implements Runnable {

    protected BlockingQueue queue = null;

    static int n = 0, red, green, blue;

    public ReadFrameProducer(BlockingQueue queue, int red, int green, int blue) {
        this.queue = queue;
        this.red = red;
        this.green = green;
        this.blue = blue;

        System.out.println(red + " ," + green + " ," + blue);
        // System.out.println(queue);
    }


    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture capture = new VideoCapture(0);

        Mat camImage = new Mat();
        if (capture.isOpened()) {
            long startTime = System.currentTimeMillis();
            while (true && (System.currentTimeMillis() - startTime) < 10000) {

                capture.read(camImage);

                BufferedImage img = Mat2BufferedImage(camImage);
                Utils.save2file(img);
                ChromaKey c = ChromaKey.getInstance();
                //  BufferedImage img2=c.replaceBgColor(img,228,217,187,"/Users/ldixit/Downloads/231822-background-image.jpg");
                BufferedImage img2 = c.replaceBackground(img, 201, 181, 130, "/Users/ldixit/Downloads/231822-background-image.jpg");
                save2file1(img2);

            }
        }
    }

    public static BufferedImage mat2Img(Mat in) {

        BufferedImage out;
        byte[] data = new byte[320 * 240 * (int) in.elemSize()];
        int type;
        in.get(0, 0, data);

        if (in.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;

        out = new BufferedImage(320, 240, type);

        out.getRaster().setDataElements(0, 0, 320, 240, data);
        return out;
    }

    static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {

        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));

        return bi;
    }



/*	private BufferedImage createImage(int[][] pixelData, BufferedImage outputImage)
    {
        final int[] outputImagePixelData = ((DataBufferInt) outputImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelData, 0, outputImagePixelData, 0, pixelData.length);
        return outputImage;
    }*/

    private static void save2file1(BufferedImage img) throws IOException {
        File outputfile = new File("output/outputFrames/" + n + ".png");
        ImageIO.write(img, "png", outputfile);
        n++;
    }

    public void run() {
        JFrame frame0 = new JFrame();
        frame0.getContentPane().setLayout(new FlowLayout());
        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame0.setSize(img.getWidth(), img.getHeight() + 30);
        frame0.setLocation(10, 10);
        JLabel jlabel = null;
        int y = 0;

        try {

            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            VideoCapture capture = new VideoCapture(0);
            // Utils.ImgFromVideo();
            List<String> bckGrndFile = Utils.getBackgroundFilesList("output/backgroundFrames");
            Mat camImage = new Mat();
            if (capture.isOpened()) {

                long startTime = System.currentTimeMillis();
                while (true) {
                    capture.read(camImage);
                    BufferedImage img = Mat2BufferedImage(camImage);
                    // Utils.save2file(img);
                    img = Utils.resizeImage(300, 530, img);

                    ReplaceBgModel replaceBgModel = new ReplaceBgModel();
                    replaceBgModel.setImg(img);

                    replaceBgModel.setRed(red);
                    replaceBgModel.setBlue(blue);
                    replaceBgModel.setGreen(green);

                    // replaceBgModel.setFileName("/Users/ldixit/Downloads/batman.jpg");
                    if (bckGrndFile.get(n).contains(".DS_Store")) n++;
                    if (n == 198 && FramesConsumer.isPlayEnabled) n = 0;

                    replaceBgModel.setFileName("output/backgroundFrames/" + bckGrndFile.get(n));
                    if (FramesConsumer.isPlayEnabled) n++;
                  //  if (n == 198 && FramesConsumer.isPlayEnabled) n = 0;
                    queue.put(replaceBgModel);
               /*     frame0.setSize(550, 550 + 30);
                    img = Utils.resizeImage(550,550, img);

                    if(y!=0) {
                        frame0.getContentPane().remove(jlabel);
                    }
                    jlabel=new JLabel(new ImageIcon(img));
                    y++;
                    frame0.getContentPane().add(jlabel);


                    frame0.setVisible(true);
               */
                    System.out.println("Queue size Producer:" + queue.size());
                    Thread.sleep(10);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


