/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Project;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * A class extending Default Tree Model from Swing, to lazy load nodes and set
 * model for a JTree.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class MapDataTreeModel extends DefaultTreeModel {

    // the map with the data structure: projects and experiments
    private final LinkedHashMap<Project, List<Experiment>> map;

    /**
     * Constructor, takes a root node, and initialise the map.
     *
     * @param root
     * @param map
     */
    public MapDataTreeModel(TreeNode root, LinkedHashMap<Project, List<Experiment>> map) {
        super(root, true);
        this.map = map;
        lazyLoadNodes();
    }

    public LinkedHashMap<Project, List<Experiment>> getMap() {
        return map;
    }

    /**
     * Lazy load the nodes (parent and children)
     */
    private void lazyLoadNodes() {
        for (Project project : map.keySet()) {
            String projectNodeString = project.toString() + " (" + project.getProjectDescription() + ")"; // show project number and description
            DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(projectNodeString, true); // the node allows children
            ((DefaultMutableTreeNode) root).add(projectNode);
            List<Experiment> experiments = map.get(project);
            if (experiments != null && !experiments.isEmpty()) {
                for (Experiment experiment : experiments) {
                    String experimentNodeString = experiment.toString() + " (" + experiment.getPurpose() + ")";
                    DefaultMutableTreeNode experimentNode = new DefaultMutableTreeNode(experimentNodeString, false); // the node does not allow children
                    projectNode.add(experimentNode);
                }
            }
        }
    }
}
