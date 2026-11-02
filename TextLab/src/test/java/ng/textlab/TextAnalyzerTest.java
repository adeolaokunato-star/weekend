package ng.textlab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextAnalyzerTest {

    private TextAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        analyzer = new TextAnalyzer();
    }

    private String join(List<String> items) {
        String result = "";
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                result = result + ",";
            }
            result = result + items.get(i);
        }
        return result;
    }

    // ---------- counts ----------

    @Test
    public void countsCharactersWithAndWithoutWhitespace() {
        String text = "a b\tc\nd";
        assertEquals(7, analyzer.countCharacters(text));
        assertEquals(4, analyzer.countCharactersWithoutWhitespace(text));
    }

    @Test
    public void countsWordsAcrossRepeatedSpacesTabsAndLineBreaks() {
        String text = "  Hello \t  world \n\n again  ";
        assertEquals(3, analyzer.countWords(text));
    }

    @Test
    public void punctuationSeparatesWords() {
        assertEquals(4, analyzer.countWords("Hello,world;this-is"));
    }

    @Test
    public void digitsCountAsWordCharacters() {
        assertEquals(4, analyzer.countWords("room 42 is open"));
    }

    @Test
    public void countsSentencesEndingWithDifferentMarks() {
        assertEquals(3, analyzer.countSentences("Hello world. How are you? Fine!"));
    }

    @Test
    public void consecutivePunctuationIsOneBoundary() {
        assertEquals(2, analyzer.countSentences("Wait... what?!"));
        assertEquals(2, analyzer.countSentences("Hi!!! Really???"));
    }

    @Test
    public void finalFragmentWithoutPunctuationIsASentence() {
        assertEquals(2, analyzer.countSentences("Hello. World"));
        assertEquals(1, analyzer.countSentences("No punctuation here"));
    }

    @Test
    public void emptyAndWhitespaceOnlyTextGiveZeroCounts() {
        assertEquals(0, analyzer.countWords(""));
        assertEquals(0, analyzer.countSentences("   \t\n  "));
        assertEquals(0, analyzer.countCharactersWithoutWhitespace("   \t\n  "));
        assertEquals(0, analyzer.countWords(null));
        assertEquals(0, analyzer.countSentences(null));
        assertEquals(0, analyzer.countCharacters(null));
    }

    @Test
    public void punctuationOnlyPassageHasNoWordsAndNoSentences() {
        String text = "!!! ... ??? ,,, ---";
        assertEquals(0, analyzer.countWords(text));
        assertEquals(0, analyzer.countSentences(text));
        assertEquals(0, analyzer.findLongestWords(text).size());
        assertTrue(!analyzer.isPalindrome(text));
    }

    // ---------- cleaning whitespace ----------

    @Test
    public void cleanWhitespaceRemovesLeadingAndTrailingSpaces() {
        assertEquals("Hello world", analyzer.cleanWhitespace("   Hello world   "));
    }

    @Test
    public void cleanWhitespaceCollapsesSpacesTabsAndLineBreaks() {
        assertEquals("Hello world again", analyzer.cleanWhitespace("Hello    world\t\tagain"));
        assertEquals("one two three", analyzer.cleanWhitespace("one\n\ntwo \t\n three"));
    }

    @Test
    public void cleanWhitespaceOfWhitespaceOnlyTextIsEmpty() {
        assertEquals("", analyzer.cleanWhitespace("  \t \n  "));
        assertEquals("", analyzer.cleanWhitespace(""));
        assertEquals("", analyzer.cleanWhitespace(null));
    }

    @Test
    public void cleanWhitespaceKeepsPunctuation() {
        assertEquals("a , b", analyzer.cleanWhitespace("a  ,  b"));
    }

    // ---------- whole-word search ----------

    @Test
    public void searchIgnoresCase() {
        assertEquals(3, analyzer.countWord("Java is fun. JAVA is big. I like java.", "Java"));
        assertEquals(3, analyzer.countWord("Java is fun. JAVA is big. I like java.", "jAvA"));
    }

    @Test
    public void searchForJavaDoesNotMatchJavaScript() {
        String text = "I use Java and JavaScript. JavaScript is not Java.";
        assertEquals(2, analyzer.countWord(text, "Java"));
        assertEquals(2, analyzer.countWord(text, "JavaScript"));
    }

    @Test
    public void searchTreatsPunctuationAsSeparator() {
        assertEquals(2, analyzer.countWord("Java-based tools and Java's features", "java"));
    }

    @Test
    public void searchForMissingWordGivesZero() {
        assertEquals(0, analyzer.countWord("Hello world", "java"));
        assertEquals(0, analyzer.countWord("", "java"));
        assertEquals(0, analyzer.countWord("   ", "java"));
    }

    @Test
    public void searchWithBlankOrMultiWordTermIsRejected() {
        try {
            analyzer.countWord("Hello world", "   ");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("blank"));
        }
        try {
            analyzer.countWord("Hello world", "two words");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("one word"));
        }
    }

    // ---------- whole-word replace ----------

    @Test
    public void replaceChangesAllMixedCaseOccurrences() {
        assertEquals("Kotlin, Kotlin and Kotlin rule",
                analyzer.replaceWord("Java, JAVA and java rule", "java", "Kotlin"));
    }

    @Test
    public void replaceLeavesJavaScriptAlone() {
        assertEquals("Kotlin and JavaScript",
                analyzer.replaceWord("Java and JavaScript", "Java", "Kotlin"));
    }

    @Test
    public void replaceKeepsSpacingAndPunctuation() {
        assertEquals("Hi,  Kotlin!\tDone.",
                analyzer.replaceWord("Hi,  Java!\tDone.", "java", "Kotlin"));
    }

    @Test
    public void replaceWithNoMatchLeavesTextUnchanged() {
        assertEquals("Hello world", analyzer.replaceWord("Hello world", "java", "Kotlin"));
    }

    @Test
    public void replaceOnEmptyTextDoesNotCrash() {
        assertEquals("", analyzer.replaceWord("", "java", "Kotlin"));
        assertEquals("", analyzer.replaceWord(null, "java", "Kotlin"));
    }

    @Test
    public void replaceWithBlankTargetIsRejected() {
        try {
            analyzer.replaceWord("Hello", "", "x");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("blank"));
        }
    }

    // ---------- palindrome ----------

    @Test
    public void classicPalindromeIsRecognised() {
        assertTrue(analyzer.isPalindrome("A man, a plan, a canal: Panama!"));
    }

    @Test
    public void palindromeIgnoresCaseSpacesAndPunctuation() {
        assertTrue(analyzer.isPalindrome("Race Car"));
        assertTrue(analyzer.isPalindrome("Was it a car or a cat I saw?"));
        assertTrue(analyzer.isPalindrome("12321"));
    }

    @Test
    public void nonPalindromeIsRejected() {
        assertTrue(!analyzer.isPalindrome("Hello"));
        assertTrue(!analyzer.isPalindrome("Java"));
    }

    @Test
    public void emptyOrPunctuationOnlyTextIsNotAPalindrome() {
        assertTrue(!analyzer.isPalindrome(""));
        assertTrue(!analyzer.isPalindrome("   "));
        assertTrue(!analyzer.isPalindrome("!!! ???"));
        assertTrue(!analyzer.isPalindrome(null));
    }

    // ---------- longest words ----------

    @Test
    public void findsSingleLongestWord() {
        assertEquals("programming", join(analyzer.findLongestWords("I love programming and testing")));
    }

    @Test
    public void findsAllLongestWordsWhenTied() {
        assertEquals("bird,fish", join(analyzer.findLongestWords("cat dog bird fish")));
    }

    @Test
    public void longestWordsHaveNoDuplicatesIgnoringCase() {
        assertEquals("Java,code", join(analyzer.findLongestWords("Java java JAVA code Code")));
    }

    @Test
    public void longestWordsIgnorePunctuation() {
        assertEquals("Hello,world", join(analyzer.findLongestWords("Hello, world!")));
    }

    @Test
    public void longestWordsOfEmptyTextIsEmpty() {
        assertEquals(0, analyzer.findLongestWords("").size());
        assertEquals(0, analyzer.findLongestWords("   ").size());
    }

    // ---------- stretch: first difference ----------

    @Test
    public void identicalPassagesHaveNoDifference() {
        assertEquals(-1, analyzer.findFirstDifference("hello", "hello"));
        assertEquals(-1, analyzer.findFirstDifference("", ""));
    }

    @Test
    public void findsFirstDifferingPosition() {
        assertEquals(3, analyzer.findFirstDifference("hello", "help!"));
        assertEquals(0, analyzer.findFirstDifference("abc", "xbc"));
    }

    @Test
    public void shorterPassageDifferenceIsAtItsEnd() {
        assertEquals(3, analyzer.findFirstDifference("abc", "abcd"));
        assertEquals(0, analyzer.findFirstDifference("", "a"));
    }
}
