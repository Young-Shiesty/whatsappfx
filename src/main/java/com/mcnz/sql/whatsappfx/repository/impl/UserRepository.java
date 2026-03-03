package com.mcnz.sql.whatsappfx.repository.impl;

import com.mcnz.sql.whatsappfx.entity.User;
import com.mcnz.sql.whatsappfx.repository.ICrud;
import com.mcnz.sql.whatsappfx.utils.JPAUtil;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.EntityManager;
import java.util.List;

public class UserRepository implements ICrud<User> {
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    private EntityManager entityManager;
    public UserRepository() {
        this.entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
    }
    @Override

    public List<User> getAll() {
            this.entityManager.getTransaction().begin();
            List<User> users = this.entityManager.createQuery("FROM User", User.class).getResultList();
            this.entityManager.getTransaction().commit();
            return users;
        }


    @Override
    public void insert(User user) {
        entityManager.getTransaction().begin();
        user.setPassword(hashPassword(user.getPassword()));
        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public User get(int id) {
        return null;
    }

    @Override
    public void update(User user) {

    }
    public User connecter(String username, String password) {
        try {
            User user = this.entityManager.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
