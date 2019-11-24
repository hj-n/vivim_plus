package project_Team7;

import com.android.tools.layoutlib.annotations.NotNull;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;

public class MyInsertModeHandler implements TypedActionHandler {
    @Override

    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
        MyTypedHandler myTypedHandler = new MyTypedHandler();
        if(c == '='){
            System.out.println("in here");
            myTypedHandler.setStoredChar('x');
        }
        else{
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 2);
            final Document document = editor.getDocument();
            final Project project = editor.getProject();

            Runnable runnable = () -> document.insertString(caret.getOffset(),String.valueOf(c));
            WriteCommandAction.runWriteCommandAction(project, runnable);
            caret.moveToVisualPosition(visualPosition);
        }
    }
}

