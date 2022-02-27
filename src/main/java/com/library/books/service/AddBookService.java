package com.library.books.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.books.controller.Library;
import com.library.books.repository.LibraryRepository;

@Service
public class AddBookService {

	@Autowired
	LibraryRepository libraryRepository;

	// Returns Library Book ID
	public String buildId(String isbn, long aisle) {
		return isbn + aisle;
	}

	// Checks whether book already present in the DB
	public boolean checkBookAlreadyExists(String id) {
		Optional<Library> isBookPresent = libraryRepository.findById(id);
		return isBookPresent.isPresent();
	}
}