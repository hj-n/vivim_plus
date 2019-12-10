package project_Team7.UMLDiagram;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.util.*;


public class UMLGraph extends mxGraphComponent {
    private HashMap<Object, String> cellToChildIndex = new HashMap<>();
    private static HashMap<String, Object> identifierToElement = new HashMap<>();

    public static HashMap<String, Object> getIdentifierToElement() {
        return identifierToElement;
    }


    /**
     * This public variable stores the reference of this Object. By using this "tricky"
     * implementation, we can access this graph in other objects.
     */
    public static UMLGraph thisGraph;
    public Project project;

    /**
     * Each psiclass, psimethod, psifield  are shown with their keys on the project structure window.
     * By keylistener, this class gets the character that user typed.
     * First letter of key are erased if it is same as a charater that user typed. If not, the whole key is erased.
     **/
    private HashMap<PsiElement, String> classToStr;
    private HashMap<String, PsiElement> strToClass;
    private HashMap<PsiElement, String> currentClassToStr;
    private HashMap<String, PsiElement> currentStrToClass;

    /**
     * Creates a UML graph for a given project.
     *
     * @param project a project
     */
    public UMLGraph(Project project) {
        // TODO: reflect code uml information
        // TODO: add keyboard or mouse click events
        super(new mxGraph());


        thisGraph = this;
        strToClass = new HashMap<>();
        classToStr = new HashMap<>();
        currentStrToClass = new HashMap<>();
        currentClassToStr = new HashMap<>();
        this.project = project;

        updateClassMap(project);

        getGraph().setModel(UMLGraphModelFactory.createUMLGraphModel(project,currentClassToStr));

        /* Set a Psi tree change listener to handle changes in the project. We provide code for obtaining an instance
          of PsiField, PsiMethod, PsiClass, or PsiPackage. Implement the updateTree method below.
         */
        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateGraph(project));
            }

            @Override
            public void childRemoved(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateGraph(project));
            }

            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateGraph(project));
            }

        });
        addKeyListener(new UMLKeyAdapter(strToClass, classToStr, currentStrToClass, currentClassToStr, this));
    }

    /**
     * Updates a tree according to the change in the target element, and shows the corresponding node in the Project
     * Structure tree. The simplest way is to reset a model of the tree (using setModel) and then to traverse the tree
     * to find the corresponding node to the target element. Use the methods {@link JTree::setSelectionPath} and
     * {@link JTree::scrollPathToVisibles} to display the corresponding node in GUI.
     *
     * @param project a project
     */
    public void updateGraph(@NotNull Project project) {
        getGraph().setModel(UMLGraphModelFactory.createUMLGraphModel(project, currentClassToStr));
        updateClassMap(project);
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

    /**
     * Update shortcut maps if files are added, deleted or renamed.
     *
     * @param project a project
     */
    public void updateClassMap(Project project) {
        /* Clean the map before reconstruct map */
        final Integer[] count = {0};
        KeyIterator it = new KeyIterator();
        strToClass.clear();
        classToStr.clear();
        currentClassToStr.clear();
        currentStrToClass.clear();


        /* The visitor to traverse the Java hierarchy and to construct the map */
        final JavaElementVisitor visitor = new JavaElementVisitor() {

            @Override
            public void visitPackage(PsiPackage pack) {
                for (PsiClass Class : pack.getClasses()) {
                    Class.accept(this);
                }
                for (PsiPackage Package : pack.getSubPackages()) {
                    Package.accept(this);
                }
            }

            @Override
            public void visitClass(PsiClass aClass) {
                String s = it.next();
                classToStr.put(aClass, s);
                strToClass.put(s, aClass);
                for (PsiElement Child : aClass.getChildren()) {
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

        /* Apply the visitor for each root package in the source directory
         *  and update remain shortcut maps from the maps already construct.
         */
        getRootPackages(project).forEach(aPackage -> aPackage.accept(visitor));
        currentClassToStr.putAll(classToStr);
        currentStrToClass.putAll(strToClass);
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
