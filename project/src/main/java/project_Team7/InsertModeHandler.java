package project_Team7;

import com.android.tools.layoutlib.annotations.NotNull;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InsertModeHandler {

    private boolean isESC;
    private String input = null;
    VisualPosition visualPosition;
    private static boolean addedKeyListener = false;
    private static boolean enteredAfterInsertion = false;

    public static void setEnteredAfterInsertion(boolean enteredAfterInsertion) {
        InsertModeHandler.enteredAfterInsertion = enteredAfterInsertion;
    }


    /**
     * Add key listener for the input of ENTER, BACKSPACE, and ESC.
     * @param editor Opened editor
     * @param parentHandler main handler. The class (object) which called this function
     */
    public void addKeyListener(@NotNull Editor editor, EditorTypedHandler parentHandler) {
        if (!addedKeyListener) {
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            final Document document = editor.getDocument();
            final Project project = editor.getProject();

            /** Implementation of the keyListerner for ENTER, BACKSPACE, ESC key */
            editor.getContentComponent().addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {}
                @Override
                public void keyPressed(KeyEvent e) {}

                @Override
                public void keyReleased(KeyEvent e) {
                    try {
                        if(VIMMode.getModeToString() == "VISUAL MODE"){
                            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                /** Store arbitrary charecter 'x' for recovering initial condition */
                                parentHandler.setStoredChar('x');
                                VIMMode.setMode(VIMMode.modeType.NORMAL);
                                editor.getSelectionModel().removeSelection();
                            }
                        }
                        else if (VIMMode.getModeToString() == "INSERT MODE") {    /** When INSERT MODE */
                            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                if (parentHandler.getStoredChar() != 'x')
                                    editor.getSettings().setBlockCursor(true);
                                /** Store arbitrary charecter 'x' for recovering initial condition */
                                parentHandler.setStoredChar('x');
                                isESC = true;
                                VIMMode.setMode(VIMMode.modeType.NORMAL);
                            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                if (enteredAfterInsertion) {
                                    Runnable runnable = () -> document.insertString(caret.getOffset(), "\n");
                                    WriteCommandAction.runWriteCommandAction(project, runnable);
                                    visualPosition = new VisualPosition(caret.getVisualPosition().getLine() + 1, caret.getVisualLineStart());
                                    caret.moveToVisualPosition(visualPosition);
                                    enteredAfterInsertion = false;
                                    if(caret.getOffset() != caret.getVisualLineStart()) {
                                        caret.moveToOffset(caret.getVisualLineStart());
                                    }
                                }
                            }
                        }
                        else {   /** When NORMAL MODE : Immediately undo the things that BACKSPACE and ENTER did */
                            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                                visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
                                Runnable runnable = () -> document.insertString(caret.getOffset(), parentHandler.getRecentDeletedString());
                                WriteCommandAction.runWriteCommandAction(project, runnable);
                                caret.moveToVisualPosition(visualPosition);
                            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                Runnable runnable = () -> document.replaceString(caret.getVisualLineStart() - 1, caret.getOffset(), "");
                                WriteCommandAction.runWriteCommandAction(project, runnable);
                            }
                        }
                    } catch (Exception ignored) {}
                }
            });
            addedKeyListener = true;
        }
    }

    /**
     * called by the main handler. Especially handles the case of INSERT MODE.
     * When the current mode is the INSERT MODE, this function will be called and
     * write typed character on the editor.
     * @param editor Opened editor
     * @param c input char (typed char)
     */
    public void execute(@NotNull Editor editor, char c) {
        isESC = false;
        input = String.valueOf(c);
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        final Document document = editor.getDocument();
        final Project project = editor.getProject();
        if(!isESC){
            visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
            Runnable runnable = () -> document.insertString(caret.getOffset(),input);
            WriteCommandAction.runWriteCommandAction(project, runnable);
            caret.moveToVisualPosition(visualPosition);
            enteredAfterInsertion = true;
        }
    }
}
