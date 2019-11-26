package project_Team7;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class modeEnum {
    public enum modeType {
        VISUAL, COMMAND, NORMAL, INSERT
    }
    private static modeType mode;
    private static String modeToString;


    public modeEnum(){
        setMode(modeType.NORMAL);
    }

    public static String getModeToString() {
        return modeToString;
    }

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
