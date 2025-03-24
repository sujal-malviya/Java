import java.util.*;
public class input {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter input for Integer : ");
        int a = in.nextInt();
        System.out.println("Input for Integer : "+a);
        System.out.println("Enter input for float : ");
        float b = in.nextFloat();
        System.out.println("Input for float : "+b);
        System.out.println("Enter input for double : ");
        double d = in.nextDouble();
        System.out.println("Input for double : "+d);
        System.out.println("Enter input for String : ");
        String s = in.next();
        System.out.println("Intput for String : "+s);
        System.out.println("Enter input for Character : ");
        char c =in.next().charAt(0);
        System.out.println("Input for Character : "+c);


    }
}
