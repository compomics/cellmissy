/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.repository.ProjectHasUserRepository;
import be.ugent.maf.cellmissy.repository.ProjectRepository;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
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

    private static final Logger LOG = Logger.getLogger(ProjectService.class);
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectHasUserRepository projectHasUserRepository;
    private String projectFolderName;

    @Override
    public Project setupProject(int projectNumber, String description, File microscopeDirectory) {

        //make new project entity and update to DB
        Project newProject = new Project();
        newProject.setProjectNumber(projectNumber);
        newProject.setProjectDescription(description);

        newProject = projectRepository.update(newProject);

        //create project folder on the server
        DecimalFormat df = new DecimalFormat("000");
        if (newProject.getProjectDescription().length() == 0) {
            projectFolderName = "CM_P" + df.format(projectNumber);
        } else {
            projectFolderName = "CM_P" + df.format(projectNumber) + "_" + newProject.getProjectDescription();
        }

        File subDirectory = new File(microscopeDirectory, projectFolderName);
        // mkdir() returns true if and only if the directory was created; false otherwise
        boolean mkdir = subDirectory.mkdir();
        if (mkdir) {
            LOG.debug("Project Folder was created: " + projectFolderName);
        }
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
    public Project update(Project entity) {
        return projectRepository.update(entity);
    }

    @Override
    public void delete(Project entity) {
        entity = projectRepository.findById(entity.getProjectid());
        projectRepository.delete(entity);
    }

    @Override
    public void save(Project entity) {
        projectRepository.save(entity);
    }

    @Override
    public void saveProjectUsers(Project entity) {
        for (ProjectHasUser projectHasUser : entity.getProjectHasUserList()) {
            projectHasUserRepository.save(projectHasUser);
        }
    }

    @Override
    public List<Project> findProjectsByUserid(Long userid) {
        return projectHasUserRepository.findProjectsByUserid(userid);
    }

    @Override
    public void deleteUsersFromProject(List<User> users, Project project) {
        // get the project has users from the project
        List<ProjectHasUser> projectHasUserList = project.getProjectHasUserList();
        List<ProjectHasUser> projHasUsersToDelete = new ArrayList<>();
        for (User userToDelete : users) {
            for (ProjectHasUser projectHasUser : projectHasUserList) {
                if (projectHasUser.getUser().equals(userToDelete)) {
                    projHasUsersToDelete.add(projectHasUser);
                }
            }
        }
        // set the other side of the relationship
//        project.setProjectHasUserList(projHasUsersToDelete);
//        for (User user : users) {
//            user.setProjectHasUserList(projHasUsersToDelete);
//        }

        for (ProjectHasUser projectHasUser : projHasUsersToDelete) {
            projectHasUser = projectHasUserRepository.findById(projectHasUser.getProjectHasUserid());
            projectHasUserRepository.delete(projectHasUser);
        }
    }

    @Override
    public void addUsersToProject(List<User> users, Project project) {
        for (User userToAdd : users) {
            ProjectHasUser projectHasUser = new ProjectHasUser(project, userToAdd);
            projectHasUserRepository.save(projectHasUser);
        }
        update(project);
    }
}
