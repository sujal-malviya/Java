import java.util.*;
public class reverse_array {
    public static void main(String[] args) {
		Scanner sc  = new Scanner(System.in);
		int arr[] = new int[5];
		for(int i = 0;i<arr.length;i++)
		{
		    arr[i] = sc.nextInt();
		}
		for(int i = arr.length;i>0;i--)
		{
		    System.out.print(arr[i-1]+" ");
		}
	}
}
