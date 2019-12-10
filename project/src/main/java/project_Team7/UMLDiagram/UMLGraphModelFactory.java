package project_Team7.UMLDiagram;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.openapi.project.Project;
import com.mxgraph.model.*;
import org.apache.commons.lang.StringUtils;

import java.util.*;

class UMLGraphModelFactory {

    /**
     * Create a graph model that describes the UML diagram of a java project. This method use JavaElementVisitor to
     * traverse the Java hierarchy of each root package in the source directory, and to create a graph. Each node is an
     * instance of {@link mxCell} that can have a user object. The user object of root is the project
     * itself, and other nodes have corresponding instances of PsiPackage, PsiClass, PsiMethod, and PsiField.
     *
     * @param project a project
     * @return a graph model to describe the uml diagram of project
     */
    public static mxIGraphModel createUMLGraphModel(Project project, HashMap<PsiElement, String> currentClassToStr) {
        // TODO: draw arrows
        // TODO: add expand/collapse feature
        // TODO: add navigating feature
        // FIXME: first cell doesn't show now
        // FIXME: scroll doesn't work when you first open it

        /* the root node of the graph */
        final mxCell root = new mxCell(project.getName(), new mxGeometry(), "ROUNDED");
        root.setVertex(true);
        root.setEdge(false);
        root.setCollapsed(false);

        root.setGeometry(new mxGeometry(0, 0, 200, 25));

        /* The visitor to traverse the Java hierarchy and to construct the tree */
        final JavaElementVisitor visitor = new JavaElementVisitor() {
            // add member variables if necessary
            mxCell parent = root;
            Stack<Integer> stack = new Stack<>();

            /**
             * @return line number for mxGeometry
             */
            private int getLine() {
                if (stack.empty())
                    stack.push(1);

                return stack.peek();
            }

            /**
             * increment firstElement of stack
             */
            private void lineDown() {
                int temp = stack.pop();
                temp++;
                stack.push(temp);
            }

            /**
             * before drawing lower-level cell, call it
             *
             * push 1 to stack
             */
            private void stepDown() {
                stack.push(0);
            }

            /**
             * before drawing higher-level cell, call it
             *
             * push sum of first and second value
             */
            private void stepUp() {
                int temp = stack.pop();
                int temp2 = stack.pop();

                stack.push(temp + temp2);
            }

            /**
             * create new mxICell which is an edge connecting source to terminal
             *
             * @param source source cell
             * @param terminal terminal cell
             * @return created new edge cell
             */
            private mxICell createEdge(mxICell source, mxICell terminal) {
                mxCell newEdge = new mxCell(" ", new mxGeometry(), "");
                newEdge.setVertex(false);
                newEdge.setEdge(true);
                newEdge.setCollapsed(false);

                newEdge.setTerminal(source, true);
                newEdge.setTerminal(terminal, false);
                source.insert(newEdge);

                return newEdge;
            }

            /**
             * create new mxICell which is an vertex child of parent, holding value
             *
             * @param value Object that represents the value of the cell.
             * @param parent parent cell
             * @return created new vertex cell
             */
            private mxICell createVertex(Object value, mxICell parent) {
                mxCell newChild = new mxCell(value, new mxGeometry(), "ROUNDED");
                newChild.setVertex(true);
                newChild.setEdge(false);
                newChild.setCollapsed(false);

                int line = getLine();
                newChild.setGeometry(new mxGeometry(200, 25 * line, 200, 25));
                parent.insert(newChild);

                return newChild;
            }

            @Override
            public void visitPackage(PsiPackage pack) {
                // NOW: refactor visiting leaf Elements
                // TODO: refactor visiting non-leaf Elements

                // create new vertex
                mxCell newChild = (mxCell) createVertex(pack.getName(), parent);

                // create new edge
                createEdge(parent, newChild);

                mxCell grand_parent = parent;
                parent = newChild;

                stepDown();
                lineDown();
                for (PsiPackage children :
                        pack.getSubPackages()) {
                    children.accept(this);
                    lineDown();
                }
                for (PsiClass children :
                        pack.getClasses()) {
                    children.accept(this);
                    lineDown();
                }
                stepUp();

                parent = grand_parent;
            }

            @Override
            public void visitClass(PsiClass aClass) {
                // create new vertex
                mxCell newChild = (mxCell) createVertex(aClass.getName()+ " " + StringUtils.defaultString(currentClassToStr.get(aClass)), parent);

                // create new edge
                createEdge(parent, newChild);

                mxCell grand_parent = parent;
                parent = newChild;

                stepDown();
                lineDown();
                for (PsiField children :
                        aClass.getFields()) {
                    children.accept(this);
                    lineDown();
                }
                for (PsiMethod children :
                        aClass.getMethods()) {
                    children.accept(this);
                    lineDown();
                }
                stepUp();

                parent = grand_parent;
            }

            private void visitLeaf(PsiNameIdentifierOwner element) {
                // create new vertex
                mxCell newChild = (mxCell) createVertex(element.getName() + " " + StringUtils.defaultString(currentClassToStr.get(element)), parent);

                // create new edge
                createEdge(parent, newChild);
            }

            @Override
            public void visitMethod(PsiMethod method) {
                visitLeaf(method);
            }

            @Override
            public void visitField(PsiField field) {
                visitLeaf(field);
            }
        };

        /* apply the visitor for each root package in the source directory */
        getRootPackages(project).forEach(aPackage -> aPackage.accept(visitor));

        mxGraphModel graphModel = new mxGraphModel();
        graphModel.beginUpdate();
        try {
            graphModel.setRoot(root);
        } finally {
            graphModel.endUpdate();
        }

        return graphModel;
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
