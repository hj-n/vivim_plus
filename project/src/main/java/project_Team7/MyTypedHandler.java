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
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MyTypedHandler implements TypedActionHandler {


    private String currentCommand = null;
    private String currentSearchingString = null;
    private boolean isSearch;
    private ArrayList<Integer> searchList = new ArrayList<>();
    private int currentIndex;
    private boolean isESC = false;
    private JPanel commandPanel = null;
    private static char storedChar = 'x';
    private MyInsertModeHandler myInsertModeHandler = new MyInsertModeHandler();
    private boolean hasDocumentListener = false;
    private String recentTypedString = null;
    private String recentDeletedString = null;
    private String clipBoard = "";


    /** Getter, setter methods */

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

    /** End of getter, setters */


    /**
     *  Execute for the every keyboard input, except the input of
     *  special keys like ESC, BACKSPACE, AND ENTER
     */
    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        if(!hasDocumentListener) {    // prevent more than one listeners to be added
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
        /** add listener to detect ESC, BACKSPACE, and ENTER input */
        myInsertModeHandler.addKeyListener(editor, this);

        Caret caret = editor.getCaretModel().getCurrentCaret();
        if(getStoredChar() == 'i'){     /** INSERT MODE */
            modeEnum.setMode(modeEnum.modeType.INSERT);
            modeViewer(editor);
            myInsertModeHandler.execute(editor, charTyped);
        }
        else{   /** NORMAL MODE */
            normalModeControl(editor, charTyped, caret);
        }
        /** Set correct cursor shape for each mode */
        setProperCursorShape(editor);
    }

    /**
     * Main Function of controlling normal mode. performs matching functionality
     * for input shortcut, which is given by the argument 'charTyped'.
     * @param editor Opened editor
     * @param charTyped the argument which denotes the shortcuts for command mode
     * @param caret The Caret of the opened editor
     */
    private void normalModeControl(Editor editor, char charTyped, Caret caret) {

            switch(charTyped) {
                case ':':
                case '/':
                    modeEnum.setMode(modeEnum.modeType.COMMAND);
                    keyStrokeCommandMode(charTyped + " ", editor);
                    modeViewer(editor);
                    break;
                case 'n':
                    if (modeEnum.getModeToString().equals("NORMAL MODE")) {
                        focusNextSearchString(editor, true);
                    }
                    break;
                case 'N':
                    if (modeEnum.getModeToString().equals("NORMAL MODE")) {
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
                    myInsertModeHandler.setEnteredAfterInsertion(true);
                    /** Store 'i' for detecting current mode is INSERT MODE */
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
                    if (caret.getSelectedText() != null) {
                        clipBoard = caret.getSelectedText();
                        editor.getDocument().replaceString(caret.getSelectionStart(), caret.getSelectionEnd(), "");
                    } else {
                        if (getStoredChar() == 'd') {
                            int start = caret.getVisualLineStart();
                            int end = caret.getVisualLineEnd();
                            caret.setSelection(start, end);
                            clipBoard = caret.getSelectedText();
                            editor.getDocument().replaceString(start, end, "");
                            /** Store arbitrary charecter 'x' for recovering initial condition */
                            setStoredChar('x');
                        } else {
                            /** Store 'd' for detecting 'dd' vim instrcution */
                            setStoredChar('d');
                        }
                    }
                    break;
                case 'y':
                    if (caret.getSelectedText() != null) {
                        clipBoard = caret.getSelectedText();
                        caret.removeSelection();
                    } else {
                        if (getStoredChar() == 'y') {
                            int start = caret.getVisualLineStart();
                            int end = caret.getVisualLineEnd();
                            caret.setSelection(start, end);
                            clipBoard = caret.getSelectedText();
                            caret.removeSelection();
                            /** Store arbitrary charecter 'x' for recovering initial condition */
                            setStoredChar('x');
                        } else {
                            /** Store 'y' for detecting 'yy' vim instrcution */
                            setStoredChar('y');
                        }
                    }
                    break;
                case 'p':
                    if (clipBoard != null) {
                        String original = clipBoard;
                        if (clipBoard.charAt(clipBoard.length() - 1) != '\n')
                            clipBoard = clipBoard + '\n';
                        if (caret.getVisualLineEnd() == editor.getDocument().getText().length()) {
                            clipBoard = '\n' + clipBoard;
                        }
                        editor.getDocument().replaceString(caret.getVisualLineEnd(), caret.getVisualLineEnd(), clipBoard);
                        clipBoard = original;
                        caret.moveToOffset(caret.getVisualLineEnd());
                    }
                    break;
                case 'P':
                    if (clipBoard != null) {
                        editor.getDocument().replaceString(caret.getOffset(), caret.getOffset(), clipBoard);
                    }
                    break;
                case 'f':
                    Component component = MyToolWindowFactory.getToolWindow().getComponent();
                    component.transferFocus();

                default:
                    modeEnum.setMode(modeEnum.modeType.NORMAL);
                    modeViewer(editor);
            }
    }


    /**
     * In modern vim plugin, there exists a convention which represents cursor
     * with single line in the INSERT MODE, and represents by block in the
     * NORMAL MODE. This method sets proper cursor types depending on the current
     * MODE.
     * @param editor Opened editor
     */
    private void setProperCursorShape(Editor editor) {
        if(modeEnum.getModeToString() == "INSERT MODE" ) {
            editor.getSettings().setBlockCursor(false);
        }
        else {
            editor.getSettings().setBlockCursor(true);
        }
    }


    /**
     * There are several moving-control shortcuts in the NORMAL MODE of vim. For example,
     * 'h, j, k, l,...'. This method moves cursor depending on the current input shortcut.
     * @param charTyped the argument which denotes the shortcuts for moving cursor in the NORMAL MODE
     * @param editor Opened editor
     */
    private void moveCursor(char charTyped, Editor editor){
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        try {
            //move under line
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


    /**
     * This method continuously make popup to show user the current MODE.
     * @param editor Opened editor
     */
    private void modeViewer(Editor editor){
        JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
        JBPopup mes = jbPopupFactory.createMessage(modeEnum.getModeToString());
        mes.setRequestFocus(false);
        mes.show(RelativePoint.getSouthEastOf(editor.getComponent()));
    }


    /**
     * This methods performs the functionality of the COMMAND MODE. It shows popup
     * and prepare for the upcoming command input.
     * @param command ':' or '/', denotes the type of the COMMAND MODE
     * @param editor Opened editor
     */
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
        // As our vim plugin creates new popup each time the user enters COMMAND MODE,
        // it also defines new listeners every time.
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
                    /** Store arbitrary charecter 'x' for recovering initial condition */
                    setStoredChar('x');
                    modeEnum.setMode(modeEnum.modeType.NORMAL);
                    modeViewer(editor);
                    popup.closeOk(e);
                    popup.canClose();
                    isESC = true;
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(textField.getText().substring(0, 1).equals("/"))
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

    /**
     * When the user use '/' COMMAND MODE and search some string, search all instances
     * of the string and store it in array. And then go to the first entry of the List using
     * 'focusNextSearchString' method.
     * @param editor Opened editor
     */
    private void searchString(Editor editor){
        String text = editor.getDocument().getText();
        while(text.contains(currentSearchingString)){
            int slicedIndex = text.indexOf(currentSearchingString);
            int beginIndex = slicedIndex + (editor.getDocument().getText().length() - text.length());
            searchList.add(beginIndex);
            text = text.substring(slicedIndex + currentSearchingString.length());
        }
        currentIndex = searchList.size() - 1;
        modeEnum.setMode(modeEnum.modeType.NORMAL);
        focusNextSearchString(editor, true);

    }


    /**
     * When the user searches something, he or she can easily access to the searched
     * strings by pressing 'n' and 'N' command. This method implements those functionality.
     * @param editor Opened Editor
     * @param isSearchingNext if true, searches forward. Else, searches backward.
     */
    private void focusNextSearchString(Editor editor, boolean isSearchingNext) {
        int start, end;
        if(searchList.size() > 0) {
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
            start = searchList.get(currentIndex);
            end = start + currentSearchingString.length();
            editor.getCaretModel().getCurrentCaret().setSelection(start, end);
            editor.getCaretModel().getCurrentCaret().moveToOffset(start);
        }
    }


    /**
     * There exists various ways to enter the insertion mode. This function make our vim
     * plugin to enter insertion mode, with slightly different functionality among
     * different shortcuts.
     * @param editor Opened Editor
     * @param type type of the way entering the Insertion mode
     */
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


    /** Helpers for the function changeCaretTtInsertionMode() */

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

    /** end of helpers */

}