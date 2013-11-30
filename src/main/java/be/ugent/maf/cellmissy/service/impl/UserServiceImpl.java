/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.repository.ProjectHasUserRepository;
import be.ugent.maf.cellmissy.repository.UserRepository;
import be.ugent.maf.cellmissy.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author niels
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectHasUserRepository projectHasUserRepository;

    @Override
    public User findByFullName(String firstName, String lastName) {
        return userRepository.findByFullName(firstName, lastName);
    }

    @Override
    public User findByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    @Override
    public User findByLastName(String lastName) {
        return userRepository.findByLastName(lastName);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User entity) {
        return userRepository.update(entity);
    }

    @Override
    public void delete(User entity) {
        entity = userRepository.findById(entity.getUserid());
        userRepository.delete(entity);
    }

    @Override
    public void save(User entity) {
        userRepository.save(entity);
    }

    @Override
    public User findByLoginCredentials(String userName, String password) {
        User user = userRepository.findByFirstName(userName);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findUsersByProjectid(Long projectid) {
        return projectHasUserRepository.findUsersByProjectid(projectid);
    }
}
