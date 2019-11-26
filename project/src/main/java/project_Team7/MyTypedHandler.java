package project_Team7;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.ui.awt.RelativePoint;
import org.apache.batik.css.dom.CSSOMStoredStyleDeclaration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MyTypedHandler implements TypedActionHandler {


    private String currentCommand = null;
    private String currentSearchingString = null;
    private boolean isSearch;
    private ArrayList<Integer> searchList = new ArrayList<Integer>();
    private int currentIndex;
    private boolean isESC = false;


    private JPanel commandPanel = null;

    public String getCurrentCommand() {
        return currentCommand;
    }
    private static char storedChar = 'x';

    private VisualPosition cursorVisualPosition;

    private MyInsertModeHandler myInsertModeHandler = new MyInsertModeHandler();

    private boolean hasDocumentListener = false;

    private String recentTypedString = null;
    private String recentDeletedString = null;

    private String clipBoard = "";

    public String getRecentTypedString() {
        return recentTypedString;
    }

    public String getRecentDeletedString() {
        return recentDeletedString;
    }

    public void setRecentTypedString(String s) {
        recentTypedString = s;
    }

    public void setRecentDeletedString(String s) {
        recentDeletedString = s;
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
        if(hasDocumentListener == false) {
            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    setRecentTypedString(event.getNewFragment().toString());
                    setRecentDeletedString(event.getOldFragment().toString());
                    searchList = new ArrayList<Integer>();

                }
            });
            hasDocumentListener = true;
        }



        Caret caret = editor.getCaretModel().getCurrentCaret();
        if(getStoredChar() == 'i'){
            modeEnum.setMode(modeEnum.modeType.INSERT);
            modeViewer(editor);
            myInsertModeHandler.execute(editor, charTyped, dataContext, this);
        }
        else{
            switch(charTyped) {
                case ':':
                case '/':
                    modeEnum.setMode(modeEnum.modeType.COMMAND);
                    keyStrokeCommandMode(charTyped + " ", editor);
                    modeViewer(editor);
                    break;
                case 'n':
                    if(modeEnum.getModeToString().equals("NORMAL MODE")) {
                        focusNextSearchString(editor, true);
                    }
                    break;
                case 'N':
                    if(modeEnum.getModeToString().equals("NORMAL MODE")) {
                        focusNextSearchString(editor, false);
                    }
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

                case 'd':
                    if(caret.getSelectedText() != null ) {
                        clipBoard = caret.getSelectedText();
                        editor.getDocument().replaceString(caret.getSelectionStart(), caret.getSelectionEnd(), "");
                    }
                    else {
                        if(getStoredChar() == 'd') {
                            int start = caret.getVisualLineStart();
                            int end = caret.getVisualLineEnd();
                            caret.setSelection(start, end);
                            clipBoard = caret.getSelectedText();
                            editor.getDocument().replaceString(start, end, "");
                            setStoredChar('x');
                        }
                        else {
                            setStoredChar('d');
                        }
                    }
                    System.out.println("clipBoard d: " + clipBoard);
                    break;
                case 'y':
                    if(caret.getSelectedText() != null ) {
                        clipBoard = caret.getSelectedText();
                        caret.removeSelection();
                    }
                    else {
                        if(getStoredChar() == 'y') {
                            int start = caret.getVisualLineStart();
                            int end = caret.getVisualLineEnd();
                            caret.setSelection(start, end);
                            clipBoard = caret.getSelectedText();
                            caret.removeSelection();
                            setStoredChar('x');
                        }
                        else {
                            setStoredChar('y');
                        }
                    }
                    System.out.println("clipBoard y: " + clipBoard);
                    break;
                case 'p':
                    if(clipBoard != null) {
                        String original = clipBoard;
                        if(clipBoard.charAt(clipBoard.length() - 1) != '\n' )
                            clipBoard = clipBoard + '\n';
                        if(caret.getVisualLineEnd() == editor.getDocument().getText().length()){
                            clipBoard = '\n' + clipBoard;
                        }
                        editor.getDocument().replaceString(caret.getVisualLineEnd(), caret.getVisualLineEnd(), clipBoard);
                        clipBoard = original;
                    }
                    break;
                case 'P':
                    if(clipBoard != null){
                        editor.getDocument().replaceString(caret.getOffset(), caret.getOffset(), clipBoard);
                    }
                    break;
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
                else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    searchString(editor);
                    popup.closeOk(e);
                    popup.canClose();

                }
                else{
                    if(command.equals(": ")) {
                        if(textField.getText().length() <= 2) {
                            textField.setText(": ");
                            isSearch = false;
                        }
                        currentCommand = textField.getText().substring(2);
                    }
                    else {
                        if(textField.getText().length() <= 2) {
                            textField.setText("/ ");
                            isSearch = true;
                        }
                        currentSearchingString = textField.getText().substring(2);
                    }
                }
            }
        });
    }


    private void searchString(Editor editor){
        String text = editor.getDocument().getText();
        System.out.println(currentSearchingString);
        System.out.println(text.contains(currentSearchingString));
        while(text.contains(currentSearchingString)){
            int slicedIndex = text.indexOf(currentSearchingString);
            int beginIndex = slicedIndex + (editor.getDocument().getText().length() - text.length());
            searchList.add(beginIndex);
            text = text.substring(slicedIndex + currentSearchingString.length());
        }
        currentIndex = 0;
        modeEnum.setMode(modeEnum.modeType.NORMAL);
        focusNextSearchString(editor, true);

    }

    private void focusNextSearchString(Editor editor, boolean isSearchingNext) {
        int start, end;
        if(searchList.size() > 0) {
            start = searchList.get(currentIndex);
            end = start + currentSearchingString.length();
            editor.getCaretModel().getCurrentCaret().setSelection(start, end);
            if (isSearchingNext) {
                if (currentIndex == searchList.size() - 1)
                    currentIndex = 0;
                else
                    currentIndex++;
            } else {
                if (currentIndex == 0)
                    currentIndex = searchList.size() - 1;
                else
                    currentIndex--;
            }
        }
        System.out.println(currentIndex);
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