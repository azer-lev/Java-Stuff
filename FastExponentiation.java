public class FastExponentiation {

    /********************************************************
     Hier bitte Antwort zu Teil (1) als Kommentar einfugen

     x ^ 13 => Ungerade
     2^13 = 8192
     2*2^(13-1 / 2) = 2 * (2 * 2)^(6)  = 2 * 4^6 = 8192
     x * x^((n-1)/2) = x^n => Korrektheit bewiesen
     ********************************************************/

    static int rekCalls = 0;
    static int iterCalls = 0;

    public static double fastPotRek(double x, int n) {
        rekCalls = 0;
        return _fastPotRek(x, n);
    }

    private static double _fastPotRek(double x, int n) {
        rekCalls++;
        if (n == 0)
            return 1;
        if (n % 2 == 0)
            return _fastPotRek(x * x, n / 2);
        return x * _fastPotRek(x, --n);
    }

    public static double fastPotIter(double x, int n) {
        double res = 1, tmp;

        for(; n > 0;){
            iterCalls++;
            if(n % 2 == 0){
                n /= 2;
                x = x * x;
            }else{
                n--;
                res *= x;
            }
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(fastPotRek(5, 13));
        System.out.println(rekCalls);
        System.out.println(fastPotIter(5, 13));
        System.out.println(iterCalls);
    }
}
