package net.energo.grodno.pes.smsSender.utils.importFromDbf;

import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Iterator;
import java.util.List;



public class DatabaseManager {
    static Logger logger = LogManager.getLogger(DatabaseManager.class);

    private static SessionFactory factory;

    public static void main(String[] args) {

        logger.info("Старт");
        try {

            factory = new Configuration().configure().buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
//
//        //Tp tp = new Tp();
//
//
//        DatabaseManager dbManager = new DatabaseManager();
//        DBFManager dbfManager = new DBFManager(
//                System.getProperty("user.dir")+"\\src\\main\\resources\\static\\files\\ГГРЭС\\4KARTTP.DBF",
//                System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\ГГРЭС\\4KARTFID.DBF",
//                System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\ГГРЭС\\GGRES.DBF");
//
//        dbfManager.manage();


        //Res res = mTp.getRes(4);

        /*DBFManager dbfManager = new DBFManager(System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\БРЭС\\1KARTTP.DBF");
        List<Tp> tpList = dbfManager.readAllTp();
        for (Tp item : tpList) {
               mTp.addTp(item);
        }*/



        //Integer tpID = mTp.addTp("ТП-2", "CODE 1234589x.x", res);
        //System.out.println(tpID);

//
//        ManageEmployee ME = new ManageEmployee();
//
//        /* Add few employee records in database */
//        Integer empID1 = ME.addEmployee("Zara", "Ali", 1000);
//        Integer empID2 = ME.addEmployee("Daisy", "Das", 5000);
//        Integer empID3 = ME.addEmployee("John", "Paul", 10000);
//
//        /* List down all the employees */
//        ME.listEmployees();
//
//        /* Update employee's records */
//        ME.updateEmployee(empID1, 5000);
//
//        /* Delete an employee from the database */
//        ME.deleteEmployee(empID2);
//
//        /* List down new list of the employees */
//        ME.listEmployees();

    }

    /* Method to  READ all the employees */
    public void listTps( ){
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List tps = session.createQuery("FROM Tp").list();
            for (Iterator iterator = tps.iterator(); iterator.hasNext();){
                Tp tp = (Tp) iterator.next();
                System.out.println("Id: " + tp.getId());
                System.out.println("Name: " + tp.getName());
                System.out.println("dbf_id: " + tp.getDbf_id());
                System.out.println();
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Res getRes(int resId ){
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Res res = (Res)session.get(Res.class, resId);
            tx.commit();
            return res;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    /* Method to CREATE an Tp in the database */
//    public Integer addTp(String name, String dbf_code, Res res){
//        Session session = factory.openSession();
//        Transaction tx = null;
//        Integer tpID = null;
//
//        try {
//            tx = session.beginTransaction();
//            Tp tp = new Tp(name, dbf_code, res);
//            tpID = (Integer) session.save(tp);
//            tx.commit();
//        } catch (HibernateException e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//        return tpID;
//    }

    public Integer addTp(Tp tp){
        Session session = factory.openSession();
        Transaction tx = null;
        Integer tpID = null;

        try {
            tx = session.beginTransaction();
            tpID = (Integer) session.save(tp);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return tpID;
    }


//
//
//
//    /* Method to UPDATE salary for an employee */
//    public void updateEmployee(Integer EmployeeID, int salary ){
//        Session session = factory.openSession();
//        Transaction tx = null;
//
//        try {
//            tx = session.beginTransaction();
//            Employee employee = (Employee)session.get(Employee.class, EmployeeID);
//            employee.setSalary( salary );
//            session.update(employee);
//            tx.commit();
//        } catch (HibernateException e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//    }
//
//    /* Method to DELETE an employee from the records */
//    public void deleteEmployee(Integer EmployeeID){
//        Session session = factory.openSession();
//        Transaction tx = null;
//
//        try {
//            tx = session.beginTransaction();
//            Employee employee = (Employee)session.get(Employee.class, EmployeeID);
//            session.delete(employee);
//            tx.commit();
//        } catch (HibernateException e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//    }

}
