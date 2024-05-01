package com.app.vp.wookiebooks.repository;

import com.app.vp.wookiebooks.model.Book;
import com.app.vp.wookiebooks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface extends the `JpaRepository` provided by Spring Data JPA.
 * It provides basic CRUD operations (Create, Read, Update, Delete) for Book;
 * Custom methods provides to.
 * Common database operations based on type (Book) and its identifier (Long).
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}