import java.util.*;
public class product {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter value of a :");
        int a = in.nextInt();
        System.out.println("Enter value of b :");
        int b = in.nextInt();
        int product = a*b;
        System.out.println("product of "+a+"*"+b+" is "+product);
    }    
}
