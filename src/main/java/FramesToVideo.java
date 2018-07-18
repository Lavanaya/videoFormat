import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.awt.Graphics;


public class FramesToVideo{

    public static void main(String[] args) throws java.io.IOException {
        File file = new File("output/outputFrames/");
        File[] files = file.listFiles();
        JFrame canvas = new JFrame();
        System.out.println(files.length);
        for(File fileName:files){

        BufferedImage cat = ImageIO.read( fileName);

        canvas.setSize(cat.getWidth()+100,cat.getHeight()+100);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setTitle("It's a cat.");
        Container pane = canvas.getContentPane();
        ColorPanel panel = new ColorPanel(cat);
        canvas.setVisible(true);
        pane.add(panel);
        }
    }
}

class ColorPanel extends JPanel{
    BufferedImage theCat;
    public ColorPanel(BufferedImage image){
        theCat = image;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(theCat, null, 50,50);
    }
}
