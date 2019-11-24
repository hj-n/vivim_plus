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

    modeEnum(modeType type){
        mode = type;
        if(type.equals(modeType.VISUAL))
            modeToString = "VISUAL MODE";
        else if(type.equals(modeType.COMMAND))
            modeToString = "COMMAND MODE";
        else if(type.equals(modeType.NORMAL))
            modeToString = "NORMAL MODE";
        else if(type.equals(modeType.INSERT))
            modeToString = "INSERT MODE";

//        JFrame frame = new JFrame("Current Mode");
//        System.out.println("in modeEnum : " + modeToString);
//        JLabel label = new JLabel(modeToString);
//        label.setSize(60, 20);
//        frame.add(label);
//        frame.setSize(60, 20);
//        frame.setVisible(true);
//        System.out.println("in modeEnum frame is : " + frame);
//        frame.addPropertyChangeListener("ModeType", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                System.out.println("in show modeEnum : " + evt.getPropertyName());
//            }
//        });
    }



    public static String getModeToString() {
        return modeToString;
    }
}
