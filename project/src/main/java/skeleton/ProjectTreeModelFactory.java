package skeleton;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.*;

class ProjectTreeModelFactory {

    /**
     * Create a tree model that describes the structure of a java project. This method use JavaElementVisitor to
     * traverse the Java hierarchy of each root package in the source directory, and to create a tree. Each node is an
     * instance of {@link DefaultMutableTreeNode} that can have a user object. The user object of root is the project
     * itself, and other nodes have corresponding instances of PsiPackage, PsiClass, PsiMethod, and PsiField.
     *
     * @param project a project
     * @return a tree model to describe the structure of project
     */
    public static TreeModel createProjectTreeModel(Project project) {
        // the root node of the tree
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(project);


        // The visitor to traverse the Java hierarchy and to construct the tree
        final JavaElementVisitor visitor = new JavaElementVisitor() {
            // TODO: add member variables if necessary

            private DefaultMutableTreeNode findNode(PsiElement elem) {
                Enumeration e = root.breadthFirstEnumeration();
                DefaultMutableTreeNode node;
                while(e.hasMoreElements()){
                    node = (DefaultMutableTreeNode) e.nextElement();
                    if(node.getUserObject().equals(elem)) {
                        return node;
                    }
                }
                return null;
            }

            private DefaultMutableTreeNode findPackageNodeByString(String s) {
                Enumeration e = root.breadthFirstEnumeration();
                DefaultMutableTreeNode node;
                while(e.hasMoreElements()){
                    node = (DefaultMutableTreeNode) e.nextElement();
                    if(node.getUserObject() instanceof PsiPackage) {
                        if (node.getUserObject().toString().substring(11).equals(s))
                            return node;
                    }

                }
                return null;
            }

            @Override
            public void visitPackage(PsiPackage pack) {
                // TODO: add a new node to the parent node, and traverse the content of the package
               /* if(pack.getParentPackage().getName() == null)
                    root.add(new DefaultMutableTreeNode(pack));
                else
                    findNode(pack.getParentPackage()).add(new DefaultMutableTreeNode(pack));
*/
                if(pack.getParentPackage().getName() != null)
                    findNode(pack.getParentPackage()).add(new DefaultMutableTreeNode(pack));
                else
                    root.add(new DefaultMutableTreeNode(pack));

                for(PsiClass Class : pack.getClasses()) {
                    Class.accept(this);
                }
                for(PsiPackage Package : pack.getSubPackages()) {
                    Package.accept(this);
                }
            }

            @Override
            public void visitClass(PsiClass aClass) {
                // TODO: add a new node the parent node, and traverse the content of the class
                if(aClass.getParent() instanceof PsiClass)
                    findNode(aClass.getParent()).add(new DefaultMutableTreeNode(aClass));
                else
                    findPackageNodeByString(((PsiJavaFile)aClass.getContainingFile()).getPackageName()).add(new DefaultMutableTreeNode(aClass));
                for(PsiElement Child : aClass.getChildren()){
                    Child.accept(this);
                }
            }

            @Override
            public void visitMethod(PsiMethod method) {
                // TODO: add a new node to the parent node
                findNode(method.getParent()).add(new DefaultMutableTreeNode(method));
            }

            @Override
            public void visitField(PsiField field) {
                // TODO: add a new node to the parent node
                findNode(field.getParent()).add(new DefaultMutableTreeNode(field));

            }
        };


        // apply the visitor for each root package in the source directory
        getRootPackages(project).forEach(aPackage -> aPackage.accept(visitor));
        return new DefaultTreeModel(root);
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

