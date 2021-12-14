import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EdgeDetection {
    public static void main(String[] args) throws IOException {
        BufferedImage img = ImageIO.read(EdgeDetection.class.getResource("img.jpg"));
        //BufferedImage newImg = getGreyscale(img);
        BufferedImage edges = sobelEdgeDetect(getGreyscaleArray(img), 100, 1020);
        try{
            File output = new File("egdes.jpg");
            ImageIO.write(edges, "jpg", output);
        }catch(IOException e){
            System.out.println("Unable to write to file");
            System.exit(3);
        }
    }
    public static int[][] padZeros(int[][]arr){
        int[][] temp = new int[arr.length + 1][arr[0].length + 1];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                temp[i][j] = 0;
            }
        }
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                temp[i+1][j+1] = arr[i][j];
            }
        }
        return temp;
    }

   public static BufferedImage sobelEdgeDetect(int[][] in, int loThreshold, int hiThreshold){
        int[][]grey = padZeros(in);
        BufferedImage edgeImg = new BufferedImage(in[0].length, in.length, BufferedImage.TYPE_BYTE_GRAY);

        for(int y = 1; y < grey.length-1; y++){
            for(int x = 1; x < grey[0].length-1; x++){
                int sobelXSum = 0;
                int sobelYSum = 0;
                sobelXSum += -1 * (grey[y-1][x-1] + (2 * grey[y][x-1]) + grey[y+1][x-1]);
                sobelXSum += (grey[y-1][x+1] + (2 * grey[y][x+1]) + grey[y+1][x+1]);

                sobelYSum += -1 * (grey[y-1][x-1] + (2 * grey[y-1][x]) + grey[y-1][x+1]);
                sobelYSum += (grey[y+1][x-1] + (2 * grey[y+1][x]) + grey[y+1][x+1]);

                int sobelVal = (int)Math.sqrt((sobelXSum * sobelXSum)+(sobelYSum * sobelYSum));
                Color edge = new Color(0, 0, 0, 1);
                Color bg = new Color(255, 255, 255, 1);
                System.out.println(sobelVal);
                if(sobelVal >= loThreshold && sobelVal <= hiThreshold) {
                    edgeImg.setRGB(x-1, y-1, edge.getRGB());
                }else{
                    edgeImg.setRGB(x-1, y-1, bg.getRGB());
                }
            }
        }
        return edgeImg;
    }

    public static BufferedImage getGreyscale2(BufferedImage img){
        BufferedImage greyed = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = greyed.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return greyed;
    }

    public static BufferedImage getGreyscale(BufferedImage img){
        BufferedImage greyed = img;
        for(int y = 0; y < img.getHeight(); y++){
            for(int x = 0; x < img.getWidth(); x++){
                Color rgb = new Color(img.getRGB(x, y));
                int red = rgb.getRed();
                int green = rgb.getGreen();
                int blue = rgb.getBlue();
                int alpha = rgb.getAlpha();
                int grey = red/3 + green/3 + blue/3;
                Color newRgb = new Color(grey, grey, grey, alpha);
                greyed.setRGB(x, y, newRgb.getRGB());
            }
        }
        return greyed;
    }

    public static int[][] getGreyscaleArray(BufferedImage img){
        int[][] greyArray = new int[img.getHeight()][img.getWidth()];
        for(int y = 0; y < img.getHeight(); y++){
            for(int x = 0; x < img.getWidth(); x++){
                Color rgb = new Color(img.getRGB(x, y));
                int red = rgb.getRed();
                int green = rgb.getGreen();
                int blue = rgb.getBlue();
                int alpha = rgb.getAlpha();
                int grey = red/3 + green/3 + blue/3;
                greyArray[y][x] = grey;
            }
        }
        return greyArray;
    }
}
