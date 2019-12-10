package project_Team7.Handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;
import project_Team7.HandlerMap.CommandHandlerMap;
import project_Team7.VIMMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import static project_Team7.EditorTypedHandler.*;


public class CommandMapHandler implements TypedHandler {

    private String command = null;
    private String currentCommand = null;
    private JPanel commandPanel = null;
    private JTextField textField;
    private JBPopupFactory a;
    private JBPopup popup;
    private Map<String, CommandHandler> handlerMap = CommandHandlerMap.getMap();
    private boolean isSearch = false;
    private Editor editor;

    public CommandMapHandler() {
        commandPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        a = JBPopupFactory.getInstance();

        /** As our vim plugin creates new popup each time the user enters COMMAND MODE,
         *it also defines new listeners every time.
         */
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
                        isSearch =true;
                    else if(textField.getText().substring(0, 1).equals(":"))
                        isSearch = false;
                    handleCommands(editor);
                    popup.closeOk(e);
                    popup.canClose();

                }
                else{
                    if(textField.getText().length() <= 2) {
                        textField.setText(command);
                    }
                    currentCommand = textField.getText().substring(2);
                }
            }
        });
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        VIMMode.setMode(VIMMode.modeType.COMMAND);
        keyStrokeCommandMode(charTyped + " ", editor);
        modeViewer(editor);
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
        this.editor = editor;
        this.command = command;
        textField.setText(command);
        textField.setEditable(true);
        popup = a.createComponentPopupBuilder(commandPanel, textField).createPopup();
        commandPanel.add(textField);

        popup.setRequestFocus(true);
        popup.setSize(new Dimension(editor.getComponent().getWidth(), 10));
        popup.showUnderneathOf(editor.getComponent());
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
        String key;
        if(isSearch) {
            key = "/";
        }
        else if(spaceIndex > 0) {      // Commands with function & argument (ex) move 9-1)
            key = currentCommandInput.substring(0, spaceIndex);
        }
        else if(isNatural(currentCommandInput)) {
            key = " ";
        }
        else {
            key = currentCommandInput;
        }
        if(handlerMap.containsKey(key))
            handlerMap.get(key).executeCommand(currentCommandInput, editor);

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
}
