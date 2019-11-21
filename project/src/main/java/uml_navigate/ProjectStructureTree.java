package uml_navigate;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.tree.DefaultMutableTreeNode;
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
class ProjectStructureTree extends Tree {

    private static final Icon projectIcon = MetalIconFactory.getTreeHardDriveIcon();
    private static final Icon packageIcon = MetalIconFactory.getTreeFolderIcon();
    private static final Icon classIcon = MetalIconFactory.getTreeComputerIcon();
    private static final Icon methodIcon = MetalIconFactory.getFileChooserDetailViewIcon();
    private static final Icon fieldIcon = MetalIconFactory.getVerticalSliderThumbIcon();
    private static final Icon defaultIcon = MetalIconFactory.getTreeLeafIcon();

    private HashMap<PsiElement, String> classToStr;
    private HashMap<String, PsiElement> strToClass;
    private HashMap<PsiElement, String> curClassToStr;
    private HashMap<String, PsiElement> curStrToClass;
    /**
     * Creates a project structure tree for a given project.
     *
     * @param project a project
     */
    ProjectStructureTree(@NotNull Project project) {
        setModel(ProjectTreeModelFactory.createProjectTreeModel(project));
        //TODO: Mapping each class to string instead integer
        strToClass = new HashMap<>();
        classToStr = new HashMap<>();
        curStrToClass = new HashMap<>();
        curClassToStr = new HashMap<>();

        updateClassMap(project);

        // Set a cell renderer to display the name and icon of each node
        setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
                                              boolean expanded, boolean leaf, int row, boolean hasFocus) {
                // TODO: implement the renderer behavior here
                // hint: use the setIcon method to assign icons, and the append method to add text
                Object element = ((DefaultMutableTreeNode)value).getUserObject();
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
                    append(((PsiClass) element).getName() + " "
                            + StringUtils.defaultString(curClassToStr.get(element)));
                }
                else if(element instanceof PsiMethod) {
                    setIcon(methodIcon);
                    append(((PsiMethod) element).getName() + " "
                            + StringUtils.defaultString(curClassToStr.get(element)));

                }
                else if(element instanceof PsiField) {
                    setIcon(fieldIcon);
                    append(((PsiField) element).getName() + " "
                            + StringUtils.defaultString(curClassToStr.get(element)));
                }
                else {
                    setIcon(defaultIcon);
                }
            }
        });
        //addKeyListener(new MyKeyAdapter(intToClass));
        addKeyListener(new MyKeyAdapter(strToClass, classToStr, curStrToClass, curClassToStr, this));

        // Set a mouse listener to handle double-click events
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // TODO: implement the double-click behavior here
                    // hint: use the navigate method of the classes PsiMethod and PsiField
                    PsiElement element = null;
                    try {
                        element = (PsiElement) ((DefaultMutableTreeNode) getPathForLocation(e.getX(), e.getY()).getLastPathComponent()).getUserObject();
                        if(element instanceof PsiMethod || element instanceof PsiField){
                            ((PsiDocCommentOwner) element).navigate(true);
                        }
                    }
                    catch(Exception ex) {
                    }
                }
            }
        });

        // Set a Psi tree change listener to handle changes in the project. We provide code for obtaining an instance
        // of PsiField, PsiMethod, PsiClass, or PsiPackage. Implement the updateTree method below.
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
        setModel(ProjectTreeModelFactory.createProjectTreeModel(project));
        updateClassMap(project);
        publicUpdateTree(target);
    }

    public void publicUpdateTree(@NotNull PsiElement target) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();

        Enumeration e = root.breadthFirstEnumeration();
        DefaultMutableTreeNode node;
        do {
            node = (DefaultMutableTreeNode) e.nextElement();
            if(node.getUserObject().equals(target)) {
                TreePath path = new TreePath(node.getPath());

                setSelectionPath(path);
                scrollPathToVisible(path);
            }
        } while(e.hasMoreElements());

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


    public void updateClassMap(Project project) {
        // the root node of the tree
        final Integer[] count = {0};
        KeyIterator it = new KeyIterator();
        strToClass.clear();
        classToStr.clear();
        curClassToStr.clear();
        curStrToClass.clear();


        // The visitor to traverse the Java hierarchy and to construct the tree
        final JavaElementVisitor visitor = new JavaElementVisitor() {

            @Override
            public void visitPackage(PsiPackage pack) {
                for(PsiClass Class : pack.getClasses()) {
                    Class.accept(this);
                }
                for(PsiPackage Package : pack.getSubPackages()) {
                    Package.accept(this);
                }
            }

            @Override
            public void visitClass(PsiClass aClass) {
                String s = it.next();
                classToStr.put(aClass, s);
                strToClass.put(s, aClass);
                for(PsiElement Child : aClass.getChildren()){
                    Child.accept(this);
                }
            }
            @Override
            public void visitMethod(PsiMethod method) {
                String s = it.next();
                classToStr.put(method, s);
                strToClass.put(s, method);
            }

            @Override
            public void visitField(PsiField field) {
                String s = it.next();
                classToStr.put(field, s);
                strToClass.put(s, field);
            }
        };

        // apply the visitor for each root package in the source directory
        getRootPackages(project).forEach(aPackage -> aPackage.accept(visitor));
        curClassToStr.putAll(classToStr);
        curStrToClass.putAll(strToClass);
    }

    /**
     * Returns the root package(s) in the source directory of a project. The default package will not be considered, as
     * it includes all Java classes. Note that classes in the default package (i.e., having no package statement) will
     * be ignored for this assignment. To be completed, this case must be separately handled.
     *
     * @param project a project
     * @return a set of root packages
     */
    private static Set<PsiPackage> getRootPackages(Project project) {
        final Set<PsiPackage> rootPackages = new HashSet<>();
        PsiElementVisitor visitor = new PsiElementVisitor() {
            @Override
            public void visitDirectory(PsiDirectory dir) {
                final PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(dir);
                if (psiPackage != null && !PackageUtil.isPackageDefault(psiPackage))
                    rootPackages.add(psiPackage);
                else
                    Arrays.stream(dir.getSubdirectories()).forEach(sd -> sd.accept(this));
            }
        };

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        Arrays.stream(rootManager.getContentSourceRoots())
                .map(psiManager::findDirectory)
                .filter(Objects::nonNull)
                .forEach(dir -> dir.accept(visitor));

        return rootPackages;
    }
}
