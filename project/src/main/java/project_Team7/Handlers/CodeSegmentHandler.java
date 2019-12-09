package project_Team7.Handlers;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.ScrollType;
import org.jetbrains.annotations.NotNull;

public class CodeSegmentHandler implements CommandHandler {
    @Override
    public void executeCommand(String currentCommandInput, @NotNull Editor editor) {
        if(currentCommandInput.substring(0, 4).equals("show")) {
            handleCodeSegment(currentCommandInput,true, editor);
        }
        else if(currentCommandInput.substring(0, 4).equals("hide")) {
            handleCodeSegment(currentCommandInput, false, editor);
        }
    }

    private void handleCodeSegment(String currentCommandInput, boolean isShowing, Editor editor){
        currentCommandInput = currentCommandInput.substring(5);
        int rowNum;
        if(isNatural(currentCommandInput)) {
            rowNum = Integer.parseInt(currentCommandInput);
            for (FoldRegion f : editor.getFoldingModel().getAllFoldRegions()) {
                if (editor.getDocument().getLineNumber(f.getStartOffset()) + 1 == rowNum && f.isExpanded() == !isShowing) {
                    editor.getFoldingModel().runBatchFoldingOperation(() -> f.setExpanded(isShowing));
                    editor.getCaretModel().getCurrentCaret().moveToOffset(f.getStartOffset());
                    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
                }
            }
        }
    }

    private boolean isNatural(String strNum){
        if(strNum == null){
            return false;
        }
        try{
            Integer integer = Integer.parseInt(strNum);
            if(integer < 0)
                return false;
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

}
