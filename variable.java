public class variable {
    public static void main(String[] args) {
        int number = 1;
        float number1 = 2.2f;
        double number2 =3.333;
        boolean number3 = false;
        String number4 = "four";
        System.out.println(number+" is Intger\n"+number1+" is float\n"+number2+" is double\n"+number3+" is boolean\n"+number4+" is String\n");
        Integer wrap =Integer.valueOf(number);
        int adress = System.identityHashCode(wrap);
        System.out.println("address of number variable is :"+adress);
    }
    
}
