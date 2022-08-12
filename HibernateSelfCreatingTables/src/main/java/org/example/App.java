package org.example;

import Models.Employee;
import Models.Laptop;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateSessionFactoryUtils;

import java.util.List;
import java.util.Map;


public class App
{
    public static void main( String[] args )
    {
        //addAndInsertData();

        // howFetchingIsWorking();

        // if we change the setting and configuration, we enable second level cache and now
        // our sessions will be able to get data from first level cache of other sessions
        //First_SecondLevelCache(); // first level cache is provided in hibernate by default

        //QuerySecondLevelCache();

        //UsingHibernateQueryLanguage1();

        //UsingHibernateQueryLanguage2();

        //UsingHibernateQueryLanguage3();

        //HibernateObjectState(); // persistence lifecycle

        DifferenceBetweenGetAndLoad();
    }

    private static void DifferenceBetweenGetAndLoad() {
        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();

        // hits database only when we try to get info, so firstly it just gives us a proxy object, not an actual object
        Employee empLoad = session.load(Employee.class, 2); // here it doesn't send query
        System.out.println(empLoad); // here it sends query

        // hits database, sends query any time is used
        Employee empGet = session.get(Employee.class, 2); // straightaway sends the query
        System.out.println(empGet);

        session.getTransaction().commit();
        session.close();
    }

    private static void HibernateObjectState() {
        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();

        // transient state, after stopping program, data will be lost
        // ===
        Employee emp = new Employee();
        emp.setName("Vadim");
        emp.setAge(39);
        // ===

        // persistence state, is connected with database
        session.save(emp); // will not save, but will make connection between the object and a row
        emp.setAge(20); // all we do with object in the state reflects in database

        // removed state
        //session.remove(emp);

        session.getTransaction().commit();

        // detached state
        session.detach(emp);
        emp.setAge(35); // object is detached and has no connection with row

        session.close();
    }

    private static void UsingHibernateQueryLanguage3() {
        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();

        // ===== we want to receive * row =====
//        SQLQuery query = session.createSQLQuery("select * from laptop where Id > 4");
//        query.addEntity(Laptop.class); // specify the getting data
//        List<Laptop> list = query.list();
//        for (Laptop item :
//                list) {
//            System.out.println(item);
//        }

        // ===== we want to receive specific values(name, age) =====
        // native queries
        SQLQuery query = session.createSQLQuery("select name, age from employee where employee_id > 2");
        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        List list = query.list();
        for (Object item :
                list) {
            Map m = (Map)item;
            System.out.println(m.get("name") + " " + m.get("age"));
        }

        session.getTransaction().commit();
        session.close();
    }

    private static void UsingHibernateQueryLanguage2() {
        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();

        // we do not get Laptop objects, but array of objects with specified values of id and name
        //Query query = session.createQuery("select Id, name from Laptop where Id = 1");
        int minId = 4;
        Query query = session.createQuery("select Id, name from Laptop where Id >= :minId");
        query.setParameter("minId", minId); // to use parameter
        // if we get one row
        //Object[] laptop = (Object[]) query.uniqueResult();
//        for (Object o :
//                laptop) {
//            System.out.println(o);
//        }

        // if we get multiple rows
        List<Object[]> laptops = (List<Object[]>) query.list();
        for (Object[] item :
                laptops) {
            System.out.println(item[0] + " " + item[1]);
        }

        session.getTransaction().commit();
        session.close();
    }

    private static void UsingHibernateQueryLanguage1() {
        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();

        // All
        //Query query = session.createQuery("from Laptop");

        // Owner's id = 2
        Query query = session.createQuery("from Laptop where owner.Id = 2");
        List<Laptop> list = query.list(); // fetching values
        for (Laptop item :
                list) {
            System.out.println(item);
        }


        session.getTransaction().commit();
        session.close();
    }

