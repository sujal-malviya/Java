package apna_college;

import java.util.Scanner;

public class program2 {
    public static void main(String[] args) {
        Scanner in  = new Scanner(System.in);
        int div = 2;
        int n = in.nextInt();
       int count = 0;
       if(n<=1)
       {
           System.out.println("Not a prime number.");
       }
       else
       {
        for(int i =1;i<=n;i++)
        {
            if(n%i==0)
            {
                count++;
                
            }
        }
        if(count>2)
        {
            System.out.println("Non prime number");
        }
        else
        {
            System.out.println("Prime number");
        }
       }
       
    }
}
