package ng.textlab;

import java.util.ArrayList;
import java.util.List;

/**
 * All the text-processing logic. There is no console code in this class.
 *
 * A word is a sequence of letters or digits. Punctuation separates words.
 */
public class TextAnalyzer {

    // ---------- counting ----------

    public int countCharacters(String text) {
        return safe(text).length();
    }

    public int countCharactersWithoutWhitespace(String text) {
        String content = safe(text);
        int count = 0;
        for (int i = 0; i < content.length(); i++) {
            if (!Character.isWhitespace(content.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    public List<String> extractWords(String text) {
        String content = safe(text);
        List<String> words = new ArrayList<String>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                current.append(c);
            } else if (current.length() > 0) {
                words.add(current.toString());
                current.setLength(0);
            }
        }
        if (current.length() > 0) {
            words.add(current.toString());
        }
        return words;
    }

    public int countWords(String text) {
        return extractWords(text).size();
    }

    /**
     * One or more consecutive . ! ? marks form a sentence boundary.
     * A final fragment is counted even without closing punctuation.
     * A fragment only counts as a sentence if it contains at least one letter or digit.
     */
    public int countSentences(String text) {
        String content = safe(text);
        int sentences = 0;
        boolean fragmentHasWord = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (isSentenceEnd(c)) {
                if (fragmentHasWord) {
                    sentences++;
                    fragmentHasWord = false;
                }
            } else if (Character.isLetterOrDigit(c)) {
                fragmentHasWord = true;
            }
        }

        if (fragmentHasWord) {
            sentences++;
        }
        return sentences;
    }

    // ---------- cleaning ----------

    /**
     * Removes leading and trailing whitespace and turns every run of whitespace
     * (spaces, tabs, line breaks) into one single space.
     */
    public String cleanWhitespace(String text) {
        String content = safe(text);
        StringBuilder result = new StringBuilder();
        boolean spacePending = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (Character.isWhitespace(c)) {
                if (result.length() > 0) {
                    spacePending = true;
                }
            } else {
                if (spacePending) {
                    result.append(' ');
                    spacePending = false;
                }
                result.append(c);
            }
        }
        return result.toString();
    }

    // ---------- whole-word search and replace ----------

    public int countWord(String text, String wordToFind) {
        String target = validateWord(wordToFind);
        List<String> words = extractWords(text);
        int count = 0;
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equalsIgnoreCase(target)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Replaces every whole-word match (ignoring case). Everything else in the text,
     * including spacing and punctuation, stays exactly as it was.
     */
    public String replaceWord(String text, String wordToReplace, String replacement) {
        String target = validateWord(wordToReplace);
        if (replacement == null) {
            throw new IllegalArgumentException("Replacement text is required.");
        }

        String content = safe(text);
        StringBuilder result = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                currentWord.append(c);
            } else {
                appendWord(result, currentWord, target, replacement);
                result.append(c);
            }
        }
        appendWord(result, currentWord, target, replacement);
        return result.toString();
    }

    private void appendWord(StringBuilder result, StringBuilder currentWord, String target, String replacement) {
        if (currentWord.length() > 0) {
            String word = currentWord.toString();
            if (word.equalsIgnoreCase(target)) {
                result.append(replacement);
            } else {
                result.append(word);
            }
            currentWord.setLength(0);
        }
    }

    // ---------- palindrome ----------

    /**
     * Ignores case, spaces and punctuation. Text with no letters or digits is not a palindrome.
     */
    public boolean isPalindrome(String text) {
        String content = safe(text);
        StringBuilder cleaned = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                cleaned.append(Character.toLowerCase(c));
            }
        }

        if (cleaned.length() == 0) {
            return false;
        }

        int left = 0;
        int right = cleaned.length() - 1;
        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    // ---------- longest words ----------

    /**
     * All words with the longest length, without duplicates (ignoring case).
     * The spelling of the first appearance is kept.
     */
    public List<String> findLongestWords(String text) {
        List<String> words = extractWords(text);
        List<String> longest = new ArrayList<String>();

        int maxLength = 0;
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).length() > maxLength) {
                maxLength = words.get(i).length();
            }
        }

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (word.length() == maxLength && !containsIgnoreCase(longest, word)) {
                longest.add(word);
            }
        }
        return longest;
    }

    private boolean containsIgnoreCase(List<String> list, String word) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    // ---------- stretch: compare two passages ----------

    /**
     * Returns the position (starting from 0) of the first character where the two
     * passages differ, or -1 if they are exactly the same. If one passage is just
     * shorter, the position is the length of the shorter one.
     */
    public int findFirstDifference(String first, String second) {
        String a = safe(first);
        String b = safe(second);
        int shorter = Math.min(a.length(), b.length());

        for (int i = 0; i < shorter; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return i;
            }
        }

        if (a.length() != b.length()) {
            return shorter;
        }
        return -1;
    }

    // ---------- helpers ----------

    private boolean isSentenceEnd(char c) {
        return c == '.' || c == '!' || c == '?';
    }

    private String validateWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("The word must not be blank.");
        }
        String trimmed = word.trim();
        for (int i = 0; i < trimmed.length(); i++) {
            if (!Character.isLetterOrDigit(trimmed.charAt(i))) {
                throw new IllegalArgumentException(
                        "Please type one word made of letters or digits only (no spaces or punctuation).");
            }
        }
        return trimmed;
    }

    // null is treated as empty text so nothing crashes
    private String safe(String text) {
        if (text == null) {
            return "";
        }
        return text;
    }
}
