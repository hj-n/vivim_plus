package project_Team7;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.impl.CaretImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.messages.impl.Message;
import com.intellij.util.ui.UIUtil;
import org.bouncycastle.est.ESTAuth;
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
    private static char storedChar = 'x';

    public MyTypedHandler(){

    }

    public void setStoredChar(char c){
        storedChar = c;
        return ;
    }
    public char getStoredChar(){
        return storedChar;
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
//        System.out.println("storedChar is " + getStoredChar());

        if(getStoredChar() == 'i'){
            mode = new modeEnum(modeEnum.modeType.INSERT);
//            System.out.println("insert mode ? " + modeEnum.getModeToString());
            modeViewer(editor);
            MyInsertModeHandler myInsertModeHandler = new MyInsertModeHandler();
            myInsertModeHandler.execute(editor, charTyped, dataContext);
        }
        else{
            if ((charTyped == ':' || charTyped == '/') && commandPanel == null) {
                keyStrokeCommandMode(charTyped + " ", editor);
                mode = new modeEnum(modeEnum.modeType.COMMAND);
                modeViewer(editor);
            }
            else if (charTyped == 'v') {
                mode = new modeEnum(modeEnum.modeType.VISUAL);
                modeViewer(editor);
            }
            else if (charTyped == 'i') {
                mode = new modeEnum(modeEnum.modeType.INSERT);
                modeViewer(editor);
                setStoredChar(charTyped);
            }
            else if(charTyped == 27){
//                System.out.println("in here~~~~~~~~~");
                storedChar = 'x';
            }
            else{
                mode = new modeEnum(modeEnum.modeType.NORMAL);
                modeViewer(editor);
            }
            moveCursor(charTyped, editor);
            //isTypedESC(editor);
//            System.out.println("current mode is " + modeEnum.getModeToString());
        }

    }

    public void isTypedESC(Editor editor){
        editor.getContentComponent().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }
            @Override
            public void keyPressed(KeyEvent e) {

            }
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    System.out.println("typed esc key");
                    if(mode.equals(modeEnum.modeType.COMMAND)){
                        mode = new modeEnum(modeEnum.modeType.NORMAL);//code to execute if escape is pressed
                    }
                    else if(mode.equals(modeEnum.modeType.INSERT)){
                        setStoredChar('x');
                        mode = new modeEnum(modeEnum.modeType.NORMAL);//code to execute if escape is pressed
                    }
                    else if(mode.equals(modeEnum.modeType.VISUAL)){
                        mode = new modeEnum(modeEnum.modeType.NORMAL);//code to execute if escape is pressed
                    }
                    modeViewer(editor);
                }
            }
        });
    }

    private void moveCursor(char charTyped, Editor editor){
        //move under line
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        try {
            if (charTyped == 'j') {
                VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine() + 1, caret.getVisualPosition().getColumn());
                caret.moveToVisualPosition(visualPosition);
            }
            //move upper line
            if (charTyped == 'k') {
                VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine() - 1, caret.getVisualPosition().getColumn());
                caret.moveToVisualPosition(visualPosition);
            }
            //move left
            if (charTyped == 'h') {
                VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() - 1);
                caret.moveToVisualPosition(visualPosition);
            }
            //move right
            if (charTyped == 'l') {
                VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
                caret.moveToVisualPosition(visualPosition);
            }
        }
        catch(Exception e){

        }
    }


    private void modeViewer(Editor editor){
        JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
        JBPopup mes = jbPopupFactory.createMessage(modeEnum.getModeToString());
        mes.setRequestFocus(false);
        mes.show(RelativePoint.getSouthEastOf(editor.getComponent()));
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