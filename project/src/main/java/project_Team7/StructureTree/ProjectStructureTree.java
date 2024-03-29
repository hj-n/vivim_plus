package project_Team7.StructureTree;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.*;


/**
 * A tree GUI for our Project Structure plugin. It displays the corresponding name and icon for the nodes in our tree
 * model using a custom cell renderer. Note that each node is an instance of DefaultMutableTreeNode, and its user data
 * is an instance of Project, PsiPackage, PsiClass, PsiMethod, or PsiField. The tree GUI detect detects double-click
 * mouse events for Method and Field nodes, and shows the corresponding methods or fields in the editor. Finally,
 * whenever the underlying project changes, the corresponding node of the tree GUI is automatically chosen.
 */
public class ProjectStructureTree extends Tree {

    private static final Icon projectIcon = MetalIconFactory.getTreeHardDriveIcon();
    private static final Icon packageIcon = MetalIconFactory.getTreeFolderIcon();
    private static final Icon classIcon = MetalIconFactory.getTreeComputerIcon();
    private static final Icon methodIcon = MetalIconFactory.getFileChooserDetailViewIcon();
    private static final Icon fieldIcon = MetalIconFactory.getVerticalSliderThumbIcon();
    private static final Icon defaultIcon = MetalIconFactory.getTreeLeafIcon();

    private HashMap<Object, String> nodeToChildIndex = new HashMap<>();
    private static HashMap<String, Object> identifierToElement = new HashMap<>();

    public static HashMap<String, Object> getIdentifierToElement() {
        return identifierToElement;
    }


    /**
     * This public variable stores the reference of this Object. By using this "tricky"
     * implementation, we can access this tree structure in other objects.
     */
    public static ProjectStructureTree thisTree;


    /**
     * Creates a project structure tree for a given project.
     *
     * @param project a project
     */
    ProjectStructureTree(@NotNull Project project) {
        setModel(ProjectTreeModelFactory.createProjectTreeModel(project));
        thisTree = this;
        /** Set a cell renderer to display the name and icon of each node */
        setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
                                              boolean expanded, boolean leaf, int row, boolean hasFocus) {

                Object element = ((DefaultMutableTreeNode)value).getUserObject();
                Integer index = tree.getModel().getIndexOfChild(((DefaultMutableTreeNode) value).getParent(), value);
                String parentIndexString;
                String identifier = null;
                if(element instanceof PsiClass) {
                    identifier = index.toString();
                    nodeToChildIndex.put(value, identifier);
                }
                else if(element instanceof PsiField || element instanceof  PsiMethod) {
                    parentIndexString = nodeToChildIndex.get(((DefaultMutableTreeNode) value).getParent());
                    identifier = parentIndexString + "-" + index.toString();
                }
                nodeToChildIndex.put(value, index.toString());
                identifierToElement.put(identifier, element);


                if(element instanceof Project) {
                    setIcon(projectIcon);
                    append(((Project) element).getName());
                }
                else if(element instanceof PsiPackage) {
                    setIcon(packageIcon);
                    append(((PsiPackage) element).getName());
                }
                else if(element instanceof PsiClass) {
                    setIcon(classIcon);
                    append(identifier +" : " + ((PsiClass) element).getName());
                }
                else if(element instanceof PsiMethod) {
                    setIcon(methodIcon);
                    append(identifier +" : " + ((PsiMethod) element).getName());

                }
                else if(element instanceof PsiField) {
                    setIcon(fieldIcon);
                    append(identifier +" : " + ((PsiField) element).getName());
                }
                else {
                    setIcon(defaultIcon);
                    append(((PsiElement) element).getText());
                }
            }
        });


        /** Set a mouse listener to handle double-click events */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    PsiElement element = null;
                    try {
                        element = (PsiElement) ((DefaultMutableTreeNode) getPathForLocation(e.getX(), e.getY()).getLastPathComponent()).getUserObject();
                        if(element instanceof PsiMethod || element instanceof PsiField){
                            ((PsiDocCommentOwner) element).navigate(true);
                        }
                    }
                    catch(Exception ex) {}
                }
            }
        });

        /** Set a Psi tree change listener to handle changes in the project. We provide code for obtaining an instance
         * of PsiField, PsiMethod, PsiClass, or PsiPackage. Implement the updateTree method below.
         */

        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateTree(project, target));
            }

            @Override
            public void childRemoved(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateTree(project, target));
            }

            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateTree(project, target));
            }
        });
    }

    /**
     * Updates a tree according to the change in the target element, and shows the corresponding node in the Project
     * Structure tree. The simplest way is to reset a model of the tree (using setModel) and then to traverse the tree
     * to find the corresponding node to the target element. Use the methods {@link JTree::setSelectionPath} and
     * {@link JTree::scrollPathToVisibles} to display the corresponding node in GUI.
     *
     * @param project a project
     * @param target  a target element
     */
    private void updateTree(@NotNull Project project, @NotNull PsiElement target) {
        // TODO: implement this method
        setModel(ProjectTreeModelFactory.createProjectTreeModel(project));
        publicUpdateTree(target);
    }

    public void publicUpdateTree(@NotNull PsiElement target) {
        TreePath tp = findTreePath(target);
        setSelectionPath(tp);
        scrollPathToVisible(tp);
    }

    public void collapseTree(@NotNull PsiElement target) {
        TreePath tp = findTreePath(target);
        setSelectionPath(tp);
        collapsePath(tp);
    }

    private TreePath findTreePath(@NotNull PsiElement target) {
        TreePath tp = null;
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) (this.getModel().getRoot());

        Enumeration<TreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject().equals(target)) {
                tp = new TreePath(node.getPath());
            }
        }

        return tp;
    }

    /**
     * Returns an instance of PsiField, PsiMethod, PsiClass, or PsiPackage that is related to a change event
     *
     * @param event a change event
     * @return the corresponding Psi element
     */
    @NotNull
    private Optional<PsiElement> getTargetElement(@NotNull PsiTreeChangeEvent event) {
        for (PsiElement obj : List.of(event.getChild(), event.getParent())) {
            for (Class<? extends PsiElement> c :
                    List.of(PsiField.class, PsiMethod.class, PsiClass.class, PsiPackage.class)) {
                PsiElement elm = PsiTreeUtil.getParentOfType(obj, c, false);
                if (elm != null)
                    return Optional.of(elm);
            }
            if (obj instanceof PsiDirectory) {
                final PsiPackage pack = JavaDirectoryService.getInstance().getPackage((PsiDirectory) obj);
                if (pack != null)
                    return Optional.of(pack);
            }
        }
        return Optional.empty();
    }

}
