import java.util.*;
public class Bill {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("---Without GST ---");
        float pencil = in.nextFloat();
        System.out.println("Pencil cost :"+pencil);
        float pen = in.nextFloat();
        System.out.println("Pen cost :"+pen);
        float eraser = in.nextFloat();
        System.out.println("Eraser cost :"+eraser);

        float total = pencil + pen + eraser;
        System.out.println("Total cost without Gst:"+totawwl+"\n");
        System.out.println("---With GST(18%)---");
        float newtotal = total + (0.18f * total);
        
        System.out.println("Total cost With Gst:"+newtotal);
    }
}
