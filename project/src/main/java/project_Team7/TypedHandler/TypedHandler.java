package project_Team7.TypedHandler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import project_Team7.EditorTypedHandler;
import project_Team7.VIMMode;

public abstract class TypedHandler {
    abstract public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext);

    /**
     * In modern vim plugin, there exists a convention which represents cursor
     * with single line in the INSERT MODE, and represents by block in the
     * NORMAL MODE. This method sets proper cursor types depending on the current
     * MODE.
     * @param editor Opened editor
     */

    protected void setProperCursorShape(Editor editor) {
        if(VIMMode.getModeToString() == "INSERT MODE" ) {
            editor.getSettings().setBlockCursor(false);
        }
        else {
            editor.getSettings().setBlockCursor(true);
        }
    }

    /**
     * This method continuously make popup to show user the current MODE.
     * @param editor Opened editor
     */
    protected void modeViewer(Editor editor){
        JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
        JBPopup mes = jbPopupFactory.createMessage(VIMMode.getModeToString());
        mes.setRequestFocus(false);
        mes.show(RelativePoint.getSouthEastOf(editor.getContentComponent()));
    }
}
