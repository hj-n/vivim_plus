package project_Team7.TypedHandler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import org.jetbrains.annotations.NotNull;
import project_Team7.HandlerMap.CommandHandlerMap;
import project_Team7.VIMMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Map;

import static project_Team7.EditorTypedHandler.*;


public class CommandMapHandler implements TypedHandler {

    private String currentCommand = null;
    private String currentSearchingString = null;
    private ArrayList<Integer> searchList = new ArrayList<>();
    private int currentIndex;
    private JPanel commandPanel = null;
    private Map<String, CommandHandler> handlerMap = CommandHandlerMap.getMap();
    private boolean isSearch = false;

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        switch (charTyped) {
            case ':':
            case '/':
                VIMMode.setMode(VIMMode.modeType.COMMAND);
                keyStrokeCommandMode(charTyped + " ", editor);
                modeViewer(editor);
                break;
            case 'n':
                if (VIMMode.getModeToString().equals("NORMAL MODE")) {
                    focusNextSearchString(editor, true);
                }
                break;
            case 'N':
                if (VIMMode.getModeToString().equals("NORMAL MODE")) {
                    focusNextSearchString(editor, false);
                }
                break;
        }
        VIMMode.setMode(VIMMode.modeType.NORMAL);
        modeViewer(editor);
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
        /** As our vim plugin creates new popup each time the user enters COMMAND MODE,
         *it also defines new listeners every time.
         */
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
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    /** Store arbitrary charecter 'x' for recovering initial condition */
                    setStoredChar('x');
                    VIMMode.setMode(VIMMode.modeType.NORMAL);
                    modeViewer(editor);
                    popup.closeOk(e);
                    popup.canClose();
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(textField.getText().substring(0, 1).equals("/"))
                        searchString(editor);
                    if(textField.getText().substring(0, 1).equals(":"))
                        handleCommands(editor);
                    popup.closeOk(e);
                    popup.canClose();

                }
                else{
                    if(command.equals(": ")) {
                        if(textField.getText().length() <= 2) {
                            textField.setText(": ");
                        }
                        currentCommand = textField.getText().substring(2);
                    }
                    else {
                        if(textField.getText().length() <= 2) {
                            textField.setText("/ ");
                        }
                        currentSearchingString = textField.getText().substring(2);
                    }
                }
            }
        });
    }


    /**
     * This method is called when the user types ENTER key in the COMMAND MODE.
     * It parse the input string, and calls corresponding sub-handling function
     * for each input command
     * @param editor Opened editor
     */
    private void handleCommands(Editor editor) {
        String currentCommandInput = currentCommand;
        int spaceIndex = currentCommandInput.indexOf(" ");
        System.out.println(spaceIndex); //debugging
        String key;
        if(isSearch) {
            key = "/";
        }
        else if(spaceIndex > 0) {      // Commands with function & argument (ex) move 9-1)
            key = currentCommandInput.substring(0, spaceIndex);
        }
        else {
            key = currentCommandInput;
        }
        if(handlerMap.containsKey(key))
            handlerMap.get(key).executeCommand(currentCommandInput, editor);
        else if(isNatural(currentCommandInput)){
            handleMoveLine(Integer.parseInt(currentCommandInput), editor);
        }
        setProperCursorShape(editor);
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

    private void handleMoveLine(int rowNum, Editor editor){
        Caret caret = editor.getCaretModel().getCurrentCaret();
        caret.moveToOffset(rowNum);
        caret.moveToVisualPosition(new VisualPosition(rowNum - 1, 0));
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
        System.out.println("rowNum is " + rowNum);
    }

    /**
     * When the user use '/' COMMAND MODE and search some string, search all instances
     * of the string and store it in array. And then go to the first entry of the List using
     * 'focusNextSearchString' method.
     * @param editor Opened editor
     */
    private void searchString(Editor editor){
        String text = editor.getDocument().getText();
        searchList.clear();
        while(text.contains(currentSearchingString)){
            int slicedIndex = text.indexOf(currentSearchingString);
            int beginIndex = slicedIndex + (editor.getDocument().getText().length() - text.length());
            searchList.add(beginIndex);
            text = text.substring(slicedIndex + currentSearchingString.length());
        }
        currentIndex = searchList.size() - 1;
        VIMMode.setMode(VIMMode.modeType.NORMAL);
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
        int exeNum = 0;
        if(getMultiExecute() == 0) exeNum = 1;
        else exeNum = getMultiExecute();
        for(int i = 0; i < exeNum; i++) {

            if (searchList.size() > 0) {
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
        setMultiExecute(0);
    }
}
