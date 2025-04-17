import java.util.Scanner;
class demo
{
    public static void main(String[] args) {
        Scanner in = new Scanner (System.in);
        int a,b,c;
        a = in.nextInt();
        b = in.nextInt();
        c = in.nextInt();
        int average = (a+b+c)/3;
        System.out.println("The average of three numbers is " + average);
        
    }
}