import java.util.*;
public class Prime {
    public static boolean Isprime(int n)
    {
        boolean prime = true;
        if(n == 2)
        return true;
        for(int i = 2;i<n;i++)
        {
            if(n%i == 0)
            {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner (System.in);
        int num  = in.nextInt();
        boolean n = Isprime(num);
        System.out.println(num +" is prime number : "+n);
    }
}
