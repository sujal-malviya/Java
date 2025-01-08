
import java.util.Scanner;


public class prgm5 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("enter number : ");
        int n = Integer.parseInt(sc.nextLine());
        //if u want to use both integer and string in a program then one prblm will come infront of u .
        //to solve it u want to consider nxtInt as integer.parseInt() and following procedure.
        System.out.println("enter string : ");
        String name = sc.nextLine();

        System.out.println("hello "+name+ " here is the counting ");
        for(int  i = 0 ; i<n; i++)
        {
            System.out.print(i+"\t");
        }
    }

    
}
