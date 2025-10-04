package goblinhunter.model;

public class Model implements IModel{


    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static Model instance = null;

    //---------------------------------------------------------------
    // STATIC METHODS
    //---------------------------------------------------------------
    public static IModel getInstance() {
        if (instance == null)
            instance = new Model();
        return instance;
    }
}
