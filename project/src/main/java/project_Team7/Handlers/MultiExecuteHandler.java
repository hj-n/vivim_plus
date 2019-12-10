package project_Team7.Handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import static project_Team7.EditorTypedHandler.getMultiExecute;
import static project_Team7.EditorTypedHandler.setMultiExecute;

public class MultiExecuteHandler implements TypedHandler {

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        if(isNatural(charTyped+"") || charTyped == '0'){
            if(getMultiExecute() != 0){
                setMultiExecute(getMultiExecute() * 10 + Integer.parseInt(charTyped + ""));
            }
            else{
                setMultiExecute(Integer.parseInt(charTyped+""));
            }
        }
        modeViewer(editor);
    }

    private boolean isNatural(String strNum){
        if(strNum == null){
            return false;
        }
        try{
            int integer = Integer.parseInt(strNum);
            if(integer < 0)
                return false;
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
}
