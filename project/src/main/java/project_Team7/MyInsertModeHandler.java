package project_Team7;

import android.inputmethodservice.KeyboardView;
import com.android.tools.layoutlib.annotations.NotNull;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import org.jsoup.select.Evaluator;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyInsertModeHandler implements TypedActionHandler {

    private boolean isESC;
    private String input = null;
    VisualPosition visualPosition;
    private static boolean addedKeyListener = false;
    private static boolean enteredAfterInsertion = false;

    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
        MyTypedHandler myTypedHandler = new MyTypedHandler();
        //myTypedHandler.isTypedESC(editor);
        isESC = false;
        input = String.valueOf(c);
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        final Document document = editor.getDocument();
        final Project project = editor.getProject();
        if(addedKeyListener == false) {
            editor.getContentComponent().addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        //System.out.println("typed esc key");
                        if (myTypedHandler.getStoredChar() != 'x')
                            changeCaretToNormalMode(editor);
                        myTypedHandler.setStoredChar('x');
                        isESC = true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if(enteredAfterInsertion) {
                            visualPosition = new VisualPosition(caret.getVisualPosition().getLine() + 1, caret.getVisualPosition().getColumn());
                            caret.moveToVisualPosition(visualPosition);
                            //editor.getDocument().replaceString(caret.getOffset(), caret.getOffset(), "\n");
                            enteredAfterInsertion = false;
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
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        if(caret.getVisualLineStart() < caret.getOffset()) {
            caret.setSelection(caret.getOffset() - 1, caret.getOffset());
            caret.setVisualAttributes(new CaretVisualAttributes(editor.getColorsScheme().getDefaultBackground(), CaretVisualAttributes.Weight.THIN));
        }
        else {
            caret.setSelection(caret.getOffset(), caret.getOffset());
            caret.setVisualAttributes(new CaretVisualAttributes(new Color(88, 115, 173), CaretVisualAttributes.Weight.HEAVY));

        }
    }
}

