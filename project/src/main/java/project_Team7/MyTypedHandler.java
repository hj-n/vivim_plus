package project_Team7;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
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



    private String currentCommand = null;
    private String currentSearchingString = null;

    private JPanel panel = null;

    public String getCurrentCommand() {
        return currentCommand;
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        if ((charTyped == ':' || charTyped == '/') && panel == null) {
            keyStrokeCommandMode(charTyped + " ", editor);
        }

    }




    private void keyStrokeCommandMode(String command, Editor editor) {

        panel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField(command);

        textField.setEditable(true);

        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

                if(command.equals(": ")) {
                    if(textField.getText().length() <= 2) {
                        textField.setText(": ");
                    }
                    currentCommand = textField.getText().substring(1);
                    System.out.println(currentCommand);
                }
                else {
                    if(textField.getText().length() <= 2) {
                        textField.setText("/ ");
                    }
                    currentSearchingString = textField.getText().substring(1);
                    System.out.println(currentSearchingString);
                }

            }
        });
        panel.add(textField);


        JBPopupFactory a = JBPopupFactory.getInstance();
        JBPopup popup = a.createComponentPopupBuilder(panel, textField).createPopup();
        popup.setRequestFocus(true);
        popup.setSize(new Dimension(editor.getComponent().getWidth(), 10));
        popup.showUnderneathOf(editor.getComponent());
        popup.addListener(new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                panel = null;

            }
        });
    }

}