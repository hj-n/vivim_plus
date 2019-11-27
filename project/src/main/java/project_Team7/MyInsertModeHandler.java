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

public class MyInsertModeHandler {

    private boolean isESC;
    private String input = null;
    VisualPosition visualPosition;
    private static boolean addedKeyListener = false;
    private static boolean enteredAfterInsertion = false;

    public static void setEnteredAfterInsertion(boolean enteredAfterInsertion) {
        MyInsertModeHandler.enteredAfterInsertion = enteredAfterInsertion;
    }

    public void addKeyListener(@NotNull Editor editor, MyTypedHandler parentHandler) {
        if (!addedKeyListener) {
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            final Document document = editor.getDocument();
            final Project project = editor.getProject();
            editor.getContentComponent().addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    try {
                        if (modeEnum.getModeToString() == "INSERT MODE") {
                            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                if (parentHandler.getStoredChar() != 'x')
                                    changeCaretToNormalMode(editor);
                                parentHandler.setStoredChar('x');
                                isESC = true;
                                modeEnum.setMode(modeEnum.modeType.NORMAL);
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
                        } else {
                            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                                visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
                                Runnable runnable = () -> document.insertString(caret.getOffset(), parentHandler.getRecentDeletedString());
                                WriteCommandAction.runWriteCommandAction(project, runnable);
                                caret.moveToVisualPosition(visualPosition);
                            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                                if (parentHandler.getRecentTypedString().length() == (caret.getOffset() - caret.getVisualLineStart() + 1)) {
                                    Runnable runnable = () -> document.replaceString(caret.getOffset() - parentHandler.getRecentTypedString().length(), caret.getOffset(), "");
                                    WriteCommandAction.runWriteCommandAction(project, runnable);
                                }
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            });
            addedKeyListener = true;
        }
    }

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

    public void changeCaretToNormalMode(Editor editor) {
            editor.getSettings().setBlockCursor(true);
    }
}

