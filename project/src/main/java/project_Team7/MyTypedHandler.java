package project_Team7;

import com.intellij.openapi.actionSystem.CommonDataKeys;
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
import com.intellij.ui.Gray;
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
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.*;
import java.util.List;

public class MyTypedHandler implements TypedActionHandler {


    private String currentCommand = null;
    private String currentSearchingString = null;


    private JPanel commandPanel = null;
    private JPanel modePanel = null;
    private boolean isESC = false;

    public String getCurrentCommand() {
        return currentCommand;
    }
    private static char storedChar = 'x';



    public void setStoredChar(char c){
        storedChar = c;
        return ;
    }
    public char getStoredChar(){
        return storedChar;
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {

        Caret caret = editor.getCaretModel().getCurrentCaret();
        editor.getContentComponent().addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    editor.getSettings().setBlockCursor(true);
                }

            }
        });
        if(getStoredChar() == 'i'){
            modeEnum.setMode(modeEnum.modeType.INSERT);
            modeViewer(editor);
            MyInsertModeHandler myInsertModeHandler = new MyInsertModeHandler();
            myInsertModeHandler.execute(editor, charTyped, dataContext);
        }
        else{
            switch(charTyped) {
                case ':':
                case '/':
                    modeEnum.setMode(modeEnum.modeType.COMMAND);
                    keyStrokeCommandMode(charTyped + " ", editor);
                    modeViewer(editor);
                    break;
                case 'v':
                    modeEnum.setMode(modeEnum.modeType.VISUAL);
                    modeViewer(editor);
                    break;
                case 'i':
                case 'I':
                case 'a':
                case 'A':
                case 'o':
                case 'O':
                    changeCaretToInsertionMode(editor, getInsertionTypeFromChar(charTyped));
                    modeEnum.setMode(modeEnum.modeType.INSERT);
                    modeViewer(editor);
                    setStoredChar('i');
                    break;
                case 'h':
                case 'j':
                case 'k':
                case 'l':
                    moveCursor(charTyped, editor);
                    modeEnum.setMode(modeEnum.modeType.NORMAL);
                    modeViewer(editor);
                    break;
                case 't':



                default:
                    modeEnum.setMode(modeEnum.modeType.NORMAL);
                    modeViewer(editor);
            }
        }
        if(modeEnum.getModeToString() == "INSERT MODE" ) {
            editor.getSettings().setBlockCursor(false);
        }
        else {
            editor.getSettings().setBlockCursor(true);
        }
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
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                isESC = false;
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    setStoredChar('x');
                    modeEnum.setMode(modeEnum.modeType.NORMAL);
                    modeViewer(editor);
                    popup.closeOk(e);
                    popup.canClose();
                    isESC = true;
                }
                else{
                    if(command.equals(": ")) {
                        if(textField.getText().length() <= 2) {
                            textField.setText(": ");
                        }
                        currentCommand = textField.getText().substring(1);
                    }
                    else {
                        if(textField.getText().length() <= 2) {
                            textField.setText("/ ");
                        }
                        currentSearchingString = textField.getText().substring(1);
                    }
                }
            }
        });
    }



    private void changeCaretToInsertionMode(Editor editor, enterInsertionType type) {
        int position = 0;
        switch(type) {
            case i :
                position = editor.getCaretModel().getCurrentCaret().getSelectionStart();
                break;
            case I :
                position = editor.getCaretModel().getCurrentCaret().getVisualLineStart();
                break;
            case a :
                position = editor.getCaretModel().getCurrentCaret().getSelectionStart() + 1;
                break;
            case A :
                position = editor.getCaretModel().getCurrentCaret().getVisualLineEnd() - 1;
                break;
            case o :
                Caret caret = editor.getCaretModel().getCurrentCaret();
                editor.getDocument().replaceString(caret.getVisualLineEnd(), caret.getVisualLineEnd(), "\n");
                position = editor.getCaretModel().getVisualLineEnd();
                break;
            case O :
                Caret caret2 = editor.getCaretModel().getCurrentCaret();
                editor.getDocument().replaceString(caret2.getVisualLineStart(), caret2.getVisualLineStart(), "\n");
                position = editor.getCaretModel().getVisualLineStart();
                break;

        }
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        caret.setSelection(position, position);
        caret.moveToOffset(position);
        editor.getSettings().setBlockCursor(false);
    }

    private enum enterInsertionType {
        o, O, i, I, a, A
    }

    private enterInsertionType getInsertionTypeFromChar(char c) {
        switch (c) {
            case 'i':
                return enterInsertionType.i;
            case 'I':
                return enterInsertionType.I;
            case 'a':
                return enterInsertionType.a;
            case 'A':
                return enterInsertionType.A;
            case 'o':
                return enterInsertionType.o;
            case 'O':
                return enterInsertionType.O;
        }
        return null;
    }



}