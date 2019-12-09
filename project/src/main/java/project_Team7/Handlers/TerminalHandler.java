package project_Team7.Handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TerminalHandler implements TypedHandler {
    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        ToolWindowManager windowManager = ToolWindowManager.getInstance(getActiveProject());
        final ToolWindow window = windowManager.getToolWindow("Terminal");
        if(window.isVisible()) {
            window.hide(null);
        }
        else {
            window.activate(null);
        }
    }

    @NotNull
    private Project getActiveProject() {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            if (window != null && window.isActive())
                return project;
        }
        // if there is no active project, return an arbitrary project (the first)
        return ProjectManager.getInstance().getOpenProjects()[0];
    }
}
