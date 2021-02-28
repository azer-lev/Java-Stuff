import java.util.Random;

public class Main {
    public static int randomNumber(int max, int min){
        return new Random().nextInt((max - min) + 1) + min;
    }
    public static double pi(int simulationen){
        int t = 0, counter = simulationen;
        while(counter > 0){
            if(Math.pow(new Random().nextDouble(), 2) + Math.pow(new Random().nextDouble(), 2) < 1)
                t++;

            counter--;
        }
        return (t/(double)simulationen)*4;
    }
    public static double ziegenproblemA(int simulations, int door){
        int counter = simulations, t = 0;
        while(counter > 0){
            if(door == randomNumber(3, 1))
                t++;
            counter--;
        }
        return (double)t/(double)simulations;
    }
    public static double ziegenproblemB(int simulations, int door){
        int counter = simulations, t = 0;
        while(counter > 0){
            if(randomNumber(3, 1) != door)
                t++;
            counter--;
        }
        return (double)t/(double)simulations;
    }
    public static void ziegenproblem(int simulations){
        System.out.println("Anzahl der Simulationen: " + simulations);
        System.out.println("Fall A: " + ziegenproblemA(simulations, randomNumber(3, 1)) * 100 + "%");
        System.out.println("Fall B: " + ziegenproblemB(simulations, randomNumber(3, 1))* 100 + "%");
    }

    public static void cube(int simulations){
        System.out.println("Die Chance zwei Fünfen hintereinander zu Würfel ist gleich der Chance eine 5 gefolgt von einer 6 zu Würfel,\ndaher würde die benötigte Zeit identisch bei n Anzahl an Simulatioen sein.");
        System.out.println("Anzahl der Simulationen: " + simulations);
        System.out.println("Wahrscheinlichkeit für 2x5: " + doubleFive(simulations) * 100 + "%");
        System.out.println("Wahrscheinlichkeit für 5 gefolgt von 6: " + sixFollowsFive(simulations) * 100 + "%");
    }
    public static double doubleFive(int simulations){
        int tmpS = simulations, t = 0;
        while(tmpS > 0){
            if(randomNumber(6, 1) == 5 && randomNumber(6, 1) == 5)
                t++;
            tmpS--;
        }
        return (double)t/(double)simulations;
    }
    public static double sixFollowsFive(int simulations){
        int tmpS = simulations, t = 0;
        while(tmpS > 0){
            if(randomNumber(6, 1) == 5 && randomNumber(6, 1) == 6)
                t++;
            tmpS--;
        }
        return (double)t/(double)simulations;
    }
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Available Parameter: Pi, Ziegenproblem, Würfel -> followed by number of simulations!");
            return;
        }
        switch (args[0].toLowerCase()){
            case "pi":
                System.out.println(pi(Integer.parseInt(args[1])));
                break;
            case "ziegenproblem":
                ziegenproblem(Integer.parseInt(args[1]));
                break;
            case "würfel":
                cube(Integer.parseInt(args[1]));
                break;
            default:
                System.out.println("Available Parameter: Pi, Ziegenproblem, Würfel -> followed by number of simulations!");
                break;
        }
    }
}
