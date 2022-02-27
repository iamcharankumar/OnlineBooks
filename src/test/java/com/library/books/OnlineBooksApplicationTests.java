package com.library.books;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.books.controller.Library;
import com.library.books.controller.LibraryController;
import com.library.books.repository.LibraryRepository;
import com.library.books.response.AddBookResponse;
import com.library.books.service.AddBookService;

@SpringBootTest
@AutoConfigureMockMvc
class OnlineBooksApplicationTests {

	AddBookService addBookService = new AddBookService();

	@Autowired
	LibraryController libraryController;

	@MockBean
	LibraryRepository libraryRepository;

	@MockBean
	AddBookService mockBookService;

	@Autowired
	private MockMvc mockMvc;

	private static final Logger UNIT_TEST_LOGGER = LoggerFactory.getLogger(OnlineBooksApplicationTests.class);

	@ParameterizedTest
	@DisplayName("To verify, the Book Id starts with the Alphabetic character")
	@MethodSource("provideStringsForBookId")
	public void testBuildIdLogicAlphabetic(String isbn, long aisle) {
		String id = addBookService.buildId(isbn, aisle);
		boolean isAlphabet = Character.isAlphabetic(id.charAt(0));
		if (isAlphabet) {
			assertTrue(isAlphabet, "First Letter of the Book ID is not alphabetic!");
			UNIT_TEST_LOGGER.info("Verified the First Letter of the Book ID to be Alphabetic: " + isAlphabet);
		} else {
			assertFalse(isAlphabet, "First Letter of the Book ID is alphabetic!");
			UNIT_TEST_LOGGER.info("Verified the First Letter of the Book ID as non Alphabetic: " + isAlphabet);
		}
	}

	@ParameterizedTest
	@DisplayName("To verify, the Book Id ends with a Number")
	@MethodSource("provideStringsForBookId")
	public void testBuildIdLogicNumeric(String isbn, long aisle) {
		String id = addBookService.buildId(isbn, aisle);
		boolean isNumber = Character.isDigit(id.charAt(id.length() - 1));
		if (isNumber) {
			assertTrue(isNumber, "Last Letter of the Book ID is not numeric!");
			UNIT_TEST_LOGGER.info("Verified the Last Letter of the Book ID to be Numeric: " + isNumber);
		} else {
			assertFalse(isNumber, "Last Letter of the Book ID is numeric!");
			UNIT_TEST_LOGGER.info("Verified the Last Letter of the Book ID to be non Numeric: " + isNumber);
		}
	}

	// Data Provider for Build ID Logics
	private static Stream<Arguments> provideStringsForBookId() {
		return Stream.of(Arguments.of("self-help", 202202004), Arguments.of("1", 20));
	}

	@ParameterizedTest
	@MethodSource("provideStringsForBookImpl")
	@DisplayName("To verify, the Add Book Implementation using Java Objects")
	public void testAddBookImplementation(boolean isBookPresent) {
		Library lib = buildLibrary();
		when(mockBookService.buildId(lib.getIsbn(), lib.getAisle())).thenReturn(lib.getId());
		when(mockBookService.checkBookAlreadyExists(lib.getId())).thenReturn(isBookPresent);
		ResponseEntity<AddBookResponse> testResponse = libraryController.addBookImplementation(buildLibrary());
		AddBookResponse addBookResponse = testResponse.getBody();
		if (!isBookPresent) {
			assertEquals(testResponse.getStatusCode(), HttpStatus.CREATED,
					"Status Code Mismatched for adding a new book!");
			UNIT_TEST_LOGGER
					.info("Verified the Status Code for Book not Present in the DB: " + testResponse.getStatusCode());
			assertEquals(addBookResponse.getId(), lib.getId(), "Book ID Mismatched for adding a new Book!");
			UNIT_TEST_LOGGER.info("Verified the Book ID for adding a new book: " + addBookResponse.getId());
			assertEquals(addBookResponse.getMessage(), "Book Successfully Added.",
					"Incorrect Message for adding a new book");
			UNIT_TEST_LOGGER.info("Verified the Message for adding a new book: " + addBookResponse.getMessage());
		} else {
			assertEquals(testResponse.getStatusCode(), HttpStatus.ACCEPTED,
					"Status Code Mismatched for Book Already Exists!");
			UNIT_TEST_LOGGER.info(
					"Verified the Status Code for Book Already Present in the DB: " + testResponse.getStatusCode());
			assertEquals(addBookResponse.getId(), lib.getId(), "Book ID Mismatched for book already exsits!");
			UNIT_TEST_LOGGER.info("Verified the Book ID for the book already exists: " + addBookResponse.getId());
			assertEquals(addBookResponse.getMessage(), "Book Already Exists!",
					"Incorrect Message for book already exists");
			UNIT_TEST_LOGGER.info("Verified the Message for book already exists: " + addBookResponse.getMessage());
		}
	}

	// Data Provider for Add Book API
	private static Stream<Arguments> provideStringsForBookImpl() {
		return Stream.of(Arguments.of(true), Arguments.of(false));
	}

