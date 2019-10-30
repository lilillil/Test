package ch01;

public class Main {

	public static void main(String[] args) {
		BookShelf bookShelf = new BookShelf(4);
		bookShelf.appentBook(new Book("Around the World in 80 Days"));
		bookShelf.appentBook(new Book("Bible"));
		bookShelf.appentBook(new Book("Cinderella"));
		bookShelf.appentBook(new Book("Daddy-Long-Legs"));

		Iterator it = bookShelf.iterator();
		while (it.hasNext()) {
			Book book = (Book) it.next();
			System.out.println(book.getName());

		}
	}

}
