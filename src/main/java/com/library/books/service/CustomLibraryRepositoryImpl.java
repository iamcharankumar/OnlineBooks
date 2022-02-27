package com.library.books.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.library.books.controller.Library;
import com.library.books.repository.CustomLibraryRepository;
import com.library.books.repository.LibraryRepository;

public class CustomLibraryRepositoryImpl implements CustomLibraryRepository {

	@Autowired
	LibraryRepository libraryRepository;

	@Override
	public List<Library> findAllByAuthor(String authorName) {
		return libraryRepository.findAll().stream().filter(author -> author.getAuthor().equalsIgnoreCase(authorName))
				.collect(Collectors.toList());
	}
}