import java.util.ArrayList;
import java.util.List;

public class SimpleEncryption{
    static class Caesar{
        /*

        Properties Section

         */
        private String _sentence;

        public Caesar(String sentence){
            _sentence = sentence;
        }
        public void setSentence(String sentence) {
            _sentence = sentence;
        }

        public String getSentence() {
            return _sentence;
        }

        /*

        Main Functions

         */

        String encrypt(String str, int shiftAmount){
            StringBuilder encryptedString = new StringBuilder();
            for(int i = 0; i < str.length(); i++){
                int charInt = str.charAt(i);

                int encryptedChar = charInt;
                //Sonderzeichen excluden
                if(isValidChar(charInt)){
                    encryptedChar += shiftAmount;
                    if((char)charInt >= 'a'){
                        //Lowercase
                        encryptedChar = fixChar(encryptedChar, 'z');
                    }
                    else{
                        //Uppercase
                        encryptedChar = fixChar(encryptedChar, 'Z');
                    }
                }
                encryptedString.append((char) encryptedChar);
            }
            return encryptedString.toString();
        }

        String encrypt(int shiftAmount){
            return encrypt(getSentence(), shiftAmount);
        }

        public String decrypt(int shiftAmount){
            return encrypt(shiftAmount * (-1));
        }

        public String decrypt(String str, int shiftAmount){
            return encrypt(str,shiftAmount * (-1));
        }

        public String decryptBruteForceA(){
            int maxChanceIndex = 1;
            for(int i = 2; i < 27; i++){
                String decryptedString = decrypt(i);
                //Wenn TrueWordChance von i shift höher als maxChanceIndex ist setzte index auf i
                if(getChance(decryptedString) > getChance(decrypt(maxChanceIndex))){
                    maxChanceIndex = i;
                }
            }
            return decrypt(maxChanceIndex);
        }

        public String decryptBruteForceB(){
            final double[] relativFrequency = {   0.0651, 0.0189, 0.036, 0.0508, 0.174, 0.0166, 0.0301, 0.0476, 0.0755, 0.0027, 0.0121, 0.0344,
                    0.0253, 0.0978, 0.0251, 0.0079, 0.0002, 0.07, 0.0727, 0.0615, 0.0435, 0.0067, 0.0189, 0.0003,
                    0.0004, 0.013
            };

            final char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
            double[] charCounter;

            int smallestDivergenceIndex = 0;
            double smallestDivergence = Integer.MAX_VALUE;

            for(int i = 1; i < 27; i++){
                String currentSentence = decrypt(getSentence().toLowerCase(), i);
                System.out.println(currentSentence);
                List<Character> usedChars = new ArrayList<>();
                charCounter = new double[26];
                for(int j = 0; j < currentSentence.length(); j++){
                    if(isValidChar(currentSentence.charAt(j))){
                        char currentChar = currentSentence.charAt(j);
                        //Jeder Buchstabe wird einmal betrachtet
                        //Doppelte Betrachtung verfälscht die Ergebnisse
                        if(!usedChars.contains(currentChar)){
                            int numberOfSimilarChars = getNumberOfSimilarChars(chars, currentChar);
                            double sentenceRelevance = Math.abs(((double)numberOfSimilarChars/getSentence().length()) - relativFrequency[getCharIndex(chars, currentChar)]);
                            charCounter[getCharIndex(chars, currentChar)] = sentenceRelevance;
                            usedChars.add(currentChar);
                        }
                    }
                }
                double standardDivergence = getSum(charCounter);
                if(standardDivergence < smallestDivergence){
                    smallestDivergence = standardDivergence;
                    smallestDivergenceIndex = i;
                }
            }
            return decrypt(getSentence(), smallestDivergenceIndex);
        }

        /*

        Helper-Functions

         */

        private double getSum(double[] arr){
            double sum = 0;
            for(double a : arr){
                sum += a;
            }
            return sum;
        }

        //Returns the first index of the Char "ch" in the Char-Array "charList"
        private int getCharIndex(char[] charList, char ch){
            for(int i = 0; i < charList.length; i++){
                if(charList[i] == ch){
                    return i;
                }
            }
            return -1;
        }

