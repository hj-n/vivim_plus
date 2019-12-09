package project_Team7;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import org.jetbrains.annotations.NotNull;

/**
 * A factory to create a UML tool window.
 */
public class UMLWindowFactory implements ToolWindowFactory {


    /**
     * There should be a way to access the UML window in other classes.
     * To provide this condition, this class has static variable that
     * saves the reference to the UML window.
     */
    private static ToolWindow toolWindow;


    public static ToolWindow getToolWindow() {
        return toolWindow;
    }


    /**
     * create UML window and add it to the project.
     * Single execution at the initialization of the UML Graph.
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // TODO: add new vim mode for UML Window and Project Structure Window
        UMLWindow umlWindow = new UMLWindow();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content content = contentFactory.createContent(umlWindow.getContent(), "", false);
        UMLWindowFactory.toolWindow = toolWindow;
        toolWindow.getContentManager().addContent(content);
    }
}
