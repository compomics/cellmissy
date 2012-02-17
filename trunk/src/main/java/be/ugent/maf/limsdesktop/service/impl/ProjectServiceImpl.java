/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.service.impl;

import be.ugent.maf.limsdesktop.entity.Project;
import be.ugent.maf.limsdesktop.repository.ProjectRepository;
import be.ugent.maf.limsdesktop.service.ProjectService;
import java.io.File;
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

    @Override
    public Project setupProject(String projectNumber, File projectDirectory) {

        //make new project entity and save to DB
        Project newProject = new Project();
        newProject.setProjectNumber(projectNumber);

        newProject = projectRepository.save(newProject);
        
        //create project files
        File subDirectory = new File(projectDirectory, newProject.getProjectNumber());
        subDirectory.mkdir();
//        String E01 = newProject.getProjectNumber()+"_E01";
//        File expFolder1 = new File (subDirectory, E01);
//        expFolder1.mkdir();
//        String E02 = newProject.getProjectNumber()+"_E02";
//        File expFolder2 = new File (subDirectory, E02);
//        expFolder2.mkdir();
        
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
