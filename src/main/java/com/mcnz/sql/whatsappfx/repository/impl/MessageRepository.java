package com.mcnz.sql.whatsappfx.repository.impl;

import com.mcnz.sql.whatsappfx.entity.Message;
import com.mcnz.sql.whatsappfx.entity.User;
import com.mcnz.sql.whatsappfx.utils.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class MessageRepository{

    private EntityManager entityManager;

    public MessageRepository() {
        this.entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
    }
    public void save(User sender, User receiver, String contenu) {
        entityManager.getTransaction().begin();
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContenu(contenu);
        message.setStatut(Message.Statut.ENVOYE);

        entityManager.persist(message);
        entityManager.getTransaction().commit();
    }
    public List<Message> getHistorique(User sender, User receiver) {
        String requete = "FROM Message m WHERE " + "(m.sender.id = :senderId AND m.receiver.id = :receiverId) OR " + "(m.sender.id = :receiverId AND m.receiver.id = :senderId) " + "ORDER BY m.dateEnvoi ASC";
        List<Message> messages = entityManager
                .createQuery(requete, Message.class)
                .setParameter("senderId", sender.getId())
                .setParameter("receiverId", receiver.getId())
                .getResultList();
        return messages;
    }
    public List<Message> getMessagesEnAttente(User receiver) {
        return entityManager.createQuery("FROM Message m WHERE m.receiver = :receiver " + "AND m.statut = :statut " + "ORDER BY m.dateEnvoi ASC", Message.class)
                .setParameter("receiver", receiver).setParameter("statut", Message.Statut.ENVOYE)
                .getResultList();
    }
    public void updateStatut(Long id, Message.Statut statut) {
        entityManager.getTransaction().begin();
        Message message = entityManager.find(Message.class, id);
        if (message != null) {
            message.setStatut(statut);
        }
        entityManager.getTransaction().commit();
    }

    public List<User> getTousLesUsers() {
        return entityManager.createQuery("FROM User ORDER BY username ASC", User.class)
                .getResultList();
    }

    public User getUserByUsername(String username) {
        try {
            return entityManager.createQuery(
                            "FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}