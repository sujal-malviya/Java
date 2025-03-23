import java.util.*;

public class prog2 {
    static float PI = 3.14f;
    public static void main(String[] args) {
      Scanner in = new Scanner(System.in);
      int radius = in.nextInt();
      float area = PI * (radius*radius);
      System.out.println("Area of circle is " + area);
    }
}
