import java.util.*;
public class chefrank {
    public static void main(String []args)
    {
        Scanner in = new Scanner(System.in);
        int t = in.nextInt();
        while(t-- >0)
        {
            int X = in.nextInt();
            if(X<=10)
            {
                System.out.println("YES");
            }
            else
            {
                System.out.println("NO");
            }
        }
    }
}
