import java.util.Arrays;
import java.util.stream.IntStream;

public class RecursiveFunction2 {
    public static int marge(int x) {
        return (x == 0) ? 1 : x - homer(marge(x - 1));
    }

    public static int homer(int x) {
        return (x == 0) ? 0 : x - marge(homer(x - 1));
    }

    public static boolean dispute(int x) {
        return !(marge(x) == homer(x));
    }

    public static int[] differenceDisputes(int x) {
        return differenceDisputes(x, 0, new int[]{});
    }

    private static int getNextDispute(int y) {
        return !dispute(++y) ? getNextDispute(y) : y;
    }

    private static int[] differenceDisputes(int x, int y, int[] curr) {
        return (x == 0) ? curr : differenceDisputes(--x, getNextDispute(y), add(curr, getNextDispute(y) - y));
    }

    private static int[] add(int[] arr, int value) {
        arr = Arrays.copyOf(arr, arr.length + 1);
        arr[arr.length - 1] = value;
        return arr;
    }

    private static double getAverage(int[] arr) {
        return (double) IntStream.of(arr).sum() / arr.length;
    }

    public static void main(String[] args) {
        System.out.println("Kupferne Hochzeit " + (dispute(7) ? "Streit" : "Kein Steit"));
        System.out.println("Silberne Hochzeit " + (dispute(25) ? "Streit" : "Kein Steit"));
        System.out.println("Die beiden verstehen sich nach der Zeit besser, da die Zeit zwischen den Streits immer größer wird!");
        System.out.println("Average time bei 5 Streits: " + getAverage(differenceDisputes(5)));
        System.out.println("Average time bei 10 Streits: " + getAverage(differenceDisputes(10)));
    }
}