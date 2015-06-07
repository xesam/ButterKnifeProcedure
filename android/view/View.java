package android.view;

/**
 * Created by xe xesamguo@gmail.com on 15-6-6.
 */
public class View {

    public static final int NO_ID = -1;

    public int id;

    public View() {
    }

    public View(int id) {
        this.id = id;
    }

    public View findViewById(int id) {
        return new View(id);
    }
}