        private int getNumberOfSimilarChars(char[] charList, char ch){
            int counter = 0;
            for(char c : charList){
                if(c == ch){
                    counter++;
                }
            }
            return counter;
        }

        private double getChance(String sentence){
            int trueWordCounter = wordCounter(sentence);
            return (double)trueWordCounter / sentence.split(" ").length;
        }

        private int wordCounter(String testSentence){
            //Liste der meistvorkommenden Wörter in Deutsch
            //31.8% aller Worte
            final String words1 = "die, der, und, in, zu, den, das, nicht, von, sie, ist, des, sich, mit, dem, dass, er, es, ein, ich, auf, so, eine, auch, als, an, nach, wie, im, für";
            String[] directoryList1 = words1.split(", ");
            //15.3% aller Worte
            final String words2 = "man, aber, aus, durch, wenn, nur, war, noch, werden, bei, hat, wir, was, wird, sein, einen, welche, sind, oder, zur, um, haben, einer, mir, über, ihm, diese, einem, ihr, uns, da, zum, kann, doch, vor, dieser, mich, ihn, du, hatte, seine, mehr, am, denn, nun, unter, sehr, selbst, schon, hier, bis, habe, ihre, dann, ihnen, seiner, alle, wieder, meine, Zeit, gegen, vom, ganz, einzelnen, wo, muss, ohne, eines, können, sei";
            String[] directoryList2 = words2.split(", ");
            //7.25% aller Worte
            final String words3 = "ja, wurde, jetzt, immer, seinen, wohl, dieses, ihren, würde, diesen, sondern, weil, welcher, nichts, diesem, alles, waren, will, Herr, viel, mein, also, soll, worden, lassen, dies, machen, ihrer, weiter, Leben, recht, etwas, keine, seinem, ob, dir, allen, großen, Jahre, Weise, müssen, welches, wäre, erst, einmal, Mann, hätte, zwei, dich, allein, Herren, während, Paragraph, anders, Liebe, kein, damit, gar, Hand, Herrn, euch, sollte, konnte, ersten, deren, zwischen, wollen, denen, dessen, sagen, bin, Menschen, gut, darauf, wurden, weiß, gewesen, Seite, bald, weit, große, solche, hatten, eben, andern, beiden, macht, sehen, ganze, anderen, lange, wer, ihrem, zwar, gemacht, dort, kommen, Welt, heute, Frau, werde, derselben, ganzen, deutschen, lässt, vielleicht, meiner";
            String[] directoryList3 = words3.split(", ");

            int trueWordCounter = 0;

            trueWordCounter += numberOfContains(directoryList1, testSentence);
            trueWordCounter += numberOfContains(directoryList2, testSentence);
            trueWordCounter += numberOfContains(directoryList3, testSentence);

            return trueWordCounter;
        }

        private int numberOfContains(String[] wordList, String sentence){
            int count = 0;
            for(String word :  wordList){
                //Wenn Wort im Satz enthalten ist
                //Leerzeichen und Komma um Überschneidungen zu vermeiden, bsp die / dieses
                if(sentence.contains(word + " ") || sentence.contains(word + ",") || sentence.contains(word + ".")){
                    count++;
                }
            }
            return count;
        }

        private boolean isValidChar(int n){
            return (n > 64 && n < 91) || (n > 96 && n < 123);
        }

        private int fixChar(int charInt, int maxIndex){
            //Wenn charInt unterhalb der Ascii-Buchstabentabelle ist wird der Wert erhöht
            if(charInt < (maxIndex - 25))
                charInt += 26;

            //Während charInt überhalb der Ascii-Buchstabentabelle ist wird der Wert erhöht
            while(charInt > maxIndex)
                charInt -= 26;
            return charInt;
        }
    }

    public static void main(String[] args){
        Caesar encrypted = new Caesar("die ja wenn.");
        System.out.println(encrypted.encrypt(10));
        encrypted.setSentence(encrypted.encrypt(10));
        System.out.println(encrypted.getSentence());
        System.out.println(encrypted.decryptBruteForceA());

    }
}