package ch01;

import java.util.ArrayList;

public class BookShelf implements Aggregate {

	private Book[] books;
	private int last = 0;

	private ArrayList<Book> booksArr;

	public BookShelf(int maxsize) {
		this.books = new Book[maxsize];
	}

	public BookShelf() {
		this.booksArr = new ArrayList<Book>();
	}

	public Book getBookAt(int index) {
		return books[index];
	}


	public void appentBook(Book book) {
		this.books[last] = book;
		last++;
	}


	public int getLength() {
		return last;
	}

	public void appentBookArr(Book book) {
		this.booksArr.add(book);
		last++;
	}

	public Book getBookAtArr(int index) {
		return booksArr.get(index);
	}

	@Override
	public Iterator iterator() {
		return new BookShelfIterator(this);
	}

}
