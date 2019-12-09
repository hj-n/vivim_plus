package project_Team7.Handlers;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.VisualPosition;
import org.jetbrains.annotations.NotNull;

public class MoveLineHandler implements CommandHandler {
    @Override
    public void executeCommand(String currentCommandInput, @NotNull Editor editor) {
        int rowNum = Integer.parseInt(currentCommandInput);
        Caret caret = editor.getCaretModel().getCurrentCaret();
        caret.moveToOffset(rowNum);
        caret.moveToVisualPosition(new VisualPosition(rowNum - 1, 0));
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
        System.out.println("rowNum is " + rowNum);
    }
}
