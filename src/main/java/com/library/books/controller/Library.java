package com.library.books.controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "books") // Table name and class name should not start with same name. It creates
						// conflict to JPA
public class Library {
	@Column(name = "book_name")
	private String book_name;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "isbn")
	private String isbn;

	@Column(name = "aisle")
	private long aisle;

	@Column(name = "author")
	private String author;

	public String getBook_name() {
		return book_name;
	}

	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public long getAisle() {
		return aisle;
	}

	public void setAisle(long aisle) {
		this.aisle = aisle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}