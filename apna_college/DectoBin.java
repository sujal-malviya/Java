public class DectoBin {
    public static void DtoB(int n)
    {
        int mynum = n;
        int bin_num = 0;
        int pow =0;
        while(n>0)
        {
            
            int rem = n%2;
            bin_num = bin_num +( rem*(int)Math.pow(10,pow));
            pow++;
            n = n/2;
        }
        System.out.println("Decimal to Binary of "+mynum+" is : "+bin_num);
    }
    public static void main(String []args)
    {
        DtoB(156);
    }
}
