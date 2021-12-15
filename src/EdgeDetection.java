import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class EdgeDetection {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    public static void main(String[] args) throws IOException {
        String file = "ping.png";
        System.out.println("Welcome to OpenCV " + Core.VERSION);

        BufferedImage img = ImageIO.read(EdgeDetection.class.getResource(file));

        BufferedImage greyArray = greyArrayToImg(getGreyscaleArray(img));
        BufferedImage edges = sobelEdgeDetect2(getGreyscaleArray(img));
        BufferedImage edges1 = sobelEdgeDetect(getGreyscaleArray(img), 10, 1020);
        try{
            File out = new File("greyArray.jpg");
            File output = new File("egdes.jpg");
            File output1 = new File("thresholded.jpg");
            ImageIO.write(edges, "jpg", output);
            ImageIO.write(edges1, "jpg", output1);
            ImageIO.write(getGreyscale(img), "jpg", out);
        }catch(IOException e){
            System.out.println("Unable to write to file");
            System.exit(3);
        }

        //OpenCV
        String outfile = "opencvedges.jpg";
        Mat m = openCVSobel("C:\\Users\\steph\\Desktop\\Proj\\edge-detection\\src\\ping.png", outfile);
        
        //Image compare
        ImageComparator ic = new ImageComparator(edges, edges1);
        System.out.println("Similarity: " + ic.compareImg());

    }
    public static BufferedImage greyArrayToImg(int[][]arr){
        BufferedImage img = new BufferedImage(arr[0].length, arr.length, BufferedImage.TYPE_BYTE_GRAY);
        for(int y = 0; y < arr.length; y++){
            for(int x = 0; x < arr[0].length; x++){
                Color c = new Color(arr[y][x],arr[y][x], arr[y][x], 1);
                img.setRGB(x, y, c.getRGB());
            }
        }
        return img;
    }
    public static BufferedImage toBufferedImage(Mat m) {
        if (!m.empty()) {
            int type = BufferedImage.TYPE_BYTE_GRAY;
            if (m.channels() > 1) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            int bufferSize = m.channels() * m.cols() * m.rows();
            byte[] b = new byte[bufferSize];
            m.get(0, 0, b); // get all the pixels
            BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(b, 0, targetPixels, 0, b.length);
            return image;
        }

        return null;
    }

    public static Mat openCVSobel(String infile, String outfile){
        Mat srcGray = new Mat();
        Mat src = Imgcodecs.imread(infile);
        if(src.empty()){
            System.out.println("empty img");
            System.exit(-1);
        }
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_RGB2GRAY);
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Mat grad = new Mat();
        int ddepth = CvType.CV_16S;
        int scale = 1;
        int delta = 0;
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
        //Imgproc.Scharr( src_gray, grad_x, ddepth, 1, 0, scale, delta, Core.BORDER_DEFAULT );
        Imgproc.Sobel( srcGray, gradX, ddepth, 1, 0, 3, scale, delta, Core.BORDER_DEFAULT );
        //Imgproc.Scharr( src_gray, grad_y, ddepth, 0, 1, scale, delta, Core.BORDER_DEFAULT );
        Imgproc.Sobel( srcGray, gradY, ddepth, 0, 1, 3, scale, delta, Core.BORDER_DEFAULT );

        Core.convertScaleAbs( gradX, abs_grad_x );
        Core.convertScaleAbs( gradY, abs_grad_y );
        Core.addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );

        Imgcodecs.imwrite(outfile, grad);
        return grad;
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

    public static BufferedImage sobelEdgeDetect2(int[][] in){
        int[][]grey = padZeros(in);
        BufferedImage edgeImg = new BufferedImage(in[0].length, in.length, BufferedImage.TYPE_BYTE_GRAY);
        int[][]sobels = new int[in.length][in[0].length];
        int maxSobel = 0;
        for(int y = 1; y < grey.length-1; y++){
            for(int x = 1; x < grey[0].length-1; x++){
                int sobelXSum = 0;
                int sobelYSum = 0;
                sobelXSum += -1 * (grey[y-1][x-1] + (2 * grey[y][x-1]) + grey[y+1][x-1]);
                sobelXSum += (grey[y-1][x+1] + (2 * grey[y][x+1]) + grey[y+1][x+1]);

                sobelYSum += -1 * (grey[y-1][x-1] + (2 * grey[y-1][x]) + grey[y-1][x+1]);
                sobelYSum += (grey[y+1][x-1] + (2 * grey[y+1][x]) + grey[y+1][x+1]);

                int sobelVal = (int)Math.sqrt((sobelXSum * sobelXSum)+(sobelYSum * sobelYSum));
                System.out.println("X: " + sobelXSum + "       Y: " + sobelYSum + "     val: " + sobelVal);
                maxSobel = Math.max(maxSobel, sobelVal);
                sobels[y-1][x-1] = sobelVal;
            }
        }
        double tr = 255.0/maxSobel;
        for(int y = 0; y < sobels.length; y++){
            for(int x = 0; x < sobels[0].length; x++){
                int pixelColor = (int)(sobels[y][x] * tr);
                Color c = new Color(pixelColor, pixelColor, pixelColor, 1);
                edgeImg.setRGB(x, y, c.getRGB());
            }
        }
        return edgeImg;
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
                sobelVal = sobelVal/4;
                Color bg = new Color(0, 0, 0, 1);
                Color edge = new Color(255, 255, 255, 1);

                if(sobelVal >= loThreshold && sobelVal <= hiThreshold) {
                    edgeImg.setRGB(x-1, y-1, edge.getRGB());
                }else{
                    edgeImg.setRGB(x-1, y-1, bg.getRGB());
                }
            }
        }
        return edgeImg;
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
                int grey = red/3 + green/3 + blue/3;
                greyArray[y][x] = grey;
            }
        }
        return greyArray;
    }
}
