package project_Team7;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.*;
import java.util.List;

public class MyTypedHandler implements TypedActionHandler {


    private JPanel panel = null;

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        if (charTyped == (':') && panel == null) {
            panel = new JPanel(new BorderLayout());
            JTextField textField = new JTextField(": ");
            textField.setEditable(true);
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    System.out.println(textField.getText());
                    System.out.println(e.getKeyChar());
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            panel.add(textField);



            JBPopupFactory a = JBPopupFactory.getInstance();
            JBPopup popup = a.createComponentPopupBuilder(panel, textField).createPopup();
            popup.setRequestFocus(true);
            popup.setSize(new Dimension(editor.getComponent().getWidth(), 10));
            popup.showUnderneathOf(editor.getComponent());



        }

    }
}