import java.util.*;
public class relational {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int a = in.nextInt();
        int b = in.nextInt();
        if(a == b)
        {
            System.out.println("a is equal to b");
        
         if(a<b)
        {
            System.out.println("a is less than b");
        }
        else
        {
            System.out.println("a is greater than b");
        }
         if(a<=b)
        {
            System.out.println("a is less than or equal to b");
        }
        else
        {
            System.out.println("a is greater than or equal to b");
        }
    }
    else 
    {
        System.out.println("a is not equal to b");
    }
            
        
    }
}
