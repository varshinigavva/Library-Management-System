import java.util.*;
public class LibraryTerminal {
static Scanner sc = new Scanner(System.in); 
static Map<String, User> users = new LinkedHashMap<>();
static Map<Integer, Book> books = new LinkedHashMap<>();
static List<BorrowingRecord> borrowingRecords = new ArrayList<>();
static int nextBook = 1, nextLoan = 1;
static final int LIMIT = 3;
enum Role { ADMIN, LIBRARIAN, STUDENT }
static class User {
String username, password, name, studentId; Role role;
boolean active = true; User(String u, String p, String n, Role r, String sid) {
username=u; password=p; name=n; role=r; studentId=sid;
}
} static class Book {
int id; String title, author, category; boolean active=true; Book(int i, String t, String a, String c) { 
    id=i; title=t; author=a; category=c; }
}
 static class BorrowingRecord {
int id, bookId; String username, borrowedOn; boolean returned=false; String returnedOn="-";
BorrowingRecord(int i, int b, String u) { 
    id=i; bookId=b; username=u; borrowedOn=new Date().toString(); }
}
 public static void main(String[] args) {
seed();
 System.out.println("========================================");
System.out.println(" COLLEGE LIBRARY MANAGEMENT SYSTEM"); 
System.out.println("          TERMINAL EDITION");
System.out.println("========================================"); while (true) {
User u = login(); if (u == null) break;
System.out.println("\nWelcome " + u.name + " [" + u.role + "]"); menu(u);
System.out.println("\nLogged out.");
} System.out.println("Goodbye!");
}
static void seed() {
users.put("admin", new User("admin","Admin@123","System Administrator",Role.ADMIN,""));
users.put("librarian", new User("librarian","Lib@123","Main Librarian",Role.LIBRARIAN,""));
users.put("student1", new User("student1","Stu@123","Aarav Kumar",Role.STUDENT,"STU1001"));
users.put("student2", new User("student2","Stu@123","Priya Sharma",Role.STUDENT,"STU1002"));
 addSeed("Clean Code","Robert C. Martin","Programming");
addSeed("Effective Java","Joshua Bloch","Programming");
 addSeed("Introduction to Algorithms","Thomas H. Cormen","Algorithms");
addSeed("Artificial Intelligence: A Modern Approach","Stuart Russell","AI"); 
addSeed("Database System Concepts","Abraham Silberschatz","Database");
addSeed("Computer Networks","Andrew S. Tanenbaum","Networking");
}
static void addSeed(String t,String a,String c) {
     books.put(nextBook,new Book(nextBook++,t,a,c)); }
static User login() {
for (int i=3;i>0;i--) {
System.out.print("\nUsername (or exit): "); String name=read(); if (name.equalsIgnoreCase("exit")) return null;
System.out.print("Password: "); String pass=read(); User u=users.get(name);
if (u!=null && u.active && u.password.equals(pass)) return u; 
System.out.println("Invalid login. Attempts remaining: "+(i-1));
} System.out.println("Too many failed attempts."); return null;
}
static void menu(User u) {
while (true) {
System.out.println("\n--- "+u.role+" MENU ---");
 System.out.println("1. Browse/search books");
System.out.println("2. View my borrowing history"); if (u.role!=Role.STUDENT) {
System.out.println("3. Add book");
 System.out.println("4. Edit book");
System.out.println("5. Deactivate book");
 System.out.println("6. Register student");
System.out.println("7. View students"); 
System.out.println("8. Edit student");
System.out.println("9. Deactivate student");
 System.out.println("10. Issue book");
System.out.println("11. Return book");
 System.out.println("12. View all borrowingRecords");
System.out.println("13. Three-book-limit warning report");
} if (u.role==Role.ADMIN) {
System.out.println("14. Create librarian/student account"); 
System.out.println("15. Enable/disable account");
System.out.println("16. View all accounts");
} System.out.println("0. Log out");
int c=number("Choice: "); if(c==0) return;
switch(c) {
case 1: browseBooks(); break; 
case 2: myLoans(u); break;
case 3: if(staff(u)) addBook(); else denied(); break; 
case 4: if(staff(u)) editBook(); else denied(); break;
case 5: if(staff(u)) deactivateBook(); else denied(); break; 
case 6: if(staff(u)) registerStudent(); else denied(); break;
case 7: if(staff(u)) showStudents(); else denied(); break;
 case 8: if(staff(u)) editStudent(); else denied(); break;
case 9: if(staff(u)) deactivateStudent(); else denied(); break; 
case 10: if(staff(u)) issueBook(); else denied(); break;
case 11: if(staff(u)) returnBook(); else denied(); break; 
case 12: if(staff(u)) allLoans(); else denied(); break;
case 13: if(staff(u)) limitReport(); else denied(); break; 
case 14: if(u.role==Role.ADMIN) createAccount(); else denied(); break;
case 15: if(u.role==Role.ADMIN) toggleAccount(); else denied(); break; 
case 16: if(u.role==Role.ADMIN) showAccounts(); else denied(); break;
default: System.out.println("Invalid option.");
}
}
}
static boolean staff(User u) { return u.role==Role.ADMIN || u.role==Role.LIBRARIAN; }
static void browseBooks() {
System.out.println("1 All books  2 Search title/author  3 Filter category  4 Sort by title"); int c=number("Choose: "); List<Book> list=activeBooks();
if(c==2) { System.out.print("Search: "); String q=read().toLowerCase(); list.removeIf(b->!b.title.toLowerCase().contains(q)&&!b.author.toLowerCase().contains(q)); }
else if(c==3) { System.out.print("Category: "); String q=read().toLowerCase(); list.removeIf(b->!b.category.toLowerCase().contains(q)); }
else if(c==4) list.sort(Comparator.comparing(b->b.title.toLowerCase())); printBooks(list);
}
static List<Book> activeBooks() {
List<Book> out=new ArrayList<>(); for(Book b:books.values()) if(b.active) out.add(b); return out;
}
static void printBooks(List<Book> list) {
System.out.println("\nID | Title | Author | Category | Status");
for(Book b:list) System.out.println(b.id+" | "+b.title+" | "+b.author+" | "+b.category+" | "+(isBorrowed(b.id)?"Borrowed":"Available"));
if(list.isEmpty()) System.out.println("No books found.");
}
static void addBook() {
String t=ask("Title: "), a=ask("Author: "), c=ask("Category: "); Book b=new Book(nextBook++,t,a,c); books.put(b.id,b);
 System.out.println("Book added with ID "+b.id);
}
static void editBook() {
printBooks(activeBooks()); Book b=books.get(number("Book ID: "));
 if(b==null||!b.active){
    System.out.println("Book not found.");return;}
b.title=defaultValue("Title",b.title); b.author=defaultValue("Author",b.author); b.category=defaultValue("Category",b.category);
 System.out.println("Book updated.");
}
static void deactivateBook() {
printBooks(activeBooks()); Book b=books.get(number("Book ID: "));
 if(b==null||!b.active){
    System.out.println("Book not found.");
    return;
 }
