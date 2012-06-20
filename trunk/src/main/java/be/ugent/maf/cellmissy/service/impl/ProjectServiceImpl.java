/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.repository.ProjectRepository;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola
 */
@Service("projectService")
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    
    private String folderName;

    @Override
    public Project setupProject(int projectNumber, String description, File microscopeDirectory) {

        //make new project entity and save to DB
        Project newProject = new Project();
        newProject.setProjectNumber(projectNumber);
        newProject.setProjectDescription(description);

        newProject = projectRepository.save(newProject);

        //create project folder on the server
        DecimalFormat df = new DecimalFormat("000");
        if (newProject.getProjectDescription().length() == 0) {
            folderName = "CM_P" + df.format(projectNumber);
        } else {
            folderName = "CM_P" + df.format(projectNumber) + "_" + newProject.getProjectDescription();
        }

        File subDirectory = new File(microscopeDirectory, folderName);
        subDirectory.mkdir();
        return newProject;
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project save(Project entity) {
        return projectRepository.save(entity);
    }

    @Override
    public void delete(Project entity) {
        entity = projectRepository.save(entity);
        projectRepository.delete(entity);
    }
}
