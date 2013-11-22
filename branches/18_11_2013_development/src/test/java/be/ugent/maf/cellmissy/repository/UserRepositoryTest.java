/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.User;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola Masuzzo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * Simple test, find all users
     */
    @Test
    public void testFindAllUsers() {
        List<User> users = userRepository.findAll();
        Assert.assertTrue(!users.isEmpty());
        Assert.assertEquals(2, users.size());
    }

    @Test
    public void testFindByFullName() {
        User findByFullName = userRepository.findByFullName("user1", "user1");
        Assert.assertNotNull(findByFullName);
    }
}
