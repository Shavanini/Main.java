package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {

    public UserDaoHibernateImpl() { }

    @Override
    public void createUsersTable() {
        final String sql =
                "CREATE TABLE IF NOT EXISTS users (" +
                        " id BIGINT NOT NULL AUTO_INCREMENT," +
                        " name VARCHAR(255) NOT NULL," +
                        " lastName VARCHAR(255) NOT NULL," +
                        " age TINYINT NOT NULL," +
                        " PRIMARY KEY (id)" +
                        ")";
        execNative(sql, "createUsersTable");
    }

    @Override
    public void dropUsersTable() {
        execNative("DROP TABLE IF EXISTS users", "dropUsersTable");
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction tx = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(new User(name, lastName, age));
            tx.commit();
            System.out.printf("User с именем — %s добавлен в базу данных%n", name);
        } catch (Exception e) {
            rollbackQuiet(tx);
            System.err.println("saveUser error: " + e.getMessage());
        }
    }

    @Override
    public void removeUserById(long id) {
        Transaction tx = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User u = session.get(User.class, id);
            if (u != null) session.remove(u);
            tx.commit();
        } catch (Exception e) {
            rollbackQuiet(tx);
            System.err.println("removeUserById error: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = Util.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).getResultList();
        } catch (Exception e) {
            System.err.println("getAllUsers error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void cleanUsersTable() {
        Transaction tx = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createNativeQuery("delete from User").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            rollbackQuiet(tx);
            System.err.println("cleanUsersTable error: " + e.getMessage());
        }
    }

    // helpers
    private void execNative(String sql, String where) {
        Transaction tx = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createNativeQuery(sql).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            rollbackQuiet(tx);
            System.err.println(where + " error: " + e.getMessage());
        }
    }

    private void rollbackQuiet(Transaction tx) {
        if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
    }
}
