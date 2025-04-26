import java.util.*;

class max_consecutive {
   static int maxConsecutiveCount(int[] arr) {
    if (arr.length == 0) return 0; 

    int maxCount = 0, count = 1;

    for (int i = 1; i < arr.length; i++) {
        if (arr[i] == arr[i - 1]) {
            count++;
        } else {
            maxCount = Math.max(maxCount, count);
            count = 1;
        }
    }

    return Math.max(maxCount, count);
}

    public static void main(String[] args) {
        int[] arr = {1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1};
        System.out.println(maxConsecutiveCount(arr));
    }
}
