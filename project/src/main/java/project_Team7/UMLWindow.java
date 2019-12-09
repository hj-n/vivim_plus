package project_Team7;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.components.JBScrollPane;

import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * a UML diagram tool window that contains a scrollable graph view for a java project
 */
public class UMLWindow {
    // TODO: create subpackage for only UML diagram
    private final JScrollPane topContainer;
    private final mxGraphComponent graph;

    public UMLWindow() {
        Project project = getActiveProject();
        graph = new UMLGraph(project);
        topContainer = new JBScrollPane(graph);
    }

    /**
     * Returns the top-level container of this plugin
     *
     * @return the top-level component
     */
    @NotNull
    public JComponent getContent() {
        return topContainer;
    }

    /**
     * Returns the current graph model for this plugin
     *
     * @return a graph model
     */
    @NotNull
    public mxIGraphModel getUMLGraph() {
        return graph.getGraph().getModel();
    }

    /**
     * Returns the open project of the current IntelliJ IDEA window
     *
     * @return the project
     */
    @NotNull
    private Project getActiveProject() {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            if (window != null && window.isActive()) return project;
        }

        // if there is no active project, return an arbitrary project (the first)
        return ProjectManager.getInstance().getOpenProjects()[0];
    }
}
