import java.util.Scanner;
public class prime1 {
    public static void main(String []args)
    {
        //In this code Time Limit Exceeds.
        Scanner sc = new Scanner(System.in);
        int testcase = sc.nextInt();
        for(int i = 0; i<testcase; i++)
        {
            //In this Time Limit does not exceeds.
            int n = sc.nextInt();
            int count = 0;
            for(int div = 2; div * div <=n; div++)
            {
                if(n % div == 0)
                {
                    count++;
                    break;
                }
            }
            if(count == 0)
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

