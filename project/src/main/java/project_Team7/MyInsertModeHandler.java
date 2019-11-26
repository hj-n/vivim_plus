package project_Team7;

import android.inputmethodservice.KeyboardView;
import com.android.tools.layoutlib.annotations.NotNull;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.impl.UndoManagerImpl;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import org.jsoup.select.Evaluator;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyInsertModeHandler {

    private boolean isESC;
    private String input = null;
    VisualPosition visualPosition;
    private static boolean addedKeyListener = false;
    private static boolean enteredAfterInsertion = false;

    public static void setEnteredAfterInsertion(boolean enteredAfterInsertion) {
        MyInsertModeHandler.enteredAfterInsertion = enteredAfterInsertion;
    }

    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext, MyTypedHandler parentHandler) {
        MyTypedHandler myTypedHandler = new MyTypedHandler();
        //myTypedHandler.isTypedESC(editor);
        isESC = false;
        input = String.valueOf(c);
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        final Document document = editor.getDocument();
        final Project project = editor.getProject();
        if(addedKeyListener == false) {
            System.out.println("Listener allocation completed!!");
            editor.getContentComponent().addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if(modeEnum.getModeToString() == "INSERT MODE") {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            if (myTypedHandler.getStoredChar() != 'x')
                                changeCaretToNormalMode(editor);
                            myTypedHandler.setStoredChar('x');
                            isESC = true;
                        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            if (enteredAfterInsertion) {
                                Runnable runnable = () -> document.insertString(caret.getOffset(), "\n");
                                WriteCommandAction.runWriteCommandAction(project, runnable);
                                visualPosition = new VisualPosition(caret.getVisualPosition().getLine() + 1, caret.getVisualLineStart());
                                caret.moveToVisualPosition(visualPosition);
                                enteredAfterInsertion = false;
                            }
                        }
                    }
                    else {
                        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                            visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
                            Runnable runnable = () -> document.insertString(caret.getOffset(), parentHandler.getRecentDeletedString());
                            WriteCommandAction.runWriteCommandAction(project, runnable);
                            caret.moveToVisualPosition(visualPosition);
                        }
                       else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                            if(parentHandler.getRecentTypedString().equals("\n")) {
                                Runnable runnable = () -> document.replaceString(caret.getOffset() - 1, caret.getOffset(), "");
                                WriteCommandAction.runWriteCommandAction(project, runnable);
                            }
                            /*visualPosition = new VisualPosition(caret.getVisualPosition().getLine() - 1, caret.getVisualPosition().getColumn());
                            Runnable runnable = () -> document.insertString(caret.getOffset(), "");
                            WriteCommandAction.runWriteCommandAction(project, runnable);
                            caret.moveToVisualPosition(visualPosition);*/
                        }


                    }

                }
            });
        }
        addedKeyListener = true;
        if(!isESC){
            visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
            Runnable runnable = () -> document.insertString(caret.getOffset(),input);
            WriteCommandAction.runWriteCommandAction(project, runnable);
            caret.moveToVisualPosition(visualPosition);
            enteredAfterInsertion = true;

        }

    }
    public void changeCaretToNormalMode(Editor editor) {
            editor.getSettings().setBlockCursor(true);
    }
}

