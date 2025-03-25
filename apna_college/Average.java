import java.util.*;
public class Average {
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
    
        double average = (a+b)/2;
        System.out.println("Average is: " + average);
        sc.close();
    }
}
