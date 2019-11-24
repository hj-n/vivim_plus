package project_Team7;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MyModeViewer{

    private static String currentMode = null;
    private JFrame frame = null;
    private JLabel label = null;

    public MyModeViewer(){
        currentMode = null;
    }
    public MyModeViewer(String currentMode){
        currentMode = modeEnum.getModeToString();
        frame = new JFrame("Current Mode");
        label = new JLabel(currentMode);
        frame.add(label);

        frame.addPropertyChangeListener("ModeType", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                frame.setSize(50, 20);
                frame.setVisible(true);
            }
        });
    }
//    public static void main(String[] args) {
//        // TODO Auto-generated method stub
//        System.out.println("start main ");
//        SwingUtilities.invokeLater(() -> {
//            if(modeEnum.getModeToString() != null){
//                MyModeViewer viewer = new MyModeViewer();
//                System.out.println("in main " + modeEnum.getModeToString());
//            }
//        });
//
//    }

}


