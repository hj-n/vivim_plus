package project_Team7.TypedHandler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import project_Team7.EditorTypedHandler;
import project_Team7.TreeWindowFactory;
import project_Team7.VIMMode;

public class TreeOpenHandler implements TypedHandler {

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        try {
            if (!TreeWindowFactory.getToolWindow().isVisible()) {
                TreeWindowFactory.getToolWindow().show(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            } else {
                TreeWindowFactory.getToolWindow().hide(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }
        catch(NullPointerException e) {
            System.out.println("YOU SHOULD OPEN TREE AT LEAST ONCE BY MOUSE");
        }
        VIMMode.setMode(VIMMode.modeType.NORMAL);
    }
}
