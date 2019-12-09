package project_Team7.TypedHandler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.VisualPosition;
import org.jetbrains.annotations.NotNull;
import project_Team7.VIMMode;

import static project_Team7.EditorTypedHandler.*;

public class CursorVisualHandler implements TypedHandler {

    private int initialVisualOffsetStart = 0;
    private int initialVisualOffsetEnd = 0;

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        Caret caret = editor.getCaretModel().getCurrentCaret();
        switch(charTyped) {
            case 'v':
                setStoredChar('v');
                VIMMode.setMode(VIMMode.modeType.VISUAL);
                modeViewer(editor);
                initialVisualOffsetStart = caret.getOffset();
                break;
            case 'V':
                setStoredChar('V');
                VIMMode.setMode(VIMMode.modeType.VISUAL);
                modeViewer(editor);
                initialVisualOffsetStart = caret.getVisualLineStart();
                initialVisualOffsetEnd = caret.getVisualLineEnd();
                editor.getSelectionModel().setSelection(initialVisualOffsetStart, initialVisualOffsetEnd);
                break;
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case '$':
            case 'w':
            case 'b':
                moveCursor(charTyped, editor);
                modeViewer(editor);
                if(getStoredChar() == 'v'){
                    editor.getSelectionModel().setSelection(initialVisualOffsetStart, caret.getOffset());
                }
                else if(getStoredChar() == 'V'){
                    if(initialVisualOffsetStart < caret.getVisualLineEnd())
                        editor.getSelectionModel().setSelection(initialVisualOffsetStart, caret.getVisualLineEnd());
                    else
                        editor.getSelectionModel().setSelection(caret.getVisualLineStart(), initialVisualOffsetEnd);
                }
        }

    }

    /**
     * There are several moving-control shortcuts in the NORMAL MODE of vim. For example,
     * 'h, j, k, l,...'. This method moves cursor depending on the current input shortcut.
     * @param charTyped the argument which denotes the shortcuts for moving cursor in the NORMAL MODE
     * @param editor Opened editor
     */
    private void moveCursor(char charTyped, Editor editor){
        int exeNum;
        String text = editor.getDocument().getText();
        if(getMultiExecute() == 0) exeNum = 1;
        else exeNum = getMultiExecute();
        for(int i = 0; i < exeNum; i++ ) {
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            try {
                //move under line
                setMultiExecute(0);
                boolean seperate = false;
                if (charTyped == 'j') {
                    VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine() + 1, caret.getVisualPosition().getColumn());
                    caret.moveToVisualPosition(visualPosition);
                }
                //move upper line
                if (charTyped == 'k') {
                    VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine() - 1, caret.getVisualPosition().getColumn());
                    caret.moveToVisualPosition(visualPosition);
                }
                //move left
                if (charTyped == 'h') {
                    VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() - 1);
                    caret.moveToVisualPosition(visualPosition);
                }
                //move right
                if (charTyped == 'l') {
                    VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
                    caret.moveToVisualPosition(visualPosition);
                }
                //move line end
                if(charTyped == '$'){
                    caret.moveToOffset(caret.getVisualLineEnd() - 1);
                    if(caret.getVisualLineStart() != caret.getOffset()){
                        caret.moveToOffset(caret.getOffset() - 1);
                    }
                }
                //move forwardly by one word
                if(charTyped == 'w'){
                    char tempChar = text.charAt(caret.getOffset());
                    int j = caret.getOffset() + 1;
                    for(; j < text.length(); j ++){
                        if(Character.isLetterOrDigit(text.charAt(j)) ^ Character.isLetterOrDigit(tempChar)) {
                            break;
                        }
                        else{
                            tempChar = text.charAt(j);
                        }
                    }
                    caret.moveToOffset(j);
                }
                //move backwardly by one word
                if(charTyped == 'b'){
                    char tempChar = text.charAt(caret.getOffset());
                    int j = caret.getOffset() - 1;
                    Boolean check = false;
                    for(; j > 0; j --){
                        if(Character.isLetterOrDigit(text.charAt(j)) ^ Character.isLetterOrDigit(tempChar) && check) {
                            break;
                        }
                        else if(Character.isLetterOrDigit(text.charAt(j)) ^ Character.isLetterOrDigit(tempChar)) {
                            check = true;
                            tempChar = text.charAt(j);
                        }
                        else{
                            tempChar = text.charAt(j);
                        }
                    }
                    caret.moveToOffset(j + 1);
                }
                //editor.getCaretModel().getCurrentCaret().moveToOffset(.getStartOffset());
                editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
            }
            catch(Exception e){

            }
        }
    }
}
