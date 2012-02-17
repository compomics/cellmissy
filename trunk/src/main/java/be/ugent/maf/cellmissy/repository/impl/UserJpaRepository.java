/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.repository.UserRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author niels
 */
@Repository("userRepository")
public class UserJpaRepository extends GenericJpaRepository<User, Long> implements UserRepository {

    @Override
    public User findByFullName(String firstName, String lastName) {
        //hibernate criteria
        //List<User> resultList = findByCriteria(Restrictions.eq("name", name));
        
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("User.findByFullName");
        byNameQuery.setParameter("firstName",firstName);
        byNameQuery.setParameter("lastName",lastName);
        List<User> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
    
    @Override
    public User findByFirstName(String firstName) {
        //hibernate criteria
        //List<User> resultList = findByCriteria(Restrictions.eq("name", name));
        
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("User.findByFirstName");
        byNameQuery.setParameter("firstName",firstName);
        List<User> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
    
    @Override
    public User findByLastName(String lastName) {
        //hibernate criteria
        //List<User> resultList = findByCriteria(Restrictions.eq("name", name));
        
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("User.findByLastName");
        byNameQuery.setParameter("lastName",lastName);
        List<User> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
