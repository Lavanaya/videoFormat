import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FramesConsumer implements Runnable {

    protected BlockingQueue queue = null;
    protected LinkedBlockingQueue<byte[]> publisherQueue = null;
    public static boolean isPlayEnabled = false;


    public FramesConsumer(BlockingQueue queue, LinkedBlockingQueue publisherQueue) {
        this.queue = queue;
        this.publisherQueue = publisherQueue;
        System.out.println(queue);
    }

    public void run() {
        List<String> bckGrndFile = Utils.getBackgroundFilesList("output/backgroundFrames");

        JFrame frame0 = new JFrame();
        JPanel fields = new JPanel();
        frame0.add(fields);

        JButton play = new JButton("Play");
        JButton stop = new JButton("Stop");
        fields.add(play);
        fields.add(stop);

        frame0.getContentPane().setLayout(new FlowLayout());
        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame0.setSize(img.getWidth(), img.getHeight() + 30);
        frame0.setLocation(50, 50);
        JLabel jlabel = null;
        int y = 0;

        stop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPlayEnabled = false;
            }
        });

        play.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPlayEnabled = true;
            }
        });


        while (true) {
            try {
                // if(queue.size()>0){
                ReplaceBgModel replaceBgModel = (ReplaceBgModel) queue.take();
                   BufferedImage output = ChromaKey.getInstance().replaceBackground(replaceBgModel.getImg(), replaceBgModel.getRed(),
                          replaceBgModel.getGreen(), replaceBgModel.getBlue(), replaceBgModel.getFileName());

               // BufferedImage output = new Gaussian().removeBgByNearestNeighbours(replaceBgModel.getImg(), replaceBgModel.getRed(),
               //         replaceBgModel.getGreen(), replaceBgModel.getBlue());
                /*
                Utils.save2file1(output);
                publisherQueue.add(Img2byteArray(output));
               */

                frame0.setSize(output.getWidth(), output.getHeight() + 50);
                //  frame0.setSize(bf2.getWidth(),bf2.getHeight()+30);

                if (y != 0) {
                    frame0.getContentPane().remove(jlabel);
                }
                jlabel = new JLabel(new ImageIcon(output));
                y++;
                frame0.getContentPane().add(jlabel);

                frame0.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static byte[] Img2byteArray(BufferedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }
}
