package utils;

import Models.Employee;
import Models.Laptop;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryUtils {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtils() {}

    public static SessionFactory getSessionFactory(){
        if(sessionFactory == null){
            Configuration con = new Configuration().configure();
            con.addAnnotatedClass(Employee.class);
            con.addAnnotatedClass(Laptop.class);
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(con.getProperties());
            sessionFactory = con.buildSessionFactory(builder.build());
        }
        return sessionFactory;
    }
}
