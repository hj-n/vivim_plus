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

    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
        MyTypedHandler myTypedHandler = new MyTypedHandler();
        //myTypedHandler.isTypedESC(editor);
        isESC = false;
        editor.getContentComponent().addKeyListener(new KeyListener() {
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
                    System.out.println("typed esc key");
                    if(myTypedHandler.getStoredChar() != 'x')
                        changeCaretToNormalMode(editor);
                    myTypedHandler.setStoredChar('x');

                    isESC = true;
                }
            }
        });
        if(!isESC){
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
            final Document document = editor.getDocument();
            final Project project = editor.getProject();

            Runnable runnable = () -> document.insertString(caret.getOffset(),String.valueOf(c));
            WriteCommandAction.runWriteCommandAction(project, runnable);
            caret.moveToVisualPosition(visualPosition);
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

