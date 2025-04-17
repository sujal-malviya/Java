import java.util.Scanner;

public class Inp {
    public static void area(int side)
{
    
    int result = side * side;
    System.out.println("Area of Square is : "+result);
}
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int side = sc.nextInt();
        area(side);
        
     }
}
