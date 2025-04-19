class Callbyval
{
    public static int factorial(int n)
    {
        if(n == 1 || n==0)
        {
            return 1;
        }
        int fact = 1;
        for(int i = 1; i <= n; i++)
        {
            fact = fact*i;
        }
        return fact;
    }
    public static void main(String[] args) {
        int n = 5;
        int result = factorial(n);
        System.out.println(result);
    }
}