import java.util.*;
public class Arithmatic {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        //binary arithmatic - 
        int num1 = in.nextInt();
        int num2 = in.nextInt();
        int sum = num1 + num2;
        System.out.println("sum = " +sum);
        System.out.println("before = "+num1);
        // unary Arithmatic -
        num1 = num1+1;
        System.out.println("after = " +num1);
    }
}
