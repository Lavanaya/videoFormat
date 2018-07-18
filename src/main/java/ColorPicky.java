import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.File;

public class ColorPicky {

    public static int red = 0, green = 0, blue = 0;

    public static void main(String[] args) throws Exception {
        new ColorPicky();
    }

    public ColorPicky() throws Exception {
        EventQueue.invokeLater(new Runnable() {
            //  @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                }

                JFrame frame = new JFrame("Color Picker");
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane(red, green, blue));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }


    public class TestPane extends JPanel {

        private BufferedImage img;
        private JLabel label;

        private JPanel fields;
        private JTextField red;
        private JTextField green;
        private JTextField blue;
        private JButton button;
        private JButton chooseBckgrnd;

        public TestPane(int redC, int greenC, int blueC) {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            try {
                VideoCapture capture = new VideoCapture(0);
                Mat camImage = new Mat();
                if (capture.isOpened()) {
                    capture.read(camImage);
                    img = ReadFrameProducer.Mat2BufferedImage(camImage);
                    setLayout(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    label = new JLabel();
                    try {
                        //     img = ImageIO.read(new File("C:\\hold\\thumbnails\\issue459.jpg"));
                        label.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    add(label, gbc);

                    fields = new JPanel();
                    fields.setBorder(new EmptyBorder(5, 5, 5, 5));
                    red = new JTextField(3);
                    green = new JTextField(3);
                    blue = new JTextField(3);
                    button = new JButton("Submit");
                    chooseBckgrnd = new JButton(("Select Background File"));
                    fields.add(red);
                    fields.add(green);
                    fields.add(blue);
                    fields.add(button);
                    fields.add(chooseBckgrnd);
                    add(fields, gbc);


                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // super.mouseClicked(e);

                            int packedInt = img.getRGB(e.getX(), e.getY());
                            Color color = new Color(packedInt, true);
                            fields.setBackground(color);
                            red.setText(Integer.toString(color.getRed()));
                            green.setText(Integer.toString(color.getGreen()));
                            blue.setText(Integer.toString(color.getBlue()));

                        }
                    });

                    button.addMouseListener(
                            new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    ColorPicky.red = Integer.valueOf(red.getText());
                                    ColorPicky.green = Integer.valueOf(green.getText());
                                    ColorPicky.blue = Integer.valueOf(blue.getText());
                                    Main.startProducerConsumer();

                                }
                            }
                    );

                    chooseBckgrnd.addMouseListener(
                            new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    JFileChooser fileChooser = new JFileChooser();
                                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                                    int result = fileChooser.showOpenDialog(fields);
                                    if (result == JFileChooser.APPROVE_OPTION) {
                                        File selectedFile = fileChooser.getSelectedFile();
                                        ChromaKey.CleanFileByName("output/backgroundFrames");
                                        try {
                                            Utils.ImgFromVideo(selectedFile.getAbsolutePath());
                                        } catch (Exception ex) {
                                            ex.getStackTrace();
                                        }
                                        //  Main.startProducerConsumer();
                                        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                                    }
                                }
                            }
                    );
          /*  label.addMouseMotionListener(new MouseAdapter() {
               // @Override
                public void mouseMoved(MouseEvent e) {
                    int packedInt = img.getRGB(e.getX(), e.getY());
                    Color color = new Color(packedInt, true);
                    fields.setBackground(color);
                    red.setText(Integer.toString(color.getRed()));
                    green.setText(Integer.toString(color.getGreen()));
                    blue.setText(Integer.toString(color.getBlue()));
                }
            });*/


                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}