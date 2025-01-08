import java.util.Scanner;
public class calculator {
    long add(int n ,int m)
    {
        int sum = n + m;
        return sum;
    }
    long subtract(int n ,int m)
    {
        int sum = n - m;
        return sum;

    }
    long divide(int n ,int m)
    {
        int ans = n / m;
        return ans;
    }
    long prod(int n ,int m)
    {
        int ans = n *m;
        return ans ;
    }

    public static void main(String[] args) {
        calculator c = new calculator();
        @SuppressWarnings("resource")
        Scanner s = new Scanner(System.in);
        int choice = s.nextInt();
        switch(choice)
        {
            case 1 -> System.out.println("Addition of two numbers is " + c.add(10, 20));
            case 2 -> System.out.println("Addition of two numbers is " + c.subtract(10, 20));
            case 3 -> System.out.println("prod of two number is "+c.prod(10, 20));
            case 4 -> System.out.println("division of two numbers is " + c.divide(10, 20));
        }
        
    }
}
