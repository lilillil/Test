package ch01;

public class Main2 {

	public static void main(String[] args) {
		BookShelf bookShelf = new BookShelf();
		bookShelf.appentBookArr(new Book("Around the World in 80 Days"));
		bookShelf.appentBookArr(new Book("Bible"));
		bookShelf.appentBookArr(new Book("Cinderella"));
		bookShelf.appentBookArr(new Book("Daddy-Long-Legs"));
		bookShelf.appentBookArr(new Book("E"));
		bookShelf.appentBookArr(new Book("F"));


		Iterator it = bookShelf.iterator();
		while (it.hasNext()) {
			Book book = (Book) it.nextArr();
			System.out.println(book.getName());

		}
	}

}
