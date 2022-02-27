package com.library.books.repository;

import java.util.List;

import com.library.books.controller.Library;

public interface CustomLibraryRepository {

	public List<Library> findAllByAuthor(String authorName);
}
