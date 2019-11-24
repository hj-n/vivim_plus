package project_Team7;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.*;
import java.util.List;

public class MyTypedHandler implements TypedActionHandler {


    private String currentCommand = null;
    private String currentSearchingString = null;

    private modeEnum mode;


    private JPanel commandPanel = null;
    private JPanel modePanel = null;

    public String getCurrentCommand() {
        return currentCommand;
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        mode = new modeEnum(modeEnum.modeType.NORMAL);

        if ((charTyped == ':' || charTyped == '/') && commandPanel == null) {
            keyStrokeCommandMode(charTyped + " ", editor);
            mode = new modeEnum(modeEnum.modeType.COMMAND);


        }
        if (charTyped == 'v') {
            mode = new modeEnum(modeEnum.modeType.VISUAL);


        }
        if (charTyped == 'i') {
            mode = new modeEnum(modeEnum.modeType.INSERT);

            //i 입력 되었을 때만 editor 가 수정가능하도록 바꿔야 함.
            //현재는 일단 뭘 입력하든 editor을 수정할 수 없는 normal 모드가 디폴트임.
        }
        modeViewer(editor);
        System.out.println(modeEnum.getModeToString());

    }


    private void modeViewer(Editor editor){
        modePanel = new JBPanel(new BorderLayout());
        JTextField textField = new JTextField(modeEnum.getModeToString());
        modePanel.setBackground(JBColor.background());
        modePanel.add(textField);
        modePanel.setAlignmentX(editor.getComponent().getAlignmentX());
        modePanel.setAlignmentY(editor.getComponent().getAlignmentY());
        modePanel.setPreferredSize(new Dimension(60, 20));
        modePanel.setSize(new Dimension(60, 20));
        modePanel.setEnabled(true);
        modePanel.setVisible(true);
        JBSplitter navigationSplitter = new JBSplitter(false);
        navigationSplitter.setFirstComponent(modePanel);
        navigationSplitter.setSecondComponent(textField);
        System.out.println("showing ? " + modePanel.isShowing());
        System.out.println("info? " + modePanel);

//        JBLabel jbLabel = new JBLabel();
//        jbLabel.setSize(new Dimension(60, 20));
//        jbLabel.setAnchor(editor.getComponent());
//        Image image = jbLabel.createImage(60, 20);
//        System.out.println(image);
//        System.out.println(jbLabel.isShowing());

    }

    private void keyStrokeCommandMode(String command, Editor editor) {

        commandPanel = new JPanel(new BorderLayout());
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
        commandPanel.add(textField);

        JBPopupFactory a = JBPopupFactory.getInstance();
        JBPopup popup = a.createComponentPopupBuilder(commandPanel, textField).createPopup();
        popup.setRequestFocus(true);
        popup.setSize(new Dimension(editor.getComponent().getWidth(), 10));
        popup.showUnderneathOf(editor.getComponent());
        popup.addListener(new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                commandPanel = null;

            }
        });
    }




}