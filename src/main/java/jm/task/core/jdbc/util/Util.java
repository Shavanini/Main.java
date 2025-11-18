package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/Database?useSSL=false&serverTimezone=UTC";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "root";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Properties props = new Properties();
                props.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                props.put("hibernate.connection.url", DB_URL);
                props.put("hibernate.connection.username", DB_USER);
                props.put("hibernate.connection.password", DB_PASSWORD);
                props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
                props.put("hibernate.hbm2ddl.auto", "none");
                props.put("hibernate.show_sql", "false");
                props.put("hibernate.format_sql", "true");

                Configuration cfg = new Configuration();
                cfg.setProperties(props);
                cfg.addAnnotatedClass(User.class);

                StandardServiceRegistryBuilder builder =
                        new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());
                sessionFactory = cfg.buildSessionFactory(builder.build());
            } catch (Exception ex) {
                throw new RuntimeException("SessionFactory initialization failed", ex);
            }
        }
        return sessionFactory;
    }
}
