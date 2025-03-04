import java.util.*;
interface  vehicle
{
    void speedup(int a);
    void brakes(int b);
    void changegear(int a, int b);


}
class bike implements vehicle
{
    public int speed ;
    public int gear;
    public void changegear(int newgear)
    {
        gear = newgear;
    }
    public void speedup(int increment)
    {
        speed = speed + increment;
    }
    public void brakes(int decrement)
    {
        speed = speed - decrement;
    }
    public void display()
    {
        System.out.println("Speed: " + speed);
        System.out.println("gear: "+gear);
    }
}
class Main1
{
    public static void main(String[] args) {
        bike b = new bike();
        b.changegear(2);
        b.speedup(3);
        b.brakes(1);

        System.out.println("Bike present state:");
        b.display();
    }
}