package project_Team7.TypedHandler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import project_Team7.ProjectStructureTree;
import project_Team7.VIMMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import static project_Team7.EditorTypedHandler.*;


public class CommandMapHandler implements TypedHandler {

    private String currentCommand = null;
    private String currentSearchingString = null;
    private ArrayList<Integer> searchList = new ArrayList<>();
    private int currentIndex;
    private JPanel commandPanel = null;

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        switch (charTyped) {
            case ':':
            case '/':
                VIMMode.setMode(VIMMode.modeType.COMMAND);
                keyStrokeCommandMode(charTyped + " ", editor);
                modeViewer(editor);
                break;
            case 'n':
                if (VIMMode.getModeToString().equals("NORMAL MODE")) {
                    focusNextSearchString(editor, true);
                }
                break;
            case 'N':
                if (VIMMode.getModeToString().equals("NORMAL MODE")) {
                    focusNextSearchString(editor, false);
                }
                break;
        }
        VIMMode.setMode(VIMMode.modeType.NORMAL);
        modeViewer(editor);
    }

    /**
     * This methods performs the functionality of the COMMAND MODE. It shows popup
     * and prepare for the upcoming command input.
     * @param command ':' or '/', denotes the type of the COMMAND MODE
     * @param editor Opened editor
     */
    private void keyStrokeCommandMode(String command, Editor editor) {

        commandPanel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField(command);

        textField.setEditable(true);
        commandPanel.add(textField);
        JBPopupFactory a = JBPopupFactory.getInstance();
        JBPopup popup = a.createComponentPopupBuilder(commandPanel, textField).createPopup();
        popup.setRequestFocus(true);
        popup.setSize(new Dimension(editor.getComponent().getWidth(), 10));
        popup.showUnderneathOf(editor.getComponent());
        // As our vim plugin creates new popup each time the user enters COMMAND MODE,
        // it also defines new listeners every time.
        popup.addListener(new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                commandPanel = null;

            }
        });
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    /** Store arbitrary charecter 'x' for recovering initial condition */
                    setStoredChar('x');
                    VIMMode.setMode(VIMMode.modeType.NORMAL);
                    modeViewer(editor);
                    popup.closeOk(e);
                    popup.canClose();
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(textField.getText().substring(0, 1).equals("/"))
                        searchString(editor);
                    if(textField.getText().substring(0, 1).equals(":"))
                        handleCommands(editor);
                    popup.closeOk(e);
                    popup.canClose();

                }
                else{
                    if(command.equals(": ")) {
                        if(textField.getText().length() <= 2) {
                            textField.setText(": ");
                        }
                        currentCommand = textField.getText().substring(2);
                    }
                    else {
                        if(textField.getText().length() <= 2) {
                            textField.setText("/ ");
                        }
                        currentSearchingString = textField.getText().substring(2);
                    }
                }
            }
        });
    }


    /**
     * This method is called when the user types ENTER key in the COMMAND MODE.
     * It parse the input string, and calls corresponding sub-handling function
     * for each input command
     * @param editor Opened editor
     */
    private void handleCommands(Editor editor) {
        String currentCommandInput = currentCommand;
        int spaceIndex = currentCommandInput.indexOf(" ");
        if(spaceIndex > 0) {      // Commands with function & argument (ex) move 9-1)
            if(currentCommandInput.substring(0, 4).equals("move")) {
                handleMoveByTree(currentCommandInput);
            }
            else if(currentCommandInput.substring(0, 6).equals("unfold")) {
                handleUnfoldByTree(currentCommandInput);
            }
            else if(currentCommandInput.substring(0, 4).equals("fold")) {
                handleFoldByTree(currentCommandInput);
            }
            else if(currentCommandInput.substring(0, 4).equals("show")) {
                handleCodeSegment(currentCommandInput,true, editor);
            }
            else if(currentCommandInput.substring(0, 4).equals("hide")) {
                handleCodeSegment(currentCommandInput, false, editor);
            }

        }
        else { // Commands consists with shortcut, not yet implemented
            if(currentCommandInput.equals("w")){
                handleSaveFile(currentCommandInput, editor);
            }
            else if(currentCommandInput.equals("q")){
                handleCloseFile(currentCommandInput, editor);
            }
            else if(currentCommandInput.equals("q!")){
                handleForceCloseFile(currentCommandInput, editor);
            }
            else if(currentCommandInput.equals("wq")){
                handleSaveCloseFile(currentCommandInput, editor);
            }
            else if(isNatural(currentCommandInput)){
                handleMoveLine((int) Integer.parseInt(currentCommandInput), editor);
            }
        }

        setProperCursorShape(editor);
    }

    private boolean isNatural(String strNum){
        if(strNum == null){
            return false;
        }
        try{
            Integer integer = Integer.parseInt(strNum);
            if(integer < 0)
                return false;
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    /**
     * When the user types "move" function command in the COMMAND MODE, it parses
     * the command and find the node that the argument given is representing. And then,
     * it navigates the editor to the location that the code corresponding
     * to the node is placed in.
     * For example, if class A corresponds to identifier 9, the user can move to it
     * by typing "move 9".
     * @param currentCommandInput the input command from the command mode
     */
    private void handleMoveByTree(String currentCommandInput) {
        currentCommandInput = currentCommandInput.substring(5);
        if(ProjectStructureTree.getIdentifierToElement().containsKey(currentCommandInput)) {
            ((PsiDocCommentOwner) ProjectStructureTree.getIdentifierToElement().get(currentCommandInput)).navigate(true);
            VIMMode.setMode(VIMMode.modeType.NORMAL);
        }
    }

    /**
     * When the user types "unfold" function command in the COMMAND MODE, it parses
     * the command and find the node that the argument given is representing. And then,
     * if unfolds the node in the tree structure.
     * @param currentCommandInput the input command from the command mode
     */
    private void handleUnfoldByTree(String currentCommandInput) {
        currentCommandInput = currentCommandInput.substring(7);
        if(ProjectStructureTree.getIdentifierToElement().containsKey(currentCommandInput)) {
            PsiElement element = (PsiElement) ProjectStructureTree.getIdentifierToElement().get(currentCommandInput);
            System.out.println(element.getChildren().length);
            if(element instanceof PsiClass) {
                if(((PsiClass) element).getFields().length != 0)
                    ProjectStructureTree.thisTree.publicUpdateTree(((PsiClass) element).getFields()[0]);
                else if((((PsiClass) element).getMethods()).length != 0)
                    ProjectStructureTree.thisTree.publicUpdateTree(((PsiClass) element).getMethods()[0]);
                else
                    ProjectStructureTree.thisTree.publicUpdateTree(element);
            }
            else {
                ProjectStructureTree.thisTree.publicUpdateTree(element);
            }
            VIMMode.setMode(VIMMode.modeType.NORMAL);
        }
    }

    /**
     * When the user types "fold" function command in the COMMAND MODE, it parses
     * the command and find the node that the argument given is representing. And then,
     * it collapses the node if the node's child nodes are unfolded.
     * @param currentCommandInput the input command from the command mode
     *
     */
    private void handleFoldByTree(String currentCommandInput) {
        currentCommandInput = currentCommandInput.substring(5);
        if(ProjectStructureTree.getIdentifierToElement().containsKey(currentCommandInput)) {
            PsiElement element = (PsiElement) ProjectStructureTree.getIdentifierToElement().get(currentCommandInput);
            ProjectStructureTree.thisTree.collapseTree(element);
            VIMMode.setMode(VIMMode.modeType.NORMAL);
        }
    }
    private void handleCodeSegment(String currentCommandInput, boolean isShowing, Editor editor){
        currentCommandInput = currentCommandInput.substring(5);
        int rowNum;
        if(isNatural(currentCommandInput)) {
            rowNum = Integer.parseInt(currentCommandInput);
            for (FoldRegion f : editor.getFoldingModel().getAllFoldRegions()) {
                if (editor.getDocument().getLineNumber(f.getStartOffset()) + 1 == rowNum && f.isExpanded() == !isShowing) {
                    editor.getFoldingModel().runBatchFoldingOperation(() -> f.setExpanded(isShowing));
                    editor.getCaretModel().getCurrentCaret().moveToOffset(f.getStartOffset());
                    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
                }
            }
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

    /**
     * When the user use '/' COMMAND MODE and search some string, search all instances
     * of the string and store it in array. And then go to the first entry of the List using
     * 'focusNextSearchString' method.
     * @param editor Opened editor
     */
    private void searchString(Editor editor){
        String text = editor.getDocument().getText();
        searchList.clear();
        while(text.contains(currentSearchingString)){
            int slicedIndex = text.indexOf(currentSearchingString);
            int beginIndex = slicedIndex + (editor.getDocument().getText().length() - text.length());
            searchList.add(beginIndex);
            text = text.substring(slicedIndex + currentSearchingString.length());
        }
        currentIndex = searchList.size() - 1;
        VIMMode.setMode(VIMMode.modeType.NORMAL);
        focusNextSearchString(editor, true);

    }


    /**
     * When the user searches something, he or she can easily access to the searched
     * strings by pressing 'n' and 'N' command. This method implements those functionality.
     * @param editor Opened Editor
     * @param isSearchingNext if true, searches forward. Else, searches backward.
     */
    private void focusNextSearchString(Editor editor, boolean isSearchingNext) {
        int start, end;
        int exeNum = 0;
        if(getMultiExecute() == 0) exeNum = 1;
        else exeNum = getMultiExecute();
        for(int i = 0; i < exeNum; i++) {

            if (searchList.size() > 0) {
                if (isSearchingNext) {
                    if (currentIndex == searchList.size() - 1)
                        currentIndex = 0;
                    else
                        currentIndex++;
                } else {
                    if (currentIndex == 0)
                        currentIndex = searchList.size() - 1;
                    else
                        currentIndex--;
                }
                start = searchList.get(currentIndex);
                end = start + currentSearchingString.length();
                editor.getCaretModel().getCurrentCaret().setSelection(start, end);
                editor.getCaretModel().getCurrentCaret().moveToOffset(start);
            }
        }
        setMultiExecute(0);
    }
}
