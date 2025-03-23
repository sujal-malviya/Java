package apna_college;

import java.util.Scanner;

public class program {
    public static void main(String[] args) {
        Scanner in  = new Scanner(System.in);
        int a = in.nextInt();
        int b = in.nextInt();
        int c = in.nextInt();
        if(a>b && a>c)
        {
            System.out.println("The largest number is "+a);
        }
        else if(b>a && b>c)
        {
            System.out.println("The largest number is "+b);
        }
        else
        {
            System.out.println("The largest number is "+c);
        }
    }
}
