public class TernarySearch {

    static int binaryCalls, ternaryCalls;

    public static int find ( int [] a, int x) {
        binaryCalls = 0;
        return _find (a ,0,a. length -1,x);
    }

    private static int _find ( int [] a,int l,int r, int x) {
        binaryCalls++;
        if (l>r) return -1 ;
        int m=(l+r) /2;
        if (x==a[m]) return m;
        else if (x<a[m]) return _find (a,l,m -1,x);
        else return _find (a,m+1,r,x);
    }



    public static int ternaryRec(int[] a, int x) {
        ternaryCalls = 0;
        return ternaryRec(a, x, 0, a.length - 1);
    }

    private static int ternaryRec(int[] a, int x, int l, int r) {
        ternaryCalls++;
        if (r - l >= 0) {
            int one = l + (r - l) / 3, two = one + (r - l) / 3;
            if (x == a[one]) return one;

            if (x == a[two]) return two;

            if (x < a[one]) return ternaryRec(a, x, l, one - 1);

            if (x > a[two]) return ternaryRec(a, x, two + 1, r);

            return ternaryRec(a, x, one + 1, two - 1);
        } else {
            return -1;
        }
    }

    /*
        Ternary log3(n) rekursive calls *
        Binary log2(n) rekursive calls

        Bei der Berechnung der Zeit der Algorithmen werden konstanten ignoriert.
        TernarySearch benötigt jedes mal die berechnung von 2 Mitten, sowie eine höhere Anzahl an Bedingungen
        was im schlimmsten falls dazu führt das TernarySearch langsamer als BinarySearch ist
     */

    public static void main(String[] args) {
        int[] test = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        System.out.println(ternaryRec(test, 9));
    }
}