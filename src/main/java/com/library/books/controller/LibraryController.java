package com.library.books.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.library.books.repository.LibraryRepository;
import com.library.books.response.AddBookResponse;
import com.library.books.response.DeleteBookResponse;
import com.library.books.service.AddBookService;

@RestController
public class LibraryController {

	@Autowired
	LibraryRepository libraryRepository;

	@Autowired
	AddBookResponse addBookResponse;

	@Autowired
	AddBookService addBookService;

	@Autowired
	DeleteBookResponse deleteBookResponse;

	private static final Logger LIBRARY_LOGGER = LoggerFactory.getLogger(LibraryController.class);

	@PostMapping("/addBook")
	public ResponseEntity<AddBookResponse> addBookImplementation(@RequestBody Library library) {
		// External Dependency from Library class - Mock the below line for testing
		String libraryId = addBookService.buildId(library.getIsbn(), library.getAisle()); // mock
		boolean isBookPresent = addBookService.checkBookAlreadyExists(libraryId); // mock
		if (!isBookPresent) {
			LIBRARY_LOGGER.info("Book do not exist. Hence, creating it.");
			library.setId(libraryId);
			libraryRepository.save(library);
			addBookResponse.setId(libraryId);
			addBookResponse.setMessage("Book Successfully Added.");
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("uniqueId", libraryId);
			return new ResponseEntity<AddBookResponse>(addBookResponse, httpHeaders, HttpStatus.CREATED);
		} else {
			addBookResponse.setMessage("Book Already Exists!");
			LIBRARY_LOGGER.error("Book Already Exists! Hence, not creating.");
			addBookResponse.setId(libraryId);
			return new ResponseEntity<AddBookResponse>(addBookResponse, HttpStatus.ACCEPTED);
		}
	}

	@GetMapping("/getBooks/{id}")
	public Library getBookById(@PathVariable(value = "id") String id) {
		boolean isBookPresent = addBookService.checkBookAlreadyExists(id);
		if (isBookPresent) {
			LIBRARY_LOGGER.info("The Book is present.");
			Library getBookInfo = libraryRepository.getById(id);
			return getBookInfo;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/getBooks/author")
	public List<Library> getBooksByAuthorName(@RequestParam(value = "authorName") String authorName) {
		LIBRARY_LOGGER.info("The Book is present for the author.");
		return libraryRepository.findAllByAuthor(authorName);
	}

	@PutMapping("/updateBook/{id}")
	public ResponseEntity<Library> updateBooksById(@PathVariable(value = "id") String id,
			@RequestBody Library library) {
		boolean isBookPresent = addBookService.checkBookAlreadyExists(id);
		if (isBookPresent) {
			Library existingBook = libraryRepository.getById(id);
			existingBook.setAisle(library.getAisle());
			existingBook.setAuthor(library.getAuthor());
			existingBook.setBook_name(library.getBook_name());
			libraryRepository.save(existingBook);
			LIBRARY_LOGGER.info("The Book is updated.");
			return new ResponseEntity<Library>(existingBook, HttpStatus.OK);
		} else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/deleteBook")
	public ResponseEntity<DeleteBookResponse> deleteBookById(@RequestBody Library library) {
		String bookId = library.getId();
		boolean isBookPresent = addBookService.checkBookAlreadyExists(bookId);
		if (isBookPresent) {
			libraryRepository.deleteById(bookId);
			deleteBookResponse.setMessage("Book is deleted!");
			LIBRARY_LOGGER.info("The Book is deleted!");
			return new ResponseEntity<DeleteBookResponse>(deleteBookResponse, HttpStatus.CREATED);
		} else {
			deleteBookResponse.setMessage("Book not found!");
			LIBRARY_LOGGER.error("Book not found! Hence, not deleting.");
			return new ResponseEntity<DeleteBookResponse>(deleteBookResponse, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/getAllBooks")
	public List<Library> getAllBooks() {
		List<Library> allBooks = libraryRepository.findAll();
		if (allBooks.size() > 0) {
			LIBRARY_LOGGER.info("All Books retreived!");
			return allBooks;
		} else {
			LIBRARY_LOGGER.error("Table is empty!");
			return null;
		}
	}
}