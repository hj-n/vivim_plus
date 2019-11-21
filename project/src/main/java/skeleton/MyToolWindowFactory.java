package skeleton;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * A factory to create a Project Structure tool window.
 */
public class MyToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ProjectStructureWindow psw = new ProjectStructureWindow();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        //Content content = contentFactory.createContent(new JBScrollPane(), "", false);
        Content contentWindow = contentFactory.createContent(psw.getContent(), "", false);
        //toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().addContent(contentWindow);
    }
}
