import java.util.Scanner;

public class count {
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int dig = 0;
        while(n!=0)
        {
            n = n/10;
            dig++;
        }
        System.out.println(dig);
    }
}
