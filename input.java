import java.util.*;
public class input {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        //nextInt is Integer scanner class.
        int num = in.nextInt();
        System.out.println("Integer Scanner class : "+num);
        //nextFloat is Float scanner class.
        float num2 = in.nextFloat();
        System.out.println("Floating value : "+num2);
        //nextDouble is Double scanner class.
        double num3 = in.nextDouble();
        System.out.println("Double value : "+num3);
        //next is string scanner class.
        String str = in.next();
        System.out.println("String value : "+str);
        //next.charAt(0) is Character Scanner class.
        char c = in.next.charAt(0);
        System.out.println("Character value : "+c);
    }
}
