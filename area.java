import java.util.*;
public class area {
    public static void main(String[] args) {
        Scanner in  =  new Scanner(System.in);
        int radius = in.nextInt();
        double area = Math.PI * radius * radius;
        System.out.println("The area of the circle is " + area);
}
}