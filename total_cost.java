import java.util.*;
public class total_cost {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        float cost1 = in.nextFloat();
        float cost2 = in.nextFloat();
        float cost3 = in.nextFloat();
        float total_cost = cost1 + cost2 + cost3;
        float gst = total_cost*0.18f;
        float finall_bill = total_cost + gst;
        System.out.println("Total cost: " + total_cost);
        System.out.println("gst : "+gst);
        System.out.println("finall_bill : "+finall_bill);
    }
}
