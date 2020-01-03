package com.example.demo;

import org.junit.After;
import org.junit.Before;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class BasicJPATest {

    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    @Before
    public void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa.sample.plain");
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
    }

    @After
    public void tearDown() {

        try {
            entityManager.getTransaction().commit();
        } catch (Exception e) {

        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }
}
