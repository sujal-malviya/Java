import java.util.ArrayList;
import java.util.Arrays;
public class Rearrange_array {
    
    static void rightRotate(ArrayList<Integer> arr, int start, int end) {
        int temp = arr.get(end);
        for (int i = end; i > start; i--) {
            arr.set(i, arr.get(i - 1));
        }
        arr.set(start, temp);
    }

    
    static void rearrange(ArrayList<Integer> arr) {
        int n = arr.size();

        for (int i = 0; i < n; i++) {

           
            if (arr.get(i) >= 0 && i % 2 == 1) {

          
                for (int j = i + 1; j < n; j++) {
                    if (arr.get(j) < 0) {
                        rightRotate(arr, i, j);
                        break;
                    }
                }
            }

            
            else if (arr.get(i) < 0 && i % 2 == 0) {

                
                for (int j = i + 1; j < n; j++) {
                    if (arr.get(j) >= 0) {
                        rightRotate(arr, i, j);
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(1, 2, 3, -4, -1, 4));

        rearrange(arr);
        for (int i = 0; i < arr.size(); i++) {
            System.out.print(arr.get(i) + " ");
        }
    }
}

