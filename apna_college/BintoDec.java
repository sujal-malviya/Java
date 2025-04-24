class BintoDec
{
    public static void BtoD(int n)
    {
        int mynum = n;
        int pow = 0;
        
        int dec = 0;
        while(n>0)
        {
            int lastdigit = n%10;
            dec = dec + (lastdigit*(int)Math.pow(2,pow));
            pow++;
            n = n/10;
        }

        System.out.println("value of binary num : "+mynum+" to decimal is = "+dec);
    }
    public static void main(String []args)
    {
        BtoD(101);
    }
}