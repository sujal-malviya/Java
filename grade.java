import java.util.Scanner;
public class grade {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Marks Obtained :");
        int marks = sc.nextInt();
        if(marks <= 100)
        {
            if(marks > 90)
        {
            System.out.println("Excellent ");
        }
        else if( marks >80 && marks <90)
        {
            System.out.println("Good marks ");
        }
        else if( marks >70 && marks <80)
        {
            System.out.println("Fair marks ");
        }
        else if(marks >60 && marks <70)
        {
            System.out.println("Meets expectation ");
        }
        else 
        {
            System.out.println("Bad marks "+marks);
        }
        }
        else
        {
            System.out.println("Invalid Marks");
        }
    }
}
