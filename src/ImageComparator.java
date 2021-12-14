import java.awt.image.BufferedImage;

public class ImageComparator {
    private BufferedImage img1;
    private BufferedImage img2;
    public ImageComparator(BufferedImage img1, BufferedImage img2){
        if(img1.getHeight() != img2.getHeight() || img1.getWidth() != img2.getWidth()){
            System.out.println("Image dimensions incompatible");
        }else {
            this.img1 = img1;
            this.img2 = img2;
        }
    }

    public double compareImg(){
        int samePixels = 0;
        for(int y = 0; y < img1.getHeight(); y++){
            for(int x = 0; x < img1.getWidth(); x++){
                if(img1.getRGB(x, y) == img2.getRGB(x, y)){
                    samePixels++;
                }
            }
        }
        return (double)samePixels / (img1.getWidth() * img1.getHeight());
    }
}
