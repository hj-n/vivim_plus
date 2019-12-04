package project_Team7;

import android.os.SystemPropertiesProto;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class EditorTypedHandler implements TypedActionHandler {


    private String currentCommand = null;
    private String currentSearchingString = null;
    private boolean isSearch;
    private ArrayList<Integer> searchList = new ArrayList<>();
    private int currentIndex;
    private boolean isESC = false;
    private JPanel commandPanel = null;
    private static char storedChar = 'x';
    private InsertModeHandler myInsertModeHandler = new InsertModeHandler();
    private boolean hasDocumentListener = false;
    private String recentTypedString = null;
    private String recentDeletedString = null;
    private String clipBoard = "";
    private Integer multiExecute = 0;


    /** Getter, setter methods */

    public String getRecentTypedString() {
        return recentTypedString;
    }

    public String getRecentDeletedString() {
        return recentDeletedString;
    }

    public void setRecentTypedString(String s) {
        recentTypedString = s;
    }

    public void setRecentDeletedString(String s) {
        recentDeletedString = s;
    }

    public static void setStoredChar(char c){
        storedChar = c;
        return ;
    }

    public char getStoredChar(){
        return storedChar;
    }

    /** End of getter, setters */


    /**
     *  Execute for the every keyboard input, except the input of
     *  special keys like ESC, BACKSPACE, AND ENTER
     */
    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        if(!hasDocumentListener) {    // prevent more than one listeners to be added
            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    setRecentTypedString(event.getNewFragment().toString());
                    setRecentDeletedString(event.getOldFragment().toString());
                    searchList = new ArrayList<Integer>();
                }
            });
            hasDocumentListener = true;
        }
        /** add listener to detect ESC, BACKSPACE, and ENTER input */
        myInsertModeHandler.addKeyListener(editor, this);

        Caret caret = editor.getCaretModel().getCurrentCaret();
        if(getStoredChar() == 'i'){     /** INSERT MODE */
            VIMMode.setMode(VIMMode.modeType.INSERT);
            modeViewer(editor);
            myInsertModeHandler.execute(editor, charTyped);
        }
        else{   /** NORMAL MODE */
            normalModeControl(editor, charTyped, caret);
        }
        /** Set correct cursor shape for each mode */
        setProperCursorShape(editor);
    }

    /**
     * Main Function of controlling normal mode. performs matching functionality
     * for input shortcut, which is given by the argument 'charTyped'.
     * @param editor Opened editor
     * @param charTyped the argument which denotes the shortcuts for command mode
     * @param caret The Caret of the opened editor
     */
    private void normalModeControl(Editor editor, char charTyped, Caret caret) {

            switch(charTyped) {
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
                case 'v':
                    VIMMode.setMode(VIMMode.modeType.VISUAL);
                    modeViewer(editor);
                    break;
                case 'i':
                case 'I':
                case 'a':
                case 'A':
                case 'o':
                case 'O':
                    changeCaretToInsertionMode(editor, getInsertionTypeFromChar(charTyped));
                    VIMMode.setMode(VIMMode.modeType.INSERT);
                    modeViewer(editor);
                    myInsertModeHandler.setEnteredAfterInsertion(true);
                    /** Store 'i' for detecting current mode is INSERT MODE */
                    setStoredChar('i');
                    break;
                case 'h':
                case 'j':
                case 'k':
                case 'l':
                case '$':
                case 'w':
                case 'b':
                    moveCursor(charTyped, editor);
                    VIMMode.setMode(VIMMode.modeType.NORMAL);
                    modeViewer(editor);
                    break;
                case 'd':
                    if (caret.getSelectedText() != null) {
                        clipBoard = caret.getSelectedText();
                        editor.getDocument().replaceString(caret.getSelectionStart(), caret.getSelectionEnd(), "");
                    } else {
                        if (getStoredChar() == 'd') {
                            clipBoard = "";
                            int exeNum;
                            if(multiExecute == 0) exeNum = 1;
                            else exeNum = multiExecute;
                            for(int i = 0; i < exeNum; i++) {
                                int start = caret.getVisualLineStart();
                                int end = caret.getVisualLineEnd();
                                caret.setSelection(start, end);
                                clipBoard += caret.getSelectedText();
                                editor.getDocument().replaceString(start, end, "");
                            }
                            /** Store arbitrary charecter 'x' for recovering initial condition */
                            setStoredChar('x');
                            multiExecute = 0;
                        } else {
                            /** Store 'd' for detecting 'dd' vim instrcution */
                            setStoredChar('d');
                        }
                    }
                    break;
                case 'y':
                    if (caret.getSelectedText() != null) {
                        clipBoard = caret.getSelectedText();
                        caret.removeSelection();
                    } else {
                        if (getStoredChar() == 'y') {
                            clipBoard = "";
                            int originalCaretOffset = caret.getOffset();
                            int exeNum;
                            if(multiExecute == 0) exeNum = 1;
                            else exeNum = multiExecute;
                            for(int i = 0; i < exeNum; i++) {
                                int start = caret.getVisualLineStart();
                                int end = caret.getVisualLineEnd();
                                caret.setSelection(start, end);
                                clipBoard += caret.getSelectedText();
                                caret.moveToOffset(caret.getVisualLineEnd() + 1);
                                caret.removeSelection();
                            }
                            multiExecute = 0;
                            caret.moveToOffset(originalCaretOffset);
                            /** Store arbitrary charecter 'x' for recovering initial condition */
                            setStoredChar('x');
                        } else {
                            /** Store 'y' for detecting 'yy' vim instrcution */
                            setStoredChar('y');
                        }
                    }
                    break;
                case 'p':
                    if (clipBoard != null) {
                        String original = clipBoard;
                        if (clipBoard.charAt(clipBoard.length() - 1) != '\n')
                            clipBoard = clipBoard + '\n';
                        if (caret.getVisualLineEnd() == editor.getDocument().getText().length()) {
                            clipBoard = '\n' + clipBoard;
                        }
                        int exeNum;
                        if(multiExecute == 0) exeNum = 1;
                        else exeNum = multiExecute;
                        for(int i = 0; i < exeNum; i++) {
                            editor.getDocument().replaceString(caret.getVisualLineEnd(), caret.getVisualLineEnd(), clipBoard);
                            caret.moveToOffset(caret.getVisualLineEnd());
                            System.out.println(exeNum);
                        }
                        multiExecute = 0;
                        clipBoard = original;
                    }
                    break;
                case 'P':
                    if (clipBoard != null) {
                        if(clipBoard.charAt(clipBoard.length() - 1) != '\n')
                            clipBoard = clipBoard + '\n';
                        int exeNum;
                        if(multiExecute == 0) exeNum = 1;
                        else exeNum = multiExecute;
                        for(int i = 0; i < exeNum; i++) {
                            editor.getDocument().replaceString(caret.getOffset()+ 1, caret.getOffset() + 1, clipBoard);
                            caret.moveToOffset(caret.getOffset() + clipBoard.length() - 1);
                        }
                        multiExecute = 0;
                    }
                    break;
                case 'f':      // should change to uml window
                    Component component = TreeWindowFactory.getToolWindow().getComponent();
                    component.transferFocus();
                    VIMMode.setMode(VIMMode.modeType.NORMAL);
                    modeViewer(editor);
                    break;
                case 't':
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
                    modeViewer(editor);
                    setProperCursorShape(editor);
                    break;
                case 'J':
                case 'K':
                    if(charTyped == 'J')
                        moveOpenedTab(true, editor);     // move to left tab
                    else
                        moveOpenedTab(false, editor);     // move to right tab
                    //VIMMode.setMode(VIMMode.modeType.NORMAL);
                    //modeViewer(editor);
                    setProperCursorShape(editor);
                    break;
                default:
                    if(isNatural(charTyped+"") || Integer.parseInt(charTyped + "") == 0){
                        if(multiExecute != 0){
                            multiExecute = multiExecute * 10 + Integer.parseInt(charTyped + "");
                        }
                        else{
                            multiExecute = Integer.parseInt(charTyped+"");
                        }
                    }

                    VIMMode.setMode(VIMMode.modeType.NORMAL);
                    modeViewer(editor);
            }
    }

    private void moveOpenedTab(boolean b, Editor editor) {
        int exeNum = 0;
        if (multiExecute == 0) exeNum = 1;
        else exeNum = multiExecute;

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
        multiExecute = 0;
    }

    /**
     * In modern vim plugin, there exists a convention which represents cursor
     * with single line in the INSERT MODE, and represents by block in the
     * NORMAL MODE. This method sets proper cursor types depending on the current
     * MODE.
     * @param editor Opened editor
     */

    private void setProperCursorShape(Editor editor) {
        if(VIMMode.getModeToString() == "INSERT MODE" ) {
            editor.getSettings().setBlockCursor(false);
        }
        else {
            editor.getSettings().setBlockCursor(true);
        }
    }


    /**
     * There are several moving-control shortcuts in the NORMAL MODE of vim. For example,
     * 'h, j, k, l,...'. This method moves cursor depending on the current input shortcut.
     * @param charTyped the argument which denotes the shortcuts for moving cursor in the NORMAL MODE
     * @param editor Opened editor
     */
    private void moveCursor(char charTyped, Editor editor){
        int exeNum;
        String text = editor.getDocument().getText();
        if(multiExecute == 0) exeNum = 1;
        else exeNum = multiExecute;
        for(int i = 0; i < exeNum; i++ ) {
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            try {
                //move under line
                multiExecute = 0;
                boolean seperate = false;
                if (charTyped == 'j') {
                    VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine() + 1, caret.getVisualPosition().getColumn());
                    caret.moveToVisualPosition(visualPosition);
                }
                //move upper line
                if (charTyped == 'k') {
                    VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine() - 1, caret.getVisualPosition().getColumn());
                    caret.moveToVisualPosition(visualPosition);
                }
                //move left
                if (charTyped == 'h') {
                    VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() - 1);
                    caret.moveToVisualPosition(visualPosition);
                }
                //move right
                if (charTyped == 'l') {
                    VisualPosition visualPosition = new VisualPosition(caret.getVisualPosition().getLine(), caret.getVisualPosition().getColumn() + 1);
                    caret.moveToVisualPosition(visualPosition);
                }
                //move line end
                if(charTyped == '$'){
                    caret.moveToOffset(caret.getVisualLineEnd() - 1);
                    if(caret.getVisualLineStart() != caret.getOffset()){
                        caret.moveToOffset(caret.getOffset() - 1);
                    }
                }
                //move forwardly by one word
                if(charTyped == 'w'){
                    char tempChar = text.charAt(caret.getOffset());
                    int j = caret.getOffset() + 1;
                    for(; j < text.length(); j ++){
                        if(Character.isLetterOrDigit(text.charAt(j)) ^ Character.isLetterOrDigit(tempChar)) {
                            break;
                        }
                        else{
                            tempChar = text.charAt(j);
                        }
                    }
                    caret.moveToOffset(j);
                }
                //move backwardly by one word
                if(charTyped == 'b'){
                    char tempChar = text.charAt(caret.getOffset());
                    int j = caret.getOffset() - 1;
                    Boolean check = false;
                    for(; j > 0; j --){
                        if(Character.isLetterOrDigit(text.charAt(j)) ^ Character.isLetterOrDigit(tempChar) && check) {
                            break;
                        }
                        else if(Character.isLetterOrDigit(text.charAt(j)) ^ Character.isLetterOrDigit(tempChar)) {
                            check = true;
                            tempChar = text.charAt(j);
                        }
                        else{
                            tempChar = text.charAt(j);
                        }
                    }
                    caret.moveToOffset(j + 1);
                }
                //editor.getCaretModel().getCurrentCaret().moveToOffset(.getStartOffset());
                editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
            }
            catch(Exception e){

        }
        }
    }

    /**
     * This method continuously make popup to show user the current MODE.
     * @param editor Opened editor
     */
    private void modeViewer(Editor editor){
        JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
        JBPopup mes = jbPopupFactory.createMessage(VIMMode.getModeToString());
        mes.setRequestFocus(false);
        mes.show(RelativePoint.getSouthEastOf(editor.getContentComponent()));
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
                isESC = false;
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    /** Store arbitrary charecter 'x' for recovering initial condition */
                    setStoredChar('x');
                    VIMMode.setMode(VIMMode.modeType.NORMAL);
                    modeViewer(editor);
                    popup.closeOk(e);
                    popup.canClose();
                    isESC = true;
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
                            isSearch = false;
                        }
                        currentCommand = textField.getText().substring(2);
                    }
                    else {
                        if(textField.getText().length() <= 2) {
                            textField.setText("/ ");
                            isSearch = true;
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
            if(integer <= 0)
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
            ((PsiDocCommentOwner)ProjectStructureTree.getIdentifierToElement().get(currentCommandInput)).navigate(true);
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
            PsiElement element = (PsiElement)ProjectStructureTree.getIdentifierToElement().get(currentCommandInput);
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
                    editor.getFoldingModel().runBatchFoldingOperation(new Runnable() {
                        @Override
                        public void run() {
                            f.setExpanded(isShowing);
                        }
                    });
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
        if(multiExecute == 0) exeNum = 1;
        else exeNum = multiExecute;
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
        multiExecute = 0;
    }


    /**
     * There exists various ways to enter the insertion mode. This function make our vim
     * plugin to enter insertion mode, with slightly different functionality among
     * different shortcuts.
     * @param editor Opened Editor
     * @param type type of the way entering the Insertion mode
     */
    private void changeCaretToInsertionMode(Editor editor, enterInsertionType type) {
        int position = 0;
        switch(type) {
            case i :
                position = editor.getCaretModel().getCurrentCaret().getSelectionStart();
                break;
            case I :
                position = editor.getCaretModel().getCurrentCaret().getVisualLineStart();
                break;
            case a :
                position = editor.getCaretModel().getCurrentCaret().getSelectionStart() + 1;
                break;
            case A :
                position = editor.getCaretModel().getCurrentCaret().getVisualLineEnd() - 1;
                break;
            case o :
                Caret caret = editor.getCaretModel().getCurrentCaret();
                editor.getDocument().replaceString(caret.getVisualLineEnd(), caret.getVisualLineEnd(), "\n");
                position = editor.getCaretModel().getVisualLineEnd();
                break;
            case O :
                Caret caret2 = editor.getCaretModel().getCurrentCaret();
                if(caret2.getOffset() == caret2.getVisualLineStart()) {
                    editor.getDocument().replaceString(caret2.getVisualLineStart(), caret2.getVisualLineStart(), "\n");
                    position = editor.getCaretModel().getVisualLineStart();
                }
                else {
                    editor.getDocument().replaceString(caret2.getVisualLineStart(), caret2.getVisualLineStart(), "\n");
                    position = editor.getCaretModel().getVisualLineStart() - 1;
                }
                break;

        }
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        caret.setSelection(position, position);
        caret.moveToOffset(position);
        editor.getSettings().setBlockCursor(false);
    }


    /**
     * Helpers for the insertionType:
     * There are 6 ways to enter the INSERT MODE from the NORMAL mode.
     * The code differentiate them by using these enum type
     */

    private enum enterInsertionType {
        o, O, i, I, a, A
    }

    private enterInsertionType getInsertionTypeFromChar(char c) {
        switch (c) {
            case 'i':
                return enterInsertionType.i;
            case 'I':
                return enterInsertionType.I;
            case 'a':
                return enterInsertionType.a;
            case 'A':
                return enterInsertionType.A;
            case 'o':
                return enterInsertionType.o;
            case 'O':
                return enterInsertionType.O;
        }
        return null;
    }

    /** end of helpers */

}