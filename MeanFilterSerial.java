import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.util.*;

public class MeanFilterSerial
{
    BufferedImage image1;
    BufferedImage image2;
    int height;
    int width;

    public MeanFilterSerial(BufferedImage img)
    {
        image1 = img;
        height = img.getHeight();
        width = img.getWidth();
        image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);        
    }

    public BufferedImage filter(int windowWidth)
    {
        long start = System.currentTimeMillis();
        int w = (int)Math.floor(windowWidth/2);
        for(int y=w; y< height-w; y++)
        {
            for (int x=w; x< width-w ;x++)
            {   
                int sumA =0;
                int sumR = 0;
                int sumG =0;
                int sumB = 0;
                for (int i=y-w; i<y+w+1; i++)
                {
                    for (int z=x-w; z< x+w+1; z++)
                    {
                        int p = image1.getRGB(z, i);
                        sumA += (p>>24) & 0xff;
                        sumR += (p>>16) & 0xff;
                        sumG += (p>>8)  & 0xff;
                        sumB += p       & 0xff;
                    }
                }
                int h = windowWidth * windowWidth;
                int p = ( ((sumA/h)<<24) |(sumR/h)<<16) | ((sumG/h)<<8) | sumB/h;
                image2.setRGB(x-w, y-w, p);
            }
        }
        long end = System.currentTimeMillis();
        float answer = (end -start);
        System.out.println("elapsed time is "+ answer);
        return image2;
    }
    public static void main(String[] args)
    {   
        try {
            String inpuString = args[0];
            String OutputString = args[1];
            int windowWidth = Integer.parseInt(args[2]);
            BufferedImage image1 = null;
            image1 = ImageIO.read(new File(inpuString));
            MeanFilterSerial mfs = new MeanFilterSerial(image1);
            BufferedImage img = mfs.filter(windowWidth);
            try {
                ImageIO.write(img, "jpg", new File(OutputString));
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.print("Image not found");
        }
        
    }
}