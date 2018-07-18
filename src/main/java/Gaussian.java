
import javax.sound.midi.Soundbank;
import java.awt.image.BufferedImage;

public class Gaussian {

    public static int[][] frameRGBVals = null;
    public static final int globalThreshold = 250;
    static BufferedImage bimg, bfg;

    private int[] findClosestPixel(BufferedImage img, int red, int green, int blue) {
        int picW = img.getWidth();
        int picH = img.getHeight();
        int red2, green2, blue2, distance, closestPixX = -1, closestPixY = -1;
        frameRGBVals = new int[picW][picH];

        int closestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < picW; i++) {
            for (int j = 0; j < picH; j++) {

                int cur_pix = img.getRGB(i, j);
                frameRGBVals[i][j] = cur_pix;

                red2 = ((cur_pix & 0x00FF0000) >>> 16); // Color.red(cur_pix);
                green2 = ((cur_pix & 0x0000FF00) >>> 8); //Color.green(cur_pix);
                blue2 = (cur_pix & 0x000000FF); //

                distance = (int) Math.sqrt((red - red2) * (red - red2) + (green - green2) * (green - green2) + (blue - blue2) * (blue - blue2));
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPixX = i;
                    closestPixY = j;
                }
            }
        }
        System.out.println("Closest Distance" + closestDistance);
        return new int[]{closestPixX, closestPixY, closestDistance};
    }

    /*public BufferedImage removeBgByNearestNeighbours(BufferedImage img,int red,int green,int blue) throws Exception {
        this.bimg = img;
        img = Utils.resizeImage(300,300, img);
        int picw=img.getWidth(), pich = img.getHeight();
        int k=50;
        for (int i = 0; i < picw - k + 1; i = i + k) {
            for (int j = 0; j < pich - k + 1; j = j + k) {

                bfg = img.getSubimage(i,j,k,k);
                boolean visited[][] = new boolean[bfg.getWidth()][bfg.getHeight()];
                int[] localThresholdXY = findClosestPixel(bfg,red,green,blue);
                int localThX= localThresholdXY[0],localThY=localThresholdXY[1];


        if(localThX >-1 && localThY >-1 && !visited[localThX][localThY]){
           bfg=DFS(localThX,localThY,visited,red,green,blue,localThresholdXY[2],bfg);
        }
                for (int p = i,i1=0; p < k + i && i1<k; p++,i1++) {
                    for (int q = j,j1=0; q < k + j&& q<k; q++,j1++) {
                        {
                            System.out.println(p+"                          "+q);
                            bimg.setRGB(p, q, bfg.getRGB(i1, j1));

                        }
                    }

                }
               // break;
        }
       // break;
        }
        return bimg;
    }*/


    public BufferedImage removeBgByNearestNeighbours(BufferedImage img, int red, int green, int blue) throws Exception {
        this.bimg = img;
        img = Utils.resizeImage(300, 300, img);
        int picw = img.getWidth(), pich = img.getHeight();
        //int k=50;
        bfg = bimg;
        boolean visited[][] = new boolean[bfg.getWidth()][bfg.getHeight()];
        int[] localThresholdXY = findClosestPixel(bfg, red, green, blue);
        int localThX = localThresholdXY[0], localThY = localThresholdXY[1];


        if (localThX > -1 && localThY > -1 && !visited[localThX][localThY]) {
            bfg = DFS(localThX, localThY, visited, red, green, blue, localThresholdXY[2], bfg);
        }
        return bfg;
    }


    BufferedImage DFS(int row, int col, boolean visited[][], int red, int green, int blue, int localThreshold, BufferedImage bfg) {
        int cur_pix = frameRGBVals[row][col];
        int red2, green2, blue2;

        visited[row][col] = true;
        // These arrays are used to get row and column numbers
        // of 8 neighbors of a given cell
        int rowNbr[] = new int[]{-1, -1, -1, 0, 0, 1, 1, 1};
        int colNbr[] = new int[]{-1, 0, 1, -1, 1, -1, 0, 1};

        red2 = ((cur_pix & 0x00FF0000) >>> 16); // Color.red(cur_pix);
        green2 = ((cur_pix & 0x0000FF00) >>> 8); //Color.green(cur_pix);
        blue2 = (cur_pix & 0x000000FF); //

        int distance = (int) Math.sqrt((red - red2) * (red - red2) + (green - green2) * (green - green2) + (blue - blue2) * (blue - blue2));
        if (Math.abs(distance - localThreshold) < 200 && localThreshold < globalThreshold && distance < globalThreshold) {
            localThreshold = distance;
            int argb = bimg.getRGB(row, col); //always returns TYPE_INT_ARGB
            int alpha = (argb >> 24) & 0xff;  //isolate alpha

            alpha *= 255;
            alpha &= 0xff;      //keeps alpha in 0-255 range

            argb &= 0x00ffffff; //remove old alpha info
            argb |= (alpha << 24);  //add new alpha info
            bfg.setRGB(row, col, 9813286);

        }

        // Recur for all connected neighbours
        for (int k = 0; k < 8; k++)
            if (isSafe(row + rowNbr[k], col + colNbr[k], visited))
                DFS(row + rowNbr[k], col + colNbr[k], visited, red, green, blue, localThreshold, bfg);
        return bfg;
    }

    boolean isSafe(int row, int col,
                   boolean visited[][]) {
        // row number is in range, column number is in range
        // and value is 1 and not yet visited
        return (row >= 0) && (row < frameRGBVals.length) &&
                (col >= 0) && (col < frameRGBVals[0].length) &&
                (visited[row][col] == false);
    }
}
