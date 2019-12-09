package project_Team7.TypedHandler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocCommentOwner;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import project_Team7.ProjectStructureTree;
import project_Team7.TreeWindowFactory;
import project_Team7.VIMMode;

public class TreeHandler implements TypedHandler,CommandHandler {

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
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
    }


    @Override
    public void executeCommand(String currentCommandInput, @NotNull Editor editor) {
        if(currentCommandInput.substring(0, 4).equals("move")) {
            handleMoveByTree(currentCommandInput);
        }
        else if(currentCommandInput.substring(0, 6).equals("unfold")) {
            handleUnfoldByTree(currentCommandInput);
        }
        else if(currentCommandInput.substring(0, 4).equals("fold")) {
            handleFoldByTree(currentCommandInput);
        }
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
}
