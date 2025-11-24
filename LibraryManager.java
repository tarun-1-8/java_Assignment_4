import java.io.*;
import java.util.*;

class Book implements Comparable<Book> {
    int bookId;
    String title, author, category;
    boolean isIssued;

    Book(int id, String t, String a, String c) {
        bookId = id;
        title = t;
        author = a;
        category = c;
    }

    void displayBookDetails() {
        System.out.println(bookId + " | " + title + " | " + author + " | " + category + " | Issued: " + isIssued);
    }

    void markAsIssued() { isIssued = true; }
    void markAsReturned() { isIssued = false; }

    public int compareTo(Book b) { return title.compareToIgnoreCase(b.title); }
}

class AuthorComparator implements Comparator<Book> {
    public int compare(Book a, Book b) { return a.author.compareToIgnoreCase(b.author); }
}

class Member {
    int memberId;
    String name, email;
    List<Integer> issuedBooks = new ArrayList<>();

    Member(int id, String n, String e) {
        memberId = id;
        name = n;
        email = e;
    }

    void displayMemberDetails() {
        System.out.println(memberId + " | " + name + " | " + email + " | Issued: " + issuedBooks);
    }

    void addIssuedBook(int b) { issuedBooks.add(b); }
    void returnIssuedBook(int b) { issuedBooks.remove(Integer.valueOf(b)); }
}

public class LibraryManager {

    Map<Integer, Book> books = new HashMap<>();
    Map<Integer, Member> members = new HashMap<>();
    Set<String> categories = new HashSet<>();
    Scanner sc = new Scanner(System.in);
    int nextBookId = 101, nextMemberId = 1;

    void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt"))) {
            for (Book b : books.values())
                bw.write(b.bookId + "," + b.title + "," + b.author + "," + b.category + "," + b.isIssued + "\n");
        } catch (Exception e) {}

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("members.txt"))) {
            for (Member m : members.values())
                bw.write(m.memberId + "," + m.name + "," + m.email + "," + m.issuedBooks + "\n");
        } catch (Exception e) {}
    }

    void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] x = line.split(",");
                Book b = new Book(Integer.parseInt(x[0]), x[1], x[2], x[3]);
                b.isIssued = Boolean.parseBoolean(x[4]);
                books.put(b.bookId, b);
                categories.add(x[3]);
                nextBookId = Math.max(nextBookId, b.bookId + 1);
            }
        } catch (Exception e) {}
    }

    void addBook() {
        sc.nextLine();
        System.out.print("Enter Book Title: ");
        String t = sc.nextLine();

        System.out.print("Enter Author: ");
        String a = sc.nextLine();

        System.out.print("Enter Category: ");
        String c = sc.nextLine();

        Book b = new Book(nextBookId++, t, a, c);
        books.put(b.bookId, b);
        categories.add(c);
        saveToFile();

        System.out.println("Book added successfully with ID: " + b.bookId + "\n");
    }

    void addMember() {
        sc.nextLine();
        System.out.print("Name: ");
        String n = sc.nextLine();
        System.out.print("Email: ");
        String e = sc.nextLine();

        Member m = new Member(nextMemberId++, n, e);
        members.put(m.memberId, m);
        saveToFile();

        System.out.println("Member added with ID: " + m.memberId + "\n");
    }

    void issueBook() {
        System.out.print("Book ID: ");
        int b = sc.nextInt();
        System.out.print("Member ID: ");
        int m = sc.nextInt();

        if (books.containsKey(b) && members.containsKey(m)) {
            books.get(b).markAsIssued();
            members.get(m).addIssuedBook(b);
            saveToFile();
            System.out.println("Book Issued.\n");
        }
    }

    void returnBook() {
        System.out.print("Book ID: ");
        int b = sc.nextInt();
        System.out.print("Member ID: ");
        int m = sc.nextInt();

        if (books.containsKey(b) && members.containsKey(m)) {
            books.get(b).markAsReturned();
            members.get(m).returnIssuedBook(b);
            saveToFile();
            System.out.println("Book Returned.\n");
        }
    }

    void searchBooks() {
        sc.nextLine();
        System.out.print("Search: ");
        String k = sc.nextLine().toLowerCase();

        for (Book b : books.values()) {
            if (String.valueOf(b.bookId).equals(k) ||
                    b.title.toLowerCase().contains(k) ||
                    b.author.toLowerCase().contains(k) ||
                    b.category.toLowerCase().contains(k))
                b.displayBookDetails();
        }
        System.out.println();
    }

    void sortBooks() {
        List<Book> list = new ArrayList<>(books.values());
        System.out.println("1. Sort by Title\n2. Sort by Author");
        int ch = sc.nextInt();

        if (ch == 1) Collections.sort(list);
        else Collections.sort(list, new AuthorComparator());

        for (Book b : list) System.out.println(b);
        System.out.println();
    }

    public void menu() {
        loadFromFile();
        int c;
        do {
            System.out.println("1 AddBook  2 AddMember  3 Issue  4 Return  5 Search  6 Sort  7 Exit");
            c = sc.nextInt();

            switch (c) {
                case 1: addBook(); break;
                case 2: addMember(); break;
                case 3: issueBook(); break;
                case 4: returnBook(); break;
                case 5: searchBooks(); break;
                case 6: sortBooks(); break;
            }
        } while (c != 7);
    }

    public static void main(String[] args) {
        new LibraryManager().menu();
    }
}