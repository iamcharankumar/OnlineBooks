package com.library.books.response;

import org.springframework.stereotype.Component;

@Component
public class AddBookResponse {

	String message;
	String id;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
