package tim.wernecke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameOfLife {

    private static int getState(double chanceOfLife) {
        return new Random().nextFloat() <= chanceOfLife ? 1 : 0;
    }

    public static void main(String[] args) {
        int size = 10;
        float chanceOfLife = 0.25F;

        /*if(args.length != 2){
            System.out.println("Bitte Parameter für Spielgröße und Lebenschance angeben!");
            return;
        }

        try{
            size = Integer.parseInt(args[0]);
            chanceOfLife = Float.parseFloat(args[1]);
        }catch (NullPointerException | NumberFormatException ex){
            System.out.println("Ungültiger Input! " + ex.getMessage());
            return;
        }

        if(size < 1){
            System.out.println("Bitte valide Spielfeldgröße angeben!");
            return;
        }
        if(chanceOfLife < 0 || chanceOfLife > 1){
            System.out.println("Chance of Life nicht zwischen 0 und 1!");
            return;
        }*/
        cGame game = new cGame(size, chanceOfLife);
        game.simulate(5);
    }

    static class cNeighbours {
        List<int[]> neighList = new ArrayList<>();
        int size;
        int[][] Game;

        public cNeighbours(int x, int y, int _size, int[][] _Game) {
            size = _size;
            Game = _Game;
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (!(i == 0 && j == 0)) {
                        int[] position = {x + i, y + j};
                        if (isValid(position[0]) && isValid(position[1])) {
                            neighList.add(position);
                        }
                    }
                }
            }
        }

        List<int[]> validNeighbours() {
            return neighList;
        }

        boolean isValid(int a) {
            return a >= 0 && a < size;
        }
    }

    static class cGame {
        int size;
        int[][] Game;

        public cGame(int _size, double _chanceOfLife) {
            int[][] tmp = new int[_size][_size];
            for (int i = 0; i < _size; i++)
                for (int j = 0; j < _size; j++)
                    tmp[i][j] = getState(_chanceOfLife);
            size = _size;
            Game = tmp;
        }

        int state(int x, int y) {
            try {
                return Game[x][y];
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println(x + " " + y);
                ex.printStackTrace();
                return -1;
            }
        }

        void simulate() {
            int[][] newGame = new int[size][size];
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++) {
                    cNeighbours neighbours = new cNeighbours(i, j, size, Game);
                    List<int[]> validNeighbours = neighbours.validNeighbours();
                    int numOfValid = this.numberOfAlive(validNeighbours);
                    switch (numOfValid) {
                        case 3:
                            newGame[i][j] = 1;
                            break;
                        case 2:
                            newGame[i][j] = this.state(i, j);
                            break;
                        default:
                            newGame[i][j] = 0;
                            break;
                    }
                }
            Game = newGame;
        }

        void simulate(int n) {
            while (n > 0) {
                this.simulate();
                this.print();
                n--;
            }
        }

        int numberOfAlive(List<int[]> list) {
            int aliveCounter = 0;
            for (int[] i : list)
                if (this.state(i[0], i[1]) == 1)
                    aliveCounter++;
            return aliveCounter;
        }

        void print() {
            for (int[] n : Game)
                System.out.println(Arrays.toString(n));
            System.out.println("\n");
        }
    }
}