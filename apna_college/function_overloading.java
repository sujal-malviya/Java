public class function_overloading {
    public static int sum(int a, int b)
    {
        return a+b;
    }
    public static float sum(float a ,float b)
    {
        return a+b;
    }
    public static int sum(int a,int b ,int c)
    {
        return a+b+c;
    }
    public static double sum(double a,double b)
    {
        return a+b;
    }
    public static void main(String[] args) {
        int result = sum(3,4);
        float result1 =   sum(3.2f,1.2f);
        double result2 =    sum(3.333f,4.444f);
        int result3 =  sum(3,4,3);
        System.out.println("the value of integer sum = "+result);
        System.out.println("the value of float sum = "+result1);
        System.out.println("the value of double sum = "+result2);
        System.out.println("the value of integer sum = "+result3);
    }    
}
