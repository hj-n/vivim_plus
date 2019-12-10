package project_Team7.Handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import project_Team7.VIMMode;

import static project_Team7.EditorTypedHandler.setStoredChar;


public class NormalToInsertHandler implements TypedHandler {

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        changeCaretToInsertionMode(editor, getInsertionTypeFromChar(charTyped));
        VIMMode.setMode(VIMMode.modeType.INSERT);
        modeViewer(editor);
        InsertModeHandler.setEnteredAfterInsertion(true);
        /** Store 'i' for detecting current mode is INSERT MODE */
        setStoredChar('i');
    }

    /**
     * There exists various ways to enter the insertion mode. This function make our vim
     * plugin to enter insertion mode, with slightly different functionality among
     * different shortcuts.
     * @param editor Opened Editor
     * @param type type of the way entering the Insertion mode
     */
    private void changeCaretToInsertionMode(Editor editor, enterInsertionType type) {
        int position = 0;
        switch(type) {
            case i :
                position = editor.getCaretModel().getCurrentCaret().getSelectionStart();
                break;
            case I :
                position = editor.getCaretModel().getCurrentCaret().getVisualLineStart();
                break;
            case a :
                position = editor.getCaretModel().getCurrentCaret().getSelectionStart() + 1;
                break;
            case A :
                position = editor.getCaretModel().getCurrentCaret().getVisualLineEnd() - 1;
                break;
            case o :
                Caret caret = editor.getCaretModel().getCurrentCaret();
                editor.getDocument().replaceString(caret.getVisualLineEnd(), caret.getVisualLineEnd(), "\n");
                position = editor.getCaretModel().getVisualLineEnd();
                break;
            case O :
                Caret caret2 = editor.getCaretModel().getCurrentCaret();
                if(caret2.getOffset() == caret2.getVisualLineStart()) {
                    editor.getDocument().replaceString(caret2.getVisualLineStart(), caret2.getVisualLineStart(), "\n");
                    position = editor.getCaretModel().getVisualLineStart();
                }
                else {
                    editor.getDocument().replaceString(caret2.getVisualLineStart(), caret2.getVisualLineStart(), "\n");
                    position = editor.getCaretModel().getVisualLineStart() - 1;
                }
                break;

        }
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        caret.setSelection(position, position);
        caret.moveToOffset(position);
        editor.getSettings().setBlockCursor(false);
    }

    /**
     * Helpers for the insertionType:
     * There are 6 ways to enter the INSERT MODE from the NORMAL mode.
     * The code differentiate them by using these enum type
     */

    private enum enterInsertionType {
        o, O, i, I, a, A
    }

    private enterInsertionType getInsertionTypeFromChar(char c) {
        switch (c) {
            case 'i':
                return enterInsertionType.i;
            case 'I':
                return enterInsertionType.I;
            case 'a':
                return enterInsertionType.a;
            case 'A':
                return enterInsertionType.A;
            case 'o':
                return enterInsertionType.o;
            case 'O':
                return enterInsertionType.O;
        }
        return null;
    }
}
