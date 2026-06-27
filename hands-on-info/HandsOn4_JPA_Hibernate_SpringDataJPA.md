# Hands-on 4: Differences Between JPA, Hibernate, and Spring Data JPA

This document explains the conceptual differences and code-level comparisons between **Java Persistence API (JPA)**, **Hibernate**, and **Spring Data JPA**.

---

## 1. Conceptual Summary

| Feature | JPA (Java Persistence API) | Hibernate | Spring Data JPA |
| :--- | :--- | :--- | :--- |
| **What is it?** | A **Specification** (JSR 338). It defines standard interfaces and guidelines for ORM. | An **ORM Framework / Library**. It is a concrete implementation of the JPA specification. | A **Spring Data Module**. It is an abstraction layer built on top of JPA providers (like Hibernate). |
| **Implementation?** | No implementation (just interfaces, annotations, and contracts like `EntityManager`). | Yes, contains actual implementation code (e.g. `SessionFactory`, `Session`, `Transaction`). | No persistence implementation. It converts your repository interfaces into database queries using a JPA provider. |
| **Boilerplate Code** | High (if using plain JPA `EntityManager` factories and transaction handlers). | High (requires session opening, closing, transaction begin/commit/rollback blocks). | Extremely Low (eliminates almost all boilerplate code by auto-generating CRUD queries). |
| **Transaction Management** | Manual (unless using container-managed JTA transactions). | Manual (via Hibernate `Transaction` object or JTA). | Automatic (via Spring's `@Transactional` annotation). |

---

## 2. Code-Level Comparisons

### Scenario: Creating and Persisting an `Employee` Object

Here is how the code compares between using plain **Hibernate** vs **Spring Data JPA**:

### Approach A: Plain Hibernate
In plain Hibernate, you must manually open a session, begin a transaction, save the object, commit the transaction, handle any exceptions with rollback, and ensure the session is closed in a `finally` block to prevent leaks.

```java
/* Method to CREATE an employee in the database using plain Hibernate */
public Integer addEmployee(Employee employee) {
    Session session = factory.openSession();
    Transaction tx = null;
    Integer employeeID = null;
    
    try {
        tx = session.beginTransaction();
        employeeID = (Integer) session.save(employee); 
        tx.commit();
    } catch (HibernateException e) {
        if (tx != null) {
            tx.rollback();
        }
        e.printStackTrace(); 
    } finally {
        session.close(); 
    }
    return employeeID;
}
```

### Approach B: Spring Data JPA
In Spring Data JPA, you define an interface extending `JpaRepository`. Spring Boot automatically provides the runtime implementation for all CRUD methods (like `save()`, `delete()`, `findById()`, etc.). You simply autowire the repository and use `@Transactional` to manage transactions.

#### 1. Repository Interface (`EmployeeRepository.java`)
```java
package com.cognizant.ormlearn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cognizant.ormlearn.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // Zero implementation code needed here for standard CRUD!
}
```

#### 2. Service Class (`EmployeeService.java`)
```java
package com.cognizant.ormlearn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cognizant.ormlearn.model.Employee;
import com.cognizant.ormlearn.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public void addEmployee(Employee employee) {
        // Boilerplate transaction management, session opening/closing is handled entirely by Spring!
        employeeRepository.save(employee);
    }
}
```
