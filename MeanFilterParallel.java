import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.*;



public class MeanFilterParallel extends RecursiveTask <BufferedImage>
{
    static int SEQUENTIAL_THRESHOLD = 100000;
    int width;
    int height;
    BufferedImage img1;
    BufferedImage img2;
    BufferedImage img3;
    int windowWidth;
    int w;

    public MeanFilterParallel (BufferedImage img, int x)
    {
        img1 = img;
        width = img.getWidth();
        height = img.getHeight();
        img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        windowWidth = x;
        w = (int)Math.floor(windowWidth/2);
    }

    public BufferedImage compute()
    {
        long start = System.currentTimeMillis();
        if (width * height <= SEQUENTIAL_THRESHOLD)
        {
            
            for(int y=w; y< height-(w); y++)
            {
                for (int x=w; x< width-(w) ;x++)
                {   
                    int sumA =0;
                    int sumR = 0;
                    int sumG =0;
                    int sumB = 0;
                    for (int i=y-w; i<y+(w+1); i++)
                    {
                        for (int z=x-(w); z< x+(w+1); z++)
                        {   
                            int p = img1.getRGB(z, i);
                            sumA += (p>>24) & 0xff;
                            sumR += (p>>16) & 0xff;
                            sumG += (p>>8)  & 0xff;
                            sumB += p       & 0xff;
                        }
                    }
                    int h = windowWidth * windowWidth;
                    int meanA = sumA/h; 
                    int meanR = sumR/h;
                    int meanG = sumG/h;
                    int meanB = sumB/h;
                    int m = (windowWidth*windowWidth)/2 -1/2;
                    int p =  (meanA<<24) |(meanR<<16) | (meanG<<8) | meanB;
                    img2.setRGB(x-w, y-w, p);
                }
            }

            return img2;
        }
        else 
        {   
            int m = 2*w-1;
            MedianFilterParallel leftTop  = new MedianFilterParallel(img1.getSubimage(0,0, (int)Math.floor(width/2), height), windowWidth);
            MedianFilterParallel rightTop = new MedianFilterParallel(img1.getSubimage((int)Math.ceil(width/2)-windowWidth, 0, width - (int)Math.ceil(width/2)+ windowWidth, height), windowWidth); //5
            leftTop.fork();
            BufferedImage r = rightTop.compute();
            leftTop.join();
            img3 = new BufferedImage((int)Math.floor(width/2) +(width - (int)Math.ceil(width/2)) ,(int)Math.floor(height/2)+(height -(int)Math.ceil(height/2)),BufferedImage.TYPE_INT_RGB);
            Graphics g = img3.getGraphics();
            img2 = img3;
            g.drawImage(leftTop.img2  , 0, 0, null);
            g.drawImage(r , leftTop.img2.getWidth()-windowWidth+1, 0, null);  //4       
            g.dispose();
            img2 = img3;
            long end = System.currentTimeMillis();
            float answer = (end -start);
            System.out.println("elapsed time is "+ answer);
            return img3;
        }
    }

    public static void main(String[] args) {

        BufferedImage image1 = null;
        try {
            image1 = ImageIO.read(new File("noisy.png"));
        } catch (Exception e) {
            System.out.print("Image not found");
        }
        Run x = new Run();
        BufferedImage img1 = x.mean(image1, 5);
        try {
            ImageIO.write(img1, "jpg", new File("OutputMeanParallel.jpg"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
class Run{
    static BufferedImage mean(BufferedImage img, int x)
    {
        MeanFilterParallel mfp = new MeanFilterParallel(img, x);
        ForkJoinPool.commonPool().invoke(mfp);
        return mfp.img3;
    }
}
