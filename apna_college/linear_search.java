import java.util.*;
public class linear_search {
    public static void main(String[] args) {
		Scanner sc  = new Scanner(System.in);
		int arr[] = new int[5];
		int target = sc.nextInt();
		boolean b = false;
		for(int i = 0;i<arr.length;i++)
		{
		    arr[i] = sc.nextInt();
		}
		for(int i = 0;i<arr.length;i++)
		{
		    if(arr[i] == target)
		    {
		       b = true;
		    }
		    
		}
		if(b)
		{
		    System.out.print("Found");
		}
		else{
		    System.out.print("Not Found");
		}
		
	}
}
