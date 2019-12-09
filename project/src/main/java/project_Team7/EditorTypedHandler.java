package project_Team7;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.awt.RelativePoint;

import org.jetbrains.annotations.NotNull;


import project_Team7.HandlerMap.TypedHandlerMap;
import project_Team7.Handlers.TypedHandler;

import java.awt.*;
import java.util.Map;

public class EditorTypedHandler implements TypedActionHandler {


    private static char storedChar = 'x';
    private InsertModeHandler myInsertModeHandler = new InsertModeHandler();
    private boolean hasDocumentListener = false;
    private static String recentDeletedString = null;
    private static Integer multiExecute = 0;

    private Map<Pair<String, Character>, TypedHandler> handlerMap = TypedHandlerMap.getMap();

    public EditorTypedHandler() {
    }


    public static String getRecentDeletedString() {
        return recentDeletedString;
    }

    public void setRecentTypedString(String s) {
    }

    public void setRecentDeletedString(String s) {
        recentDeletedString = s;
    }

    public static void setStoredChar(char c){
        storedChar = c;
    }

    public static char getStoredChar(){
        return storedChar;
    }

    public static Integer getMultiExecute() {
        return multiExecute;
    }

    public static void setMultiExecute(Integer multiExecute) {
        EditorTypedHandler.multiExecute = multiExecute;
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
                }
            });
            VIMMode.setMode(VIMMode.modeType.NORMAL);
            hasDocumentListener = true;
        }
        /** add listener to detect ESC, BACKSPACE, and ENTER input */
        myInsertModeHandler.addKeyListener(editor);

        Caret caret = editor.getCaretModel().getCurrentCaret();
        if(getStoredChar() == 'i'){     /** INSERT MODE */
            VIMMode.setMode(VIMMode.modeType.INSERT);
            modeViewer(editor);
            myInsertModeHandler.execute(editor, charTyped);
        }
        else{   /** NORMAL MODE */
            normalModeControl(editor, charTyped, caret);
        }
        //added
        if(handlerMap.containsKey(new Pair<>(VIMMode.getModeToString(),charTyped)))
            handlerMap.get(new Pair<>(VIMMode.getModeToString(),charTyped)).execute(editor, charTyped, dataContext); //I will use this

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
        if(isNatural(charTyped+"") || charTyped == '0'){
            if(multiExecute != 0){
                multiExecute = multiExecute * 10 + Integer.parseInt(charTyped + "");
            }
            else{
                multiExecute = Integer.parseInt(charTyped+"");
            }
        }

//        VIMMode.setMode(VIMMode.modeType.NORMAL);
        modeViewer(editor);
        switch(charTyped) {

            case 'f':      // should change to uml window
                Component component = TreeWindowFactory.getToolWindow().getComponent();
                component.transferFocus();
                VIMMode.setMode(VIMMode.modeType.NORMAL);
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
        if(VIMMode.getModeToString() == "INSERT MODE" ) {
            editor.getSettings().setBlockCursor(false);
        }
        else {
            editor.getSettings().setBlockCursor(true);
        }
    }


    /**
     * This method continuously make popup to show user the current MODE.
     * @param editor Opened editor
     */
    private void modeViewer(Editor editor){
        JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
        JBPopup mes = jbPopupFactory.createMessage(VIMMode.getModeToString());
        mes.setRequestFocus(false);
        mes.show(RelativePoint.getSouthEastOf(editor.getContentComponent()));
    }

    private boolean isNatural(String strNum){
        if(strNum == null){
            return false;
        }
        try{
            Integer integer = Integer.parseInt(strNum);
            if(integer < 0)
                return false;
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    /** end of helpers */
}