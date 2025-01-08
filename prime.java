import java.util.Scanner;
public class prime {
    public static void main(String []args)
    {
        //In this code Time Limit Exceeds.
        Scanner sc = new Scanner(System.in);
        int testcase = sc.nextInt();
        for(int i = 0; i<testcase; i++)
        {
            int n = sc.nextInt();
            int count = 0;
            for(int div = 1; div <=n; div++)
            {
                if(n%div == 0)
                {
                    count++;
                }
            }
            if(count == 2)
        {
            System.out.println("prime");
        }
        else
        {
            System.out.println("not prime");
        }
        }
        
    }
}