    private static void QuerySecondLevelCache() {
        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();
        Session session2 = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session2.beginTransaction();

        Employee emp = new Employee();
        Query q1 = session.createQuery("from Employee where Id=1");
        q1.setCacheable(true); // enables using second level cache
        emp = (Employee) q1.uniqueResult();
        System.out.println(emp);


        Query q2 = session2.createQuery("from Employee where Id=1");
        q2.setCacheable(true);
        emp = (Employee) q2.uniqueResult();
        System.out.println(emp );

        session2.getTransaction().commit();
        session.getTransaction().commit();
        session2.close();
        session.close();
    }

    public static void First_SecondLevelCache (){

        Employee emp = null;

        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();
        Session session2 = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session2.beginTransaction();

        // We make a query to database and store in our server the result of it
        emp = (Employee) session.get(Employee.class, 1); // makes a query
        System.out.println(emp);

        // So the next try we want to run this task we won't ask the database, but will take previous result of the query
        emp = (Employee)session.get(Employee.class, 1); // doesn't make any query
        System.out.println(emp);

        // another session can't access the first level cache of another session
        emp = (Employee) session2.get(Employee.class, 1); // makes a query
        System.out.println(emp);

        session2.getTransaction().commit();
        session.getTransaction().commit();
        session2.close();
        session.close();
    }

    public static void howFetchingIsWorking(){
        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();

        Employee emp =  session.get(Employee.class, 2);
        System.out.println(emp);
        for (Laptop item :
                emp.getDevices()) {
            System.out.println(item);
        }

        session.getTransaction().commit();
        session.close();
    }

    public static void addAndInsertData(){
        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
        session.beginTransaction();

        Employee emp = new Employee();
        emp.setAge(38);
        emp.setId(1);
        emp.setName("John");

        Employee emp1 = new Employee();
        emp1.setAge(31);
        emp1.setId(3);
        emp1.setName("Joey");

        Employee emp2 = new Employee();
        emp2.setAge(31);
        emp2.setId(3);
        emp2.setName("Mike");

        Employee emp3 = new Employee();
        emp3.setAge(31);
        emp3.setId(3);
        emp3.setName("Raimond");

        Employee emp4 = new Employee();
        emp4.setAge(31);
        emp4.setId(3);
        emp4.setName("Max");

        Laptop lap1 = new Laptop();
        lap1.setName("Hp");
        lap1.setOwner(emp);
        Laptop lap2 = new Laptop();
        lap2.setName("Asus");
        lap2.setOwner(emp1);
        Laptop lap3 = new Laptop();
        lap3.setName("Acer");
        lap3.setOwner(emp2);
        Laptop lap4 = new Laptop();
        lap4.setName("Macbook");
        lap4.setOwner(emp3);
        Laptop lap5 = new Laptop();
        lap5.setName("Samsung");
        lap5.setOwner(emp1);
        Laptop lap6 = new Laptop();
        lap6.setName("MiBook");
        lap6.setOwner(emp1);
        Laptop lap7 = new Laptop();
        lap7.setName("VivoBook");
        lap7.setOwner(emp4);
        Laptop lap8 = new Laptop();
        lap8.setName("Legion");
        lap8.setOwner(emp2);
        emp.getDevices().add(lap1);
        emp1.getDevices().add(lap2);
        emp2.getDevices().add(lap3);
        emp3.getDevices().add(lap4);
        emp1.getDevices().add(lap5);
        emp1.getDevices().add(lap6);
        emp4.getDevices().add(lap7);
        emp2.getDevices().add(lap8);


        session.save(emp);
        session.save(emp1);
        session.save(emp2);
        session.save(emp3);
        session.save(emp4);
        session.save(lap1);
        session.save(lap2);
        session.save(lap3);
        session.save(lap4);
        session.save(lap5);
        session.save(lap6);
        session.save(lap7);
        session.save(lap8);

        session.getTransaction().commit();
        session.close();

    }
}
