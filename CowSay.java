import java.util.Arrays;

public class CowSay {

    private static String getBorder(int size, char borderType) {
        StringBuilder border = new StringBuilder();
        for (; size > 0; size--) {
            border.append(borderType);
        }
        return " " + border.substring(1, border.length() - 1) + " ";
    }

    private static String formatLine(String originalLine, int size, boolean corner, boolean top) {
        StringBuilder originalLineBuilder = new StringBuilder(originalLine);
        originalLineBuilder.append(" ".repeat(Math.max(0, size - originalLineBuilder.length())));
        originalLine = originalLineBuilder.toString();
        if (corner) {
            if (top) {
                return "/ " + originalLine + " \\";
            }
            return "\\ " + originalLine + " /";
        }
        return "| " + originalLine + " |";
    }

    

    private static String[] insertWord(String[] wordList, String word, int index) {
        String[] updatedWordList = new String[wordList.length + 1];
        index++;
        for (int i = 0; i < updatedWordList.length; i++) {
            if (i < index) {
                updatedWordList[i] = wordList[i];
            } else if (i > index) {
                updatedWordList[i] = wordList[i - 1];
            } else {
                updatedWordList[i] = word;
            }
        }
        return updatedWordList;
    }

    private static String[] removeWordAtIndex(String[] wordList, int index) {
        String[] updatedStringList = new String[wordList.length - 1];
        for (int i = 0, k = 0; i < wordList.length; i++) {
            if (i != index) {
                updatedStringList[k++] = wordList[i];
            }
        }
        return updatedStringList;
    }

    private static String[] insertWordParts(String[] wordList, String[] wordPartList, int index) {
        String[] words = Arrays.copyOf(wordList, wordList.length);
        for (int i = 0; i < wordPartList.length; i++) {
            words = insertWord(words, wordPartList[i], index + i);
        }
        return words;
    }

    private static String[] formatWordlist(String[] wordList, int maxSize) {
        String[] copy = Arrays.copyOf(wordList, wordList.length);
        for (int i = 0, k = 0; i < wordList.length; i++) {
            if (wordList[i].length() > maxSize) {
                String[] wordParts = wordList[i].split("(?<=\\G.{" + maxSize + "})");
                copy = insertWordParts(copy, wordParts, k);
                k += wordParts.length;
                copy = removeWordAtIndex(copy, k - wordParts.length);
            } else {
                k++;
            }
        }
        return copy;
    }

    public static void printCowSay(int size, String text, String eyes, boolean tongue) {
        String header = getBorder(size + 4, '_');
        String footer = getBorder(size + 4, '-');

        String[] wordList = formatWordlist(text.split(" "), size);
        System.out.println(header);

        for (int i = 0; i < wordList.length; i++) {
            String line = wordList[i];
            int k = 1;
            while (line.length() < size + 1) {
                if (i + k < wordList.length) {
                    if (line.length() + 1 + wordList[i + k].length() < size + 1) {
                        line += " " + wordList[i + k];
                        k++;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (i == 0) {
                System.out.print(formatLine(line, size, true, true) + "\n");
            } else if (i == wordList.length - 1) {
                System.out.print(formatLine(line, size, true, false));
            } else {
                System.out.print(formatLine(line, size, false, false) + "\n");
            }
            i += k - 1;
        }
        System.out.println((text.length() < size) ? footer : "\n" + footer);
        printCow(eyes, tongue);
    }

    private static void printCow(String eyes, boolean tongue) {
        String[] cowLines = new String[]{
                "        \\   ^__^",
                "         \\  (" + eyes + ")\\_______",
                "            (__)\\       )\\/\\",
                "                ||----w |",
                "                ||     ||"
        };

        if (tongue) {
            cowLines[3] = "             U  ||----w |";
        }
        for (String line : cowLines) {
            System.out.print(line + "\n");
        }
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }

    public static void main(String[] args) {
        String eyes = "oo";
        boolean tongue = false;
        int size = 40;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-W" -> {
                    if (!isNumeric(args[i + 1])) {
                        System.out.println("Ungültiger Input!");
                        return;
                    }
                    size = Integer.parseInt(args[i + 1]);
                    i++;
                }
                case "-b" -> eyes = "==";
                case "-d" -> {
                    eyes = "XX";
                    tongue = true;
                }
                case "-g" -> eyes = "$$";
                case "-p" -> eyes = "@@";
                case "-s" -> {
                    eyes = "**";
                    tongue = true;
                }
                case "-t" -> eyes = "--";
                case "-w" -> eyes = "00";
                case "-y" -> eyes = "..";
                case "-e" -> {
                    eyes = args[i + 1];
                    i++;
                }
                case "-T" -> tongue = !tongue;
            }
        }
        printCowSay(size, args[args.length - 1], eyes, tongue);
    }
}
