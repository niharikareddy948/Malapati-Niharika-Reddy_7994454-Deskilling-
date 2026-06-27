package com.cognizant.hibernatexml;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageEmployee {
    private static SessionFactory factory;

    public static void main(String[] args) {
        try {
            // Build SessionFactory from hibernate.cfg.xml
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        ManageEmployee ME = new ManageEmployee();

        // 1. Add few employee records in database (CREATE - session.save)
        System.out.println("\n--- ADDING EMPLOYEES ---");
        Integer empID1 = ME.addEmployee("Zara", "Ali", 1000);
        Integer empID2 = ME.addEmployee("Daisy", "Das", 5000);
        Integer empID3 = ME.addEmployee("John", "Paul", 10000);

        // 2. List down all the employees (READ - session.createQuery.list)
        System.out.println("\n--- LISTING EMPLOYEES ---");
        ME.listEmployees();

        // 3. Get specific employee details (READ - session.get)
        System.out.println("\n--- GETTING EMPLOYEE BY ID: " + empID1 + " ---");
        ME.getEmployee(empID1);

        // 4. Delete an employee from database (DELETE - session.delete)
        System.out.println("\n--- DELETING EMPLOYEE BY ID: " + empID2 + " ---");
        ME.deleteEmployee(empID2);

        // 5. List down all the employees again to verify deletion
        System.out.println("\n--- LISTING EMPLOYEES AFTER DELETION ---");
        ME.listEmployees();

        // Close SessionFactory
        factory.close();
        System.out.println("\nSessionFactory Closed successfully.");
    }

    /* Method to CREATE an employee in the database */
    public Integer addEmployee(String fname, String lname, int salary) {
        Session session = factory.openSession();
        Transaction tx = null;
        Integer employeeID = null;

        try {
            tx = session.beginTransaction();
            Employee employee = new Employee(fname, lname, salary);
            employeeID = (Integer) session.save(employee);
            tx.commit();
            System.out.println("Successfully added employee: " + employee + " with ID: " + employeeID);
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return employeeID;
    }

    /* Method to READ all the employees */
    @SuppressWarnings("unchecked")
    public void listEmployees() {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List<Employee> employees = session.createQuery("FROM Employee").list();
            for (Employee employee : employees) {
                System.out.println(" - " + employee);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /* Method to READ a specific employee */
    public void getEmployee(Integer employeeId) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            System.out.println("Fetched employee details: " + employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /* Method to DELETE an employee from the records */
    public void deleteEmployee(Integer employeeId) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            if (employee != null) {
                session.delete(employee);
                System.out.println("Deleted employee: " + employee);
            } else {
                System.out.println("Employee with ID " + employeeId + " not found to delete.");
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
