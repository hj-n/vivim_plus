package project_Team7;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class VIMMode {

    /**
     * This class represents the CURRENT MODE of the vim plugin. By using static
     * keyword, there is no need of the instances of the class, and every other
     * classes can access to this class to modify or check the current MODE.
     */

    public enum modeType {
        VISUAL, COMMAND, NORMAL, INSERT
    }
    private static modeType mode;
    private static String modeToString;


    public VIMMode(){
        setMode(modeType.NORMAL);
    }

    /** Returns current mode of the vim plugin */
    public static String getModeToString() {
        return modeToString;
    }


    /** Set current mode of the vim plugin */
    public static void setMode(modeType type) {
        mode = type;
        if(type.equals(modeType.VISUAL))
            modeToString = "VISUAL MODE";
        else if(type.equals(modeType.COMMAND))
            modeToString = "COMMAND MODE";
        else if(type.equals(modeType.NORMAL))
            modeToString = "NORMAL MODE";
        else if(type.equals(modeType.INSERT))
            modeToString = "INSERT MODE";
    }
}
