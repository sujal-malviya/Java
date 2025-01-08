import java.util.Scanner;

public  class Main
{

	public static void main(String[]args) {
		Scanner sc = new Scanner(System.in);
		int num;
		System.out.println("Enter value :");
		num = sc.nextInt();
		if(num%2==0) {
			System.out.println("hello world");
		}
		else
		{
			System.out.println("namaste!!!");
		}
	}

}

