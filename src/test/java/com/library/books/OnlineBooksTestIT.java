package com.library.books;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.library.books.controller.Library;

@SpringBootTest
public class OnlineBooksTestIT {

	private static final String API_SERVER = "http://localhost:8081";
	private static final String API_GET_BOOK_BY_AUTHOR = "/getBooks/author?authorName=";
	private static final String API_ADD_BOOK = "/addBook";
	private TestRestTemplate testRestTemplate = new TestRestTemplate();

	private static final Logger INTEGRATION_TEST_LOGGER = LoggerFactory.getLogger(OnlineBooksTestIT.class);

	@Test
	@DisplayName("To veirfy, the Get Book By Author Name API through H2 DB")
	public void testGetBookByAuthorName() throws IOException, JSONException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("GetBookByAuthorNameTestData.json").getFile());
		String getBookByAuthorJson = new String(Files.readAllBytes(file.toPath()));
		ResponseEntity<String> getBookByAuthorNameResponse = testRestTemplate
				.getForEntity(API_SERVER + API_GET_BOOK_BY_AUTHOR + "charan", String.class);
		assertEquals(getBookByAuthorNameResponse.getStatusCodeValue(), 200,
				"Status Code Mismatched for Get Book By Author Name!");
		INTEGRATION_TEST_LOGGER
				.info("Get Book By Author Name Response Status Code: " + getBookByAuthorNameResponse.getStatusCode());
		JSONAssert.assertEquals(getBookByAuthorJson, getBookByAuthorNameResponse.getBody(), false);
		INTEGRATION_TEST_LOGGER.info("Get Book By Author Name Response: " + getBookByAuthorNameResponse.getBody());
	}

	@Test
	@DisplayName("To veirfy, the Add Book API through H2 DB")
	public void testAddBook() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Library> httpRequest = new HttpEntity<Library>(buildLibrary(), headers);
		ResponseEntity<String> addBookResponse = testRestTemplate.postForEntity(API_SERVER + API_ADD_BOOK, httpRequest,
				String.class);
		assertEquals(201, addBookResponse.getStatusCodeValue(), "Add Book Response Status Code Mismatched!");
		INTEGRATION_TEST_LOGGER.info("Add Book Response Status Code: " + addBookResponse.getStatusCodeValue());
		assertEquals(buildLibrary().getId(), addBookResponse.getHeaders().get("uniqueId").get(0),
				"Add Book Response Headers Mismatched!");
		INTEGRATION_TEST_LOGGER
				.info("Add Book Response Headers: " + addBookResponse.getHeaders().get("uniqueId").get(0));
	}

	// Library Object for Add Book API
	public Library buildLibrary() {
		Library library = new Library();
		library.setAisle(123);
		library.setAuthor("Int Test Author");
		library.setBook_name("Int Test Book Name");
		library.setId("Int Test ISBN123");
		library.setIsbn("Int Test ISBN");
		return library;
	}
}