package project_Team7.TypedHandler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import project_Team7.EditorTypedHandler;
import project_Team7.VIMMode;

import static project_Team7.EditorTypedHandler.getMultiExecute;
import static project_Team7.EditorTypedHandler.setMultiExecute;

public class MoveOpenTabHandler extends TypedHandler {
    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        if(charTyped == 'J')
            moveOpenedTab(true, editor);     // move to left tab
        else
            moveOpenedTab(false, editor);     // move to right tab

        setProperCursorShape(editor);
    }

    private void moveOpenedTab(boolean b, Editor editor) {
        int exeNum = 0;
        if (getMultiExecute() == 0) exeNum = 1;
        else exeNum = getMultiExecute();

        for (int j = 0; j < exeNum; j++) {

            FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(editor.getProject());
            VirtualFile[] files = manager.getWindows()[0].getFiles();
            VirtualFile currentFile = manager.getCurrentFile();

            int currentIndex = -1;
            int tabNum = files.length;
            for (int i = 0; i < tabNum; i++) {
                if (currentFile.getName().equals(files[i].getName())) {
                    currentIndex = i;
                    break;
                }
            }
            if (b) {
                if (currentIndex == 0)
                    manager.openFile(files[tabNum - 1], true);
                else
                    manager.openFile(files[currentIndex - 1], true);
            } else {
                if (currentIndex == tabNum - 1) {
                    manager.openFile(files[0], true);
                } else {
                    manager.openFile(files[currentIndex + 1], true);
                }
            }
            VIMMode.setMode(VIMMode.modeType.NORMAL);
            modeViewer(manager.getSelectedTextEditor());
        }
        setMultiExecute(0);
    }
}
