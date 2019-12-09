package project_Team7.Handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import project_Team7.VIMMode;

import static project_Team7.EditorTypedHandler.*;

public class TextEditHandler implements TypedHandler {

    private String clipBoard = "";

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        Caret caret = editor.getCaretModel().getCurrentCaret();
        int exeNum;
        if(getMultiExecute() == 0) exeNum = 1;
        else exeNum = getMultiExecute();
        switch(charTyped) {

            case 'd':
                if (caret.getSelectedText() != null) {
                    clipBoard = caret.getSelectedText();
                    editor.getDocument().replaceString(caret.getSelectionStart(), caret.getSelectionEnd(), "");
                } else {
                    if (getStoredChar() == 'd') {
                        clipBoard = "";

                        for(int i = 0; i < exeNum; i++) {
                            int start = caret.getVisualLineStart();
                            int end = caret.getVisualLineEnd();
                            caret.setSelection(start, end);
                            clipBoard += caret.getSelectedText();
                            editor.getDocument().replaceString(start, end, "");
                        }

                        setStoredChar('x');
                        setMultiExecute(0);
                    } else {

                        setStoredChar('d');
                    }
                }
                VIMMode.setMode(VIMMode.modeType.NORMAL);
                modeViewer(editor);
                break;
            case 'y':
                if (caret.getSelectedText() != null) {
                    clipBoard = caret.getSelectedText();
                    caret.removeSelection();
                } else {
                    if (getStoredChar() == 'y') {
                        clipBoard = "";
                        int originalCaretOffset = caret.getOffset();
                        for(int i = 0; i < exeNum; i++) {
                            int start = caret.getVisualLineStart();
                            int end = caret.getVisualLineEnd();
                            caret.setSelection(start, end);
                            clipBoard += caret.getSelectedText();
                            caret.moveToOffset(caret.getVisualLineEnd() + 1);
                            caret.removeSelection();
                        }
                        setMultiExecute(0);
                        caret.moveToOffset(originalCaretOffset);
                        /** Store arbitrary charecter 'x' for recovering initial condition */
                        setStoredChar('x');
                    } else {
                        /** Store 'y' for detecting 'yy' vim instrcution */
                        setStoredChar('y');
                    }
                }
                VIMMode.setMode(VIMMode.modeType.NORMAL);
                modeViewer(editor);
                editor.getSelectionModel().removeSelection();
                break;
            case 'p':
                if (clipBoard != null) {
                    String original = clipBoard;
                    if (clipBoard.charAt(clipBoard.length() - 1) != '\n')
                        clipBoard = clipBoard + '\n';
                    if (caret.getVisualLineEnd() == editor.getDocument().getText().length()) {
                        clipBoard = '\n' + clipBoard;
                    }

                    for(int i = 0; i < exeNum; i++) {
                        editor.getDocument().replaceString(caret.getVisualLineEnd(), caret.getVisualLineEnd(), clipBoard);
                        caret.moveToOffset(caret.getVisualLineEnd());
                        System.out.println(exeNum);
                    }
                    setMultiExecute(0);
                    clipBoard = original;
                }
                VIMMode.setMode(VIMMode.modeType.NORMAL);
                modeViewer(editor);
                break;
            case 'P':
                if (clipBoard != null) {
                    if(clipBoard.charAt(clipBoard.length() - 1) != '\n')
                        clipBoard = clipBoard + '\n';
                    for(int i = 0; i < exeNum; i++) {
                        editor.getDocument().replaceString(caret.getOffset()+ 1, caret.getOffset() + 1, clipBoard);
                        caret.moveToOffset(caret.getOffset() + clipBoard.length() - 1);
                    }
                    setMultiExecute(0);
                }
                VIMMode.setMode(VIMMode.modeType.NORMAL);
                modeViewer(editor);
                break;
        }

    }
}