	// Library Object for Add Book API
	public Library buildLibrary() {
		Library library = new Library();
		library.setAisle(456);
		library.setAuthor("Unit Test Author");
		library.setBook_name("Unit Test Book Name");
		library.setId("456Unit Test ISBN");
		library.setIsbn("Unit Test ISBN");
		return library;
	}

	@ParameterizedTest
	@MethodSource("provideStringsForBookImpl")
	@DisplayName("To verify, the Add Book Implementation with Mock MVC")
	public void testAddBookController(boolean isBookPresent) throws Exception {
		Library lib = buildLibrary();
		ObjectMapper objectMapper = new ObjectMapper();
		String libraryJsonString = objectMapper.writeValueAsString(lib);
		when(mockBookService.buildId(lib.getIsbn(), lib.getAisle())).thenReturn(lib.getId());
		when(mockBookService.checkBookAlreadyExists(lib.getId())).thenReturn(isBookPresent);
		when(libraryRepository.save(any())).thenReturn(lib);
		if (!isBookPresent) {
			this.mockMvc.perform(post("/addBook").contentType(MediaType.APPLICATION_JSON).content(libraryJsonString))
					.andExpect(status().isCreated()).andDo(print()).andExpect(jsonPath("$.id").value(lib.getId()));
			UNIT_TEST_LOGGER.info("Verified Adding New Book through Mock MVC");
		} else {
			this.mockMvc.perform(post("/addBook").contentType(MediaType.APPLICATION_JSON).content(libraryJsonString))
					.andExpect(status().isAccepted()).andDo(print()).andExpect(jsonPath("$.id").value(lib.getId()));
			UNIT_TEST_LOGGER.info("Verified Existing Book through Mock MVC");
		}
	}

	@Test
	@DisplayName("To verify, the Get Book By Author API with Mock MVC")
	public void testGetBookByAuthor() throws Exception {
		List<Library> libraryList = new ArrayList<Library>();
		libraryList.add(buildLibrary());
		libraryList.add(buildLibrary());
		when(libraryRepository.findAllByAuthor(any())).thenReturn(libraryList);
		this.mockMvc.perform(get("/getBooks/author").queryParam("authorName", "Unit Test Author")).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(2)))
				.andExpect(jsonPath("$.[0].id").value("456Unit Test ISBN"));
		UNIT_TEST_LOGGER.info("Verified Get Book By Author through Mock MVC!");
	}

	// Library Object for Add Book API
	public Library updateLibrary() {
		Library update = new Library();
		update.setAisle(456);
		update.setAuthor("Update Unit Test Author");
		update.setBook_name("Update Unit Test Book Name");
		return update;
	}

	@ParameterizedTest
	@MethodSource("provideStringsForBookImpl")
	@DisplayName("To verify, the Update Book By Id with Mock MVC")
	public void testUpdateBookById(boolean isBookPresent) throws Exception {
		Library lib = buildLibrary();
		ObjectMapper objectMapper = new ObjectMapper();
		String updateJsonString = objectMapper.writeValueAsString(updateLibrary());
		if (isBookPresent) {
			when(libraryRepository.getById(lib.getId())).thenReturn(lib);
			this.mockMvc.perform(
					put("/updateBook/" + lib.getId()).contentType(MediaType.APPLICATION_JSON).content(updateJsonString))
					.andDo(print()).andExpect(status().isCreated());
			UNIT_TEST_LOGGER.info("Verified Updating Existing Book through Mock MVC");
		} else {
			this.mockMvc.perform(
					put("/updateBook/" + lib.getId()).contentType(MediaType.APPLICATION_JSON).content(updateJsonString))
					.andDo(print()).andExpect(status().isNotFound());
			UNIT_TEST_LOGGER.info("Verified Book not found for Update through Mock MVC");
		}
	}

	public Library deleteLibrary() {
		Library delete = new Library();
		delete.setId("deleteId");
		return delete;
	}

	@ParameterizedTest
	@MethodSource("provideStringsForBookImpl")
	@DisplayName("To verify, the Delete Book By Id with Mock MVC")
	public void testDeleteBookById(boolean isBookPresent) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String deleteJson = objectMapper.writeValueAsString(deleteLibrary());
		if (isBookPresent) {
			when(libraryRepository.getById(any())).thenReturn(deleteLibrary());
			doNothing().when(libraryRepository).delete(deleteLibrary());
			this.mockMvc.perform(delete("/deleteBook").contentType(MediaType.APPLICATION_JSON).content(deleteJson))
					.andDo(print()).andExpect(status().isCreated())
					.andExpect(jsonPath("$.message").value("The Book is deleted!"));
			UNIT_TEST_LOGGER.info("Verified Book found for Delete through Mock MVC");
		} else {
			when(libraryRepository.getById(any())).thenReturn(buildLibrary());
			doNothing().when(libraryRepository).delete(buildLibrary());
			this.mockMvc.perform(delete("/deleteBook").contentType(MediaType.APPLICATION_JSON).content(deleteJson))
					.andDo(print()).andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message").value("Book not found!"));
			UNIT_TEST_LOGGER.info("Verified Book not found for Delete through Mock MVC");
		}
	}
}