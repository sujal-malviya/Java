import java.util.Scanner;
public class pttrn1 {
    public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the number : ");
    int i = 0,j = 0 ,n = sc.nextInt();
    
    // while(i<=n)
    // {
    //     System.out.print(i);
    //     i++;
    // }
    for(i = 0; i<n; i++)
    {
        for(j = 0; j<n ; j++)
        {
            System.out.print("*");
        }
        System.out.println();
    }
    
    }
}
