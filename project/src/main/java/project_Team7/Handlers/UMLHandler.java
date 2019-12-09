package project_Team7.Handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import project_Team7.UMLWindowFactory;
import project_Team7.VIMMode;

import java.awt.*;

public class UMLHandler implements TypedHandler {
    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        try {
            if (!UMLWindowFactory.getToolWindow().isVisible()) {
                UMLWindowFactory.getToolWindow().show(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            } else {
                UMLWindowFactory.getToolWindow().hide(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }
        catch(NullPointerException e) {
            System.out.println("YOU SHOULD OPEN UML DIAGRAM AT LEAST ONCE BY MOUSE");
            return;
        }
        Component component = UMLWindowFactory.getToolWindow().getComponent();
        component.transferFocus();

        VIMMode.setMode(VIMMode.modeType.NORMAL);
        modeViewer(editor);
        setProperCursorShape(editor);

    }
}
