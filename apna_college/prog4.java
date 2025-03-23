import java.util.*;
public class prog4 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int I =1;
        int sum = 0;
        int N = 25;
        int X;
        for(int i = 1;i<=N;i++)
        {
            X = in.nextInt();
            sum = sum + X;
        }
        System.out.println(sum/N);
    }
        
}
