package ng.textlab;

public class Passage {

    private String original;
    private String edited;

    public Passage() {
        this.original = "";
        this.edited = "";
    }

    // A new passage replaces both the original and the edited version
    public void setNewPassage(String text) {
        if (text == null) {
            this.original = "";
        } else {
            this.original = text;
        }
        this.edited = this.original;
    }

    public String getOriginal() {
        return original;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        if (edited == null) {
            this.edited = "";
        } else {
            this.edited = edited;
        }
    }
}
