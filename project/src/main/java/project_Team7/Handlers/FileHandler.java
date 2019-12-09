package project_Team7.Handlers;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import project_Team7.VIMMode;

public class FileHandler implements CommandHandler {
    @Override
    public void executeCommand(String currentCommandInput, @NotNull Editor editor) {
        switch (currentCommandInput) {
            case "w":
                handleSaveFile(currentCommandInput, editor);
                break;
            case "q":
                handleCloseFile(currentCommandInput, editor);
                break;
            case "q!":
                handleForceCloseFile(currentCommandInput, editor);
                break;
            case "wq":
                handleSaveCloseFile(currentCommandInput, editor);
                break;
        }
    }

    private void handleSaveFile(String currentCommandInput, Editor editor){
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(editor.getProject());
        VirtualFile files[] = fileEditorManager.getSelectedFiles();
        PsiFile file = PsiManager.getInstance(editor.getProject()).findFile(files[0]);
        FileDocumentManager.getInstance().saveDocument(PsiDocumentManager.getInstance(editor.getProject()).getDocument(file));
        VIMMode.setMode(VIMMode.modeType.NORMAL);
    }

    private void handleCloseFile(String currentCommandInput, Editor editor){

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(editor.getProject());
        VirtualFile files[] = fileEditorManager.getSelectedFiles();
        PsiFile file = PsiManager.getInstance(editor.getProject()).findFile(files[0]);
        if(FileDocumentManager.getInstance().isDocumentUnsaved(PsiDocumentManager.getInstance(editor.getProject()).getDocument(file))) {
            String array[] = new String[1];
            array[0] = "OK";
            Messages.showDialog("file unsaved", "NOTIFICATION", array, 0, Messages.getWarningIcon());
        }
        else {
            fileEditorManager.closeFile(files[0]);
        }
        VIMMode.setMode(VIMMode.modeType.NORMAL);
    }

    private void handleForceCloseFile(String currentCommandInput, Editor editor){

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(editor.getProject());
        VirtualFile files[] = fileEditorManager.getSelectedFiles();
        PsiFile file = PsiManager.getInstance(editor.getProject()).findFile(files[0]);
        fileEditorManager.closeFile(files[0]);
        VIMMode.setMode(VIMMode.modeType.NORMAL);
    }

    private void handleSaveCloseFile(String currentCommandInput, Editor editor){

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(editor.getProject());
        VirtualFile files[] = fileEditorManager.getSelectedFiles();
        PsiFile file = PsiManager.getInstance(editor.getProject()).findFile(files[0]);
        FileDocumentManager.getInstance().saveDocument(PsiDocumentManager.getInstance(editor.getProject()).getDocument(file));
        fileEditorManager.closeFile(files[0]);
        VIMMode.setMode(VIMMode.modeType.NORMAL);
    }

    private void handleMoveLine(int rowNum, Editor editor){
        Caret caret = editor.getCaretModel().getCurrentCaret();
        caret.moveToOffset(rowNum);
        caret.moveToVisualPosition(new VisualPosition(rowNum - 1, 0));
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
        System.out.println("rowNum is " + rowNum);
    }
}
