package ng.textlab;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PassageTest {

    @Test
    public void editingDoesNotChangeTheOriginal() {
        Passage passage = new Passage();
        passage.setNewPassage("  Hello   world  ");
        TextAnalyzer analyzer = new TextAnalyzer();

        passage.setEdited(analyzer.cleanWhitespace(passage.getEdited()));

        assertEquals("  Hello   world  ", passage.getOriginal());
        assertEquals("Hello world", passage.getEdited());
    }

    @Test
    public void newPassageReplacesBothVersions() {
        Passage passage = new Passage();
        passage.setNewPassage("first");
        passage.setEdited("changed");
        passage.setNewPassage("second");
        assertEquals("second", passage.getOriginal());
        assertEquals("second", passage.getEdited());
    }

    @Test
    public void nullPassageBecomesEmpty() {
        Passage passage = new Passage();
        passage.setNewPassage(null);
        assertEquals("", passage.getOriginal());
        assertEquals("", passage.getEdited());
    }
}
