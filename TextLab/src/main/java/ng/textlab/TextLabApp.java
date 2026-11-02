package ng.textlab;

import java.util.List;
import java.util.Scanner;

public class TextLabApp {

    private final TextAnalyzer analyzer;
    private final Passage passage;
    private final Scanner scanner;

    public TextLabApp(Scanner scanner) {
        this.analyzer = new TextAnalyzer();
        this.passage = new Passage();
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        TextLabApp app = new TextLabApp(scanner);
        app.run();
        scanner.close();
    }

    public void run() {
        System.out.println("=====================================");
        System.out.println("   Welcome to TextLab");
        System.out.println("=====================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Choose an option: ");
            if (choice == null) {
                System.out.println("\nNo more input. Goodbye!");
                running = false;
            } else {
                running = handleChoice(choice.trim());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("---------- MAIN MENU ----------");
        System.out.println("1. Enter a new passage");
        System.out.println("2. Show original and edited passage");
        System.out.println("3. Show counts (characters, words, sentences)");
        System.out.println("4. Clean whitespace");
        System.out.println("5. Search for a word");
        System.out.println("6. Replace a word");
        System.out.println("7. Check palindrome");
        System.out.println("8. Show longest words");
        System.out.println("9. Compare two passages");
        System.out.println("0. Exit");
    }

    private boolean handleChoice(String choice) {
        switch (choice) {
            case "1":
                enterPassage();
                break;
            case "2":
                showPassages();
                break;
            case "3":
                showCounts();
                break;
            case "4":
                cleanWhitespace();
                break;
            case "5":
                searchWord();
                break;
            case "6":
                replaceWord();
                break;
            case "7":
                checkPalindrome();
                break;
            case "8":
                showLongestWords();
                break;
            case "9":
                comparePassages();
                break;
            case "0":
                System.out.println("Goodbye!");
                return false;
            default:
                System.out.println("Invalid option '" + choice + "'. Please type a number from the menu (0 to 9).");
                break;
        }
        return true;
    }

    private void enterPassage() {
        System.out.println("Type or paste your passage. Type END on a line by itself to finish.");
        StringBuilder text = new StringBuilder();
        boolean firstLine = true;
        boolean reading = true;

        while (reading) {
            String line = readLine("");
            if (line == null || line.trim().equalsIgnoreCase("END")) {
                reading = false;
            } else {
                if (!firstLine) {
                    text.append("\n");
                }
                text.append(line);
                firstLine = false;
            }
        }

        passage.setNewPassage(text.toString());
        System.out.println("Passage saved.");
    }

    private void showPassages() {
        System.out.println("Original passage:");
        printText(passage.getOriginal());
        System.out.println("Edited passage:");
        printText(passage.getEdited());
    }

    private void printText(String text) {
        System.out.println("-----");
        if (text.isEmpty()) {
            System.out.println("(empty)");
        } else {
            System.out.println(text);
        }
        System.out.println("-----");
    }

    private void showCounts() {
        String text = passage.getEdited();
        System.out.println("Counts for the current (edited) passage:");
        System.out.println("Characters (with whitespace):    " + analyzer.countCharacters(text));
        System.out.println("Characters (without whitespace): " + analyzer.countCharactersWithoutWhitespace(text));
        System.out.println("Words:                           " + analyzer.countWords(text));
        System.out.println("Sentences:                       " + analyzer.countSentences(text));
    }

    private void cleanWhitespace() {
        String cleaned = analyzer.cleanWhitespace(passage.getEdited());
        passage.setEdited(cleaned);
        System.out.println("Whitespace cleaned. The edited passage is now:");
        printText(passage.getEdited());
    }

    private void searchWord() {
        String word = readLine("Word to search for: ");
        try {
            int count = analyzer.countWord(passage.getEdited(), word);
            System.out.println("The word '" + word.trim() + "' appears " + count + " time(s) (whole word, ignoring case).");
        } catch (IllegalArgumentException e) {
            System.out.println("Could not search: " + e.getMessage());
        }
    }

    private void replaceWord() {
        String word = readLine("Word to replace: ");
        String replacement = readLine("Replace it with: ");
        try {
            int count = analyzer.countWord(passage.getEdited(), word);
            String updated = analyzer.replaceWord(passage.getEdited(), word, replacement);
            passage.setEdited(updated);
            System.out.println("Replaced " + count + " occurrence(s). The edited passage is now:");
            printText(passage.getEdited());
        } catch (IllegalArgumentException e) {
            System.out.println("Could not replace: " + e.getMessage());
        }
    }

    private void checkPalindrome() {
        String text = readLine("Text to check: ");
        if (analyzer.countWords(text) == 0) {
            System.out.println("There are no letters or digits to check, so this is not a palindrome.");
        } else if (analyzer.isPalindrome(text)) {
            System.out.println("Yes, it is a palindrome (ignoring case, spaces and punctuation).");
        } else {
            System.out.println("No, it is not a palindrome.");
        }
    }

    private void showLongestWords() {
        List<String> longest = analyzer.findLongestWords(passage.getEdited());
        if (longest.isEmpty()) {
            System.out.println("There are no words in the passage.");
        } else {
            System.out.println("Longest word(s), " + longest.get(0).length() + " characters each:");
            for (int i = 0; i < longest.size(); i++) {
                System.out.println("- " + longest.get(i));
            }
        }
    }

    private void comparePassages() {
        String first = readLine("First passage: ");
        String second = readLine("Second passage: ");
        if (first == null || second == null) {
            return;
        }

        int index = analyzer.findFirstDifference(first, second);
        if (index == -1) {
            System.out.println("The two passages are exactly the same.");
        } else if (index >= first.length() || index >= second.length()) {
            System.out.println("The passages match until one of them ends. First difference at position "
                    + (index + 1) + " (one passage is longer than the other).");
        } else {
            System.out.println("First difference at position " + (index + 1) + ": "
                    + describeCharacter(first.charAt(index)) + " in the first passage, but "
                    + describeCharacter(second.charAt(index)) + " in the second.");
        }
    }

    private String describeCharacter(char c) {
        if (c == ' ') {
            return "a space";
        } else if (c == '\t') {
            return "a tab";
        }
        return "'" + c + "'";
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }
}
