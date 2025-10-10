package goblinhunter.model;

public class Model implements IModel{
    //---------------------------------------------------------------
    // STATIC CONSTANTS
    //---------------------------------------------------------------
    public static final int DEFAULT_NUM_ROWS  = 13;
    public static final int DEFAULT_NUM_COLUMNS = 11;



    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static Model instance = null;
    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    private final int[][] gameAreaArray;
    private Model() {
        this.gameAreaArray = new int[DEFAULT_NUM_ROWS][DEFAULT_NUM_COLUMNS];
    }



    public int getNumRows(){
        return this.gameAreaArray.length;
    }

    public int getNumColumns() {
        return this.gameAreaArray[0].length;
    }



    public static IModel getInstance() {
        if (instance == null)
            instance = new Model();
        return instance;
    }

}
