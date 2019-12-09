package project_Team7.Handlers;

import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public interface CommandHandler {
    void executeCommand(String currentCommandInput, @NotNull Editor editor);
}
