package com.library.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.books.controller.Library;

@Repository
public interface LibraryRepository extends JpaRepository<Library, String>, CustomLibraryRepository {

}