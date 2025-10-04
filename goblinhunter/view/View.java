package goblinhunter.view;

public class View implements iView{

    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static View instance = null;

    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    protected MainGUI mainGUI = null;

    private View() {
    //TO-DO
    }

    public static IView getInstance() {
        if (instance == null)
            instance = new View();
        return instance;
    }

} // end class
