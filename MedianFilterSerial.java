import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.util.*;

public class MedianFilterSerial
{
    BufferedImage image1;
    BufferedImage image2;
    int height;
    int width;

    public MedianFilterSerial(BufferedImage img)
    {
        image1 = img;
        height = img.getHeight();
        width = img.getWidth();
        image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    public BufferedImage filter(int windowWidth)
    {
        int w = (int)Math.floor(windowWidth/2);
        for(int y=w; y< height-(w); y++)
            {
                for (int x=w; x< width-(w) ;x++)
                {   
                    ArrayList<Integer> a = new ArrayList<Integer>();
                    ArrayList<Integer> r = new ArrayList<Integer>();
                    ArrayList<Integer> g = new ArrayList<Integer>();
                    ArrayList<Integer> b = new ArrayList<Integer>();
                    for (int i=y-w; i<y+(w+1); i++)
                    {
                        for (int z=x-(w); z< x+(w+1); z++)
                        {   
                            int p = image1.getRGB(z, i);            
                            a.add( (p>>24) & 0xff);
                            r.add((p>>16) & 0xff);
                            g.add((p>>8)  & 0xff);
                            b.add(p       & 0xff);
                        }
                    }
                    Collections.sort(a);
                    Collections.sort(r);
                    Collections.sort(g);
                    Collections.sort(b);
                    int m = (windowWidth*windowWidth)/2 -1/2;
                    int p =  (a.get(m)<<24) |(r.get(m)<<16) | (g.get(m)<<8) | b.get(m);
                    image2.setRGB(x-w, y-w, p);
                }
            }
            return image2;
    }    
    public static void main(String[] args)
    {
        BufferedImage image1 = null;
        try {
            String inpuString = args[0];
            String OutputString = args[1];
            int windowWidth = Integer.parseInt(args[2]);   
            
            image1 = ImageIO.read(new File(inpuString));
            MedianFilterSerial mfs = new MedianFilterSerial(image1);
            long start = System.currentTimeMillis();
            BufferedImage img = mfs.filter(windowWidth);
            long end = System.currentTimeMillis();
            float answer = (end -start);
            System.out.println("elapsed time is "+ answer);
            try {
                ImageIO.write(img, "jpg", new File(OutputString));
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println("Invalid Input");
        }
        
    }
}