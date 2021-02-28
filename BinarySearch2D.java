import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BinarySearch2D {
    static int currArr, currIndex;
    static int[] indexChanges;

    /**
     * @param k -> Number of arrays inside the matrix
     * @param m -> Number of elements in each array
     * @return Returns a new array filled from 1 to the size of the matrix(k * m)
     */
    public static int[][] generateArray(int k, int m) {
        if (k == 0 || m == 0) {
            throwException("Empty array generated!");
        }
        int[][] arr = new int[k][m];
        for (int i = 0, a = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                arr[i][j] = ++a;
            }
        }
        return arr;
    }

    /**
     * @param size  -> current size of the matrix
     * @param index -> Array index in matrix
     * @return Returns the Elementindex of a matrix
     * Example: {{1, 2, 3}, {4, 5, 6}}
     * convertIndex(2, 4) -> returns new int[]{1, 1}
     */
    private static int[] convertIndex(int size, int index) {
        int a = index / size;
        int b = index - a * size;
        return new int[]{a, b};
    }

    /**
     * @param matrix -> Matrix to be searched
     * @param number -> Number to be found
     * @return Returns the Indexes of an element in the given matrix, returns [-1, -1] if the matrix does not contains the element
     */
    public static int[] binarySearch2D(int[][] matrix, int number) {
        int size = matrix.length * matrix[0].length / 4;

        int currentArr = matrix.length / 2;
        int currentIndex = matrix[currentArr].length / 2;
        int[] nonExistent = {-1, -1};

        if (matrix[0][0] > number || matrix[matrix.length - 1][matrix[matrix.length - 1].length - 1] < number) {
            return nonExistent;
        }

        while (true) {
            if (matrix[currentArr][currentIndex] == number) {
                return new int[]{currentArr, currentIndex};
            }
            if (size == 1 && matrix[currentArr][currentIndex] < number && matrix[currentArr][currentIndex + 1] > number) {
                return nonExistent;
            }
            int[] indexChanges = convertIndex(matrix.length, size);
            if (matrix[currentArr][currentIndex] > number) {
                currentArr -= indexChanges[0];

                if (currentIndex - indexChanges[1] < 0) {
                    currentIndex = matrix[currentArr].length - Math.abs(currentIndex - indexChanges[1]);
                    currentArr--;
                } else {
                    currentIndex -= indexChanges[1];
                }
            } else {
                currentArr += indexChanges[0];
                if (currentIndex + indexChanges[1] >= matrix[currentArr].length) {
                    currentIndex = (currentIndex + indexChanges[1]) - (matrix[currentArr].length - 1);
                    currentArr++;
                } else {
                    currentIndex += indexChanges[1];
                }
            }
            Math.max(size/2, 1);
            //size /= 2;
            if (size == 0) {
                size = 1;
            }
        }
    }

    /**
     * @param matrix -> Matrix to be searched
     * @param number -> Searched element
     * @return Returns the Indexes of an element in the given matrix, returns [-1, -1] if the matrix does not contains the element
     */
    public static int[] binarySearch2DRek(int[][] matrix, int number) {
        if(matrix.length == 0){
            throwException("Empty matrix!");
        }
        if (matrix[0][0] > number || matrix[matrix.length - 1][matrix[matrix.length - 1].length - 1] < number) {
            return new int[]{-1, -1};
        }

        // Size durch 4 damit Hälfte des übrig geblieben den Arrays
        int size = matrix.length * matrix[0].length / 4;
        return binarySearch2DRek(matrix, number, matrix.length / 2, matrix[matrix.length / 2].length / 2, size);
    }

    /**
     * @param matrix -> Matrix to be searched
     * @param number -> Searched element
     * @param size   -> Size of the remaining Part
     * @return Constructor for the recursive binarySearch2D algorithm
     */
    private static int[] binarySearch2DRek(int[][] matrix, int number, int currentArr, int currentIndex, int size) {
        if (matrix[currentArr][currentIndex] == number) {
            return new int[]{currentArr, currentIndex};
        }
        if (size == 1 && matrix[currentArr][currentIndex] < number && matrix[currentArr][currentIndex + 1] > number) {
            return new int[]{-1, -1};
        }
        indexChanges = convertIndex(matrix.length, size);

        if (matrix[currentArr][currentIndex] > number) {
            currArr = currentArr - indexChanges[0];
            if (currentIndex - indexChanges[1] < 0) {
                currIndex = matrix[currentArr].length - Math.abs(currentIndex - indexChanges[1]);
                currArr--;
            } else {
                currIndex = currentIndex - indexChanges[1];
            }
        } else {
            currArr = currentArr + indexChanges[0];
            if (currentIndex + indexChanges[1] >= matrix[0].length) {
                currIndex = (currentIndex + indexChanges[1]) - (matrix[currentArr].length - 1);
                currArr++;
            } else {
                currIndex = currentIndex + indexChanges[1];
            }
        }
        return binarySearch2DRek(matrix, number, currArr, currIndex, Math.max(size / 2, 1));
    }

    /**
     * @param max -> Highest Number to be generated
     * @param min -> Lowest Number to be generated
     * @return Returns a random number between "max" and "min"
     */
    private static int randomNumber(int max, int min) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    /**
     * @param k    -> Number of arrays inside the matrix
     * @param m    -> Number of elements inside every array
     * @param low  -> Lowest random number
     * @param high -> Highest random number
     * @return Returns an array with random values between "low" and "high" (Array is sorted!)
     */
    public static int[][] generateRandomArray(int k, int m, int low, int high) {
        int[] tmpArr = new int[k * m];
        if (k * m > (high - low) + 1) {
            throwException("Random array requires more available numbers!");
        }
        if (m == 0 || k == 0) {
            throwException("Empty random array generated!");
        }

        List<Integer> usedNumbers = new ArrayList<>();
        for (int i = 0; i < k * m; i++) {
            int newNumber = randomNumber(high, low);
            while (usedNumbers.contains(newNumber)) {
                newNumber = randomNumber(high, low);
            }
            usedNumbers.add(newNumber);
            tmpArr[i] = newNumber;
        }
        Arrays.sort(tmpArr);
        int[][] matrix = new int[k][m];
        int last = 0;
        for (int i = 0, j = 0; i < tmpArr.length; i++, j++) {
            if (i / k != last) {
                last = i / k;
                j = 0;
            }
            matrix[i / k][j] = tmpArr[i];
        }
        return matrix;
    }

    /**
     * @param errorMessage -> Exception with given error message will be thrown
     */
    private static void throwException(String errorMessage) {
        try {
            throw new Exception(errorMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int[][] matrix = generateRandomArray(6, 6, 1, 1000);
        for (int[] arr : matrix) {
            System.out.println(Arrays.toString(arr));
        }
        System.out.println(Arrays.toString(binarySearch2DRek(matrix, -1)));
    }


}