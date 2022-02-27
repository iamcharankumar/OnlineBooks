package com.library.books.response;

import org.springframework.stereotype.Component;

@Component
public class DeleteBookResponse {

	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
