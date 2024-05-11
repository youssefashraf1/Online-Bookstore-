import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class ClientHandler extends Thread {
    Socket client;
    DataInputStream in;
    DataOutputStream out;
    final ArrayList<String> reseviedMess =new ArrayList<>();
    ClientHandler(Socket clientS){
        this.client=clientS;
        try {
            this.in=new DataInputStream(client.getInputStream());
            this.out=new DataOutputStream(client.getOutputStream());
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    String role = null;

    @Override
    public void run() {
        try {

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "Y.ashraf253");

            while (true) {


                try {

                    while (true){
                        out.writeUTF("1-Login \n2-Register");
                        String choice = in.readUTF();

                        if ("1".equals(choice)) {
                          String username=  handleLogin(connection, in, out);
                          if (!username.equals("not login")){

                              if (Objects.equals(role, "user")){
                                  userbookStore(connection,in,out,username);

                              }else if (Objects.equals(role, "admin")){
                                  adminbookStore(connection,in,out,username);
                              }


                          }
                            break;
                        } else if ("2".equals(choice)) {
                            handleRegistration(connection, in, out);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                client.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public void sendMess(String str){
        try {
            out.writeUTF(str);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String>  getMessages(){
        return reseviedMess;
    }
    private String handleLogin(Connection connection, DataInputStream in, DataOutputStream out) throws SQLException {
        ArrayList<String> usernames = getUsernames(connection);
        try {
            while (true) {
                out.writeUTF("Enter Username");
                String username = in.readUTF();
                if (!usernames.contains(username)) {
                    out.writeUTF("404");
                } else {
                    PreparedStatement check = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
                    check.setString(1, username);
                    ResultSet result = check.executeQuery();
                    if (result.next()) {
                        String passwordFromDB = result.getString("password");
                        out.writeUTF("Enter Password");
                        String password = in.readUTF();
                        if (password.equals(passwordFromDB)) {

                            out.writeUTF("Login successful. Welcome, " + username + "!");
                           this.role= result.getString("_role");
                            out.writeUTF(this.role);
                            return username;
                        } else {
                            out.writeUTF("401");
                        }
                    }

                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "not login";

    }

    private static void handleRegistration(Connection connection, DataInputStream in, DataOutputStream out) throws SQLException {
        ArrayList<String> usernames = getUsernames(connection);

        try {
            while (true) {
                out.writeUTF("Enter Username");
                String username = in.readUTF();
                if (usernames.contains(username)) {
                    out.writeUTF("404");
                } else {
                    out.writeUTF("Enter Name");
                    String name = in.readUTF();
                    out.writeUTF("Enter Password");
                    String password = in.readUTF();

                    PreparedStatement add_user = connection.prepareStatement("INSERT INTO users (username, password, name) VALUES (?, ?, ?)");
                    add_user.setString(1, username);
                    add_user.setString(2, password);
                    add_user.setString(3, name);
                    add_user.executeUpdate();
                    out.writeUTF("Registered successfully. Welcome, " + username + "!");
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static ArrayList<String> getUsernames(Connection connection) throws SQLException {
        ArrayList<String> usernames = new ArrayList<>();
        Statement stm = connection.createStatement();
        ResultSet users = stm.executeQuery("SELECT username FROM users");
        while (users.next()) {
            usernames.add(users.getString("username"));
        }
        users.close();
        stm.close();
        return usernames;
    }

    private static void userbookStore(Connection connection, DataInputStream in, DataOutputStream out , String username) throws SQLException{
        try {
            label:
            while (true) {

                out.writeUTF("1-Browse and Search \n" +
                        "2-Add book to lend\n" +
                        "3-Remove book\n" +
                        "4-Submit a Request\n"+
                        "5-Incoming Requests\n"+
                        "6-Request History\n"+
                        "7-logout\n");
                String  choice = in.readUTF();
                switch (choice) {
                    case "1":

                        Search(connection, in, out);

                        break;
                    case "2":

                        AddBook(connection, in, out, username);

                        break;
                    case "3":

                        RemoveBook(connection, in, out, username);

                        break;
                    case "4":
                        SubmitRequest(connection, in, out, username);
                        break;

                    case "5":
                        HandleRequests(connection, in, out, username);

                        break;
                    case "6":
                        RequestHistory(connection, in, out, username);

                        break;
                    case "7":
                        break label;
                }




            }
        }catch (Exception e) {
            e.printStackTrace();
        }




    }


    private static void Search(Connection connection, DataInputStream in, DataOutputStream out) throws SQLException{

        try {
            while (true){
                String  choise = in.readUTF();
                if (choise.equals("1")){
                    PreparedStatement books = connection.prepareStatement("SELECT * FROM books");
                    ResultSet result = books.executeQuery();
                    Display(in,out,result);

                    break;

                }else if (choise.equals("2")){
                    out.writeUTF("Search by (title, author, genre)" );
                    String  by = in.readUTF();
                    out.writeUTF(by+":  " );
                    String  book = in.readUTF();
                    if(by.equals("title")){
                        PreparedStatement books = connection.prepareStatement("SELECT * FROM books WHERE title=?");
                        books.setString(1, book);
                        ResultSet result = books.executeQuery();
                        Display(in,out,result);
                    }else  if(by.equals("author")){
                        PreparedStatement books = connection.prepareStatement("SELECT * FROM books WHERE author=?");
                        books.setString(1, book);
                        ResultSet result = books.executeQuery();
                        Display(in,out,result);
                    } if(by.equals("genre")){
                        PreparedStatement books = connection.prepareStatement("SELECT * FROM books WHERE genre=?");
                        books.setString(1, book);
                        ResultSet result = books.executeQuery();
                        Display(in,out,result);
                    }



                    break;

                }


            }


        }catch (Exception e) {
            e.printStackTrace();
        }


    }
    private static void Display(DataInputStream in, DataOutputStream out, ResultSet result) throws SQLException{
        try {
            int x =0;
            ArrayList<String> author=new ArrayList<>();
            ArrayList<String> genre=new ArrayList<>();
            ArrayList<String> title=new ArrayList<>();
            ArrayList<String> price=new ArrayList<>();

            while (result.next()){
                String author1 = result.getString("author");
                String genre2 = result.getString("genre");
                String title2 = result.getString("title");
                String price1 =result.getString("price");
                author.add(author1);
                genre.add(genre2);
                title.add(title2);
                price.add(price1);
                x++;
            }

            out.writeInt(x);
            int z=0;
            while (z<x){


                out.writeUTF("----------------------------");
                out.writeUTF("title : "+title.get(z)+"\ngenre : "+genre.get(z)+
                        "\nauthor :" +author.get(z) +"\nprice : "+price.get(z));
                out.writeUTF("----------------------------");
                z++;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static void AddBook(Connection connection, DataInputStream in, DataOutputStream out, String username) throws SQLException {
        try {
            PreparedStatement user = connection.prepareStatement("SELECT * FROM users WHERE username=?");
            user.setString(1, username);
            ResultSet backf = user.executeQuery();

            if (backf.next()) {
                String userId = backf.getString("user_id");

                while (true) {
                    out.writeUTF("book title:");
                    String title = in.readUTF();

                    out.writeUTF("book author:");
                    String author = in.readUTF();

                    out.writeUTF("book genre:");
                    String genre = in.readUTF();

                    out.writeUTF("book price:");
                    String price = in.readUTF();

                    PreparedStatement add_book = connection.prepareStatement("INSERT INTO books (title, author, genre, user_id, price,quantity) VALUES (?, ?, ?, ?, ?,?)");
                    add_book.setString(1, title);
                    add_book.setString(2, author);
                    add_book.setString(3, genre);
                    add_book.setString(4, userId);
                    add_book.setString(5, price);
                    add_book.setString(6,"1");

                    add_book.executeUpdate();
                    add_book.close();

                    PreparedStatement check = connection.prepareStatement("SELECT * FROM books WHERE title=? AND user_id=?");
                    check.setString(1, title);
                    check.setString(2, userId);
                    ResultSet added = check.executeQuery();

                    if (added.next()) {
                        String bookId = added.getString("book_id");
                        PreparedStatement user_book = connection.prepareStatement("INSERT INTO userbooks (book_id, user_id, is_available) VALUES (?, ?, ?)");
                        user_book.setInt(1, Integer.parseInt(bookId));
                        user_book.setInt(2, Integer.parseInt(userId));
                        user_book.setInt(3, 1);
                        user_book.executeUpdate();
                        user_book.close();

                        out.writeUTF("Book Added");
                        break;
                    } else {
                        out.writeUTF("Something went wrong. Please try again.");
                    }

                    check.close();
                    added.close();
                }
            } else {
                out.writeUTF("User not found.");
            }

            user.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RemoveBook(Connection connection, DataInputStream in, DataOutputStream out, String username) throws SQLException {
        try {
            PreparedStatement user = connection.prepareStatement("SELECT * FROM users WHERE username=?");
            user.setString(1, username);
            ResultSet backf = user.executeQuery();
            if (backf.next()) {
                String userId = backf.getString("user_id");

                out.writeUTF("Enter book title to remove:");
                String title = in.readUTF();

                PreparedStatement books = connection.prepareStatement("SELECT * FROM books WHERE title=? AND user_id=?");
                books.setString(1, title);
                books.setString(2, userId);
                ResultSet result = books.executeQuery();
                if (result.next()) {
                    PreparedStatement dd = connection.prepareStatement("DELETE FROM books WHERE title=? AND user_id=?");
                    dd.setString(1, title);
                    dd.setString(2, userId);
                    dd.executeQuery();
                    String bookId = result.getString("book_id");

                    PreparedStatement delete = connection.prepareStatement("DELETE FROM userbooks WHERE book_id=? AND user_id=?");
                    delete.setString(1, bookId);
                    delete.setString(2, userId);
                    int rowsDeleted = delete.executeUpdate();
                    if (rowsDeleted > 0) {
                        out.writeUTF("Book removed successfully");
                    } else {
                        out.writeUTF("Their is no book with this name.");
                    }
                }


            } else {
                out.writeUTF("User not found.");
            }

            user.close();
        } catch (Exception e) {

        }
    }

    private static void SubmitRequest(Connection connection, DataInputStream in, DataOutputStream out, String username) {
        try {
            PreparedStatement userQuery = connection.prepareStatement("SELECT * FROM users WHERE username=?");
            userQuery.setString(1, username);
            ResultSet userResult = userQuery.executeQuery();

            if (userResult.next()) {
                String userId = userResult.getString("user_id");

                String bookTitle = in.readUTF();

                PreparedStatement bookQuery = connection.prepareStatement("SELECT * FROM books WHERE title=?");
                bookQuery.setString(1, bookTitle);
                ResultSet bookResult = bookQuery.executeQuery();

                if (bookResult.next()) {
                    String lenderId = bookResult.getString("user_id");
                    String bookId = bookResult.getString("book_id");

                    PreparedStatement requestQuery = connection.prepareStatement("INSERT INTO requests (book_id, borrower_id, lender_id, status) VALUES (?, ?, ?, ?)");
                    requestQuery.setString(1, bookId);
                    requestQuery.setString(2, userId);
                    requestQuery.setString(3, lenderId);
                    requestQuery.setString(4, "pending");
                    requestQuery.executeUpdate();

                    out.writeUTF("1");
                } else {
                    out.writeUTF("Book not found.");
                }
            }
        } catch (Exception e) {
        }
    }
    private static void HandleRequests(Connection connection, DataInputStream in, DataOutputStream out, String username) throws SQLException, IOException {
        try (PreparedStatement userStmt = connection.prepareStatement("SELECT * FROM users WHERE username=?")) {
            userStmt.setString(1, username);
            ResultSet userResult = userStmt.executeQuery();

            if (userResult.next()) {
                String lenderId = userResult.getString("user_id");
                try (PreparedStatement requestsStmt = connection.prepareStatement("SELECT * FROM requests WHERE lender_id=?")) {
                    requestsStmt.setString(1, lenderId);
                    ResultSet reqListResult = requestsStmt.executeQuery();

                    ArrayList<String> listBooks = new ArrayList<>();
                    ArrayList<String> status = new ArrayList<>();
                    ArrayList<String> reqIds = new ArrayList<>();

                    int count = 0;
                    while (reqListResult.next()) {
                        String bookId = reqListResult.getString("book_id");
                        try (PreparedStatement booksStmt = connection.prepareStatement("SELECT * FROM books WHERE book_id=?")) {
                            booksStmt.setString(1, bookId);
                            ResultSet bookResult = booksStmt.executeQuery();

                            if (bookResult.next()) {
                                String title = bookResult.getString("title");
                                String stat = reqListResult.getString("status");
                                String id = reqListResult.getString("request_id");
                                listBooks.add(title);
                                status.add(stat);
                                reqIds.add(id);
                                count++;
                            }
                        }
                    }

                    out.writeInt(count);
                    for (int i = 0; i < listBooks.size(); i++) {
                        out.writeUTF(listBooks.get(i));
                        out.writeUTF(status.get(i));
                    }

                    int num = in.readInt();
                    int choice = in.readInt();
                    if (choice == 1) {
                        updateRequestStatus(connection, reqIds.get(num), "accept");
                        PreparedStatement booksStmt = connection.prepareStatement("SELECT * FROM requests WHERE request_id=?");
                        booksStmt.setString(1, reqIds.get(num));
                        ResultSet bookResult = booksStmt.executeQuery();
                        if (bookResult.next()){
                            PreparedStatement w = connection.prepareStatement("SELECT * FROM requests WHERE request_id=?");
                            w.setString(1, reqIds.get(num));
                            ResultSet b = booksStmt.executeQuery();
                            String id= b.getString("book_id");
                            PreparedStatement x = connection.prepareStatement("UPDATE  requests SET is_available=? WHERE book_id=?");
                            x.setInt(1, 0);
                            x.setString(2,id);
                            ResultSet q = booksStmt.executeQuery();
                        }

                        out.writeUTF("Request accepted");
                    } else if (choice == 2) {
                        updateRequestStatus(connection, reqIds.get(num), "reject");
                        out.writeUTF("Request rejected");
                    }
                }
            }
        }
    }

    private static void updateRequestStatus(Connection connection, String requestId, String status) throws SQLException {
        try (PreparedStatement updateStmt = connection.prepareStatement("UPDATE requests SET status = ? WHERE request_id=?")) {
            updateStmt.setString(1, status);
            updateStmt.setString(2, requestId);
            updateStmt.executeUpdate();
        }
    }





    private static void RequestHistory(Connection connection, DataInputStream in, DataOutputStream out, String username) throws SQLException {


        PreparedStatement user = null;
        PreparedStatement requests = null;
        try {
            user = connection.prepareStatement("SELECT * FROM users WHERE username=?");
            user.setString(1, username);
            ResultSet backf = user.executeQuery();

            if (backf.next()) {
                String lender_id = backf.getString("user_id");
                requests = connection.prepareStatement("SELECT * FROM requests WHERE borrower_id=?");
                requests.setString(1, lender_id);
                ResultSet req_list = requests.executeQuery();
                ArrayList<String> listId = new ArrayList<>();
                ArrayList<String> status = new ArrayList<>();
                int x = 0;
                while (req_list.next()) {
                    String book_id = req_list.getString("book_id");
                    PreparedStatement bbb = connection.prepareStatement("SELECT * FROM books WHERE book_id=?");
                    bbb.setString(1, book_id);
                    ResultSet res = bbb.executeQuery();
                    if (res.next()) {
                        String title = res.getString("title");
                        String stat = req_list.getString("status");
                        listId.add(title);
                        status.add(stat);
                        x++;
                    }
                    bbb.close();
                }
                out.writeInt(x);
                for (int i = 0; i < listId.size(); i++) {
                    out.writeUTF(listId.get(i));
                    out.writeUTF(status.get(i));
                }
            }
        } catch (SQLException | IOException e) {
        } finally {
            if (user != null) {
                user.close();
            }
            if (requests != null) {
                requests.close();
            }
        }
    }


    private static void adminbookStore(Connection connection, DataInputStream in, DataOutputStream out, String username) throws SQLException {
        try {
            label:
            while (true) {
                out.writeUTF("1-current borrowed books \n" +
                        "2-accepted requests\n" +
                        "3-rejected Request\n" +
                        "4-pending Requests\n" +
                        "5-logout\n");
                String choice = in.readUTF();
                switch (choice) {
                    case "1":
                        PreparedStatement updateStmt = connection.prepareStatement("SELECT * FROM userbooks WHERE is_available=?");
                        updateStmt.setInt(1, 0);
                        ResultSet res = updateStmt.executeQuery();
                        ArrayList<String> book_id = new ArrayList<>();
                        while (res.next()) {
                            book_id.add(res.getString("book_id"));
                        }
                        out.writeInt(book_id.size());
                        for (String id : book_id) {
                            PreparedStatement getdata = connection.prepareStatement("SELECT * FROM books WHERE book_id=?");
                            getdata.setString(1, id);
                            ResultSet data = getdata.executeQuery();  // Use getdata instead of updateStmt here
                            if (data.next()) {
                                String title = data.getString("title");
                                out.writeUTF(title);
                            }
                            data.close();  // Close the ResultSet
                            getdata.close();  // Close the PreparedStatement
                        }
                        res.close();  // Close the ResultSet
                        updateStmt.close();  // Close the PreparedStatement
                        break;
                    case "2":
                        handleData(in, out, connection, "accept");
                        break;
                    case "3":
                        handleData(in, out, connection, "reject");
                        break;
                    case "4":
                        handleData(in, out, connection, "pending");
                        break;
                    case "5":
                        break label;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void handleData(DataInputStream in, DataOutputStream out, Connection connection, String st) throws IOException {
        PreparedStatement stm = null;
        PreparedStatement oo = null;
        PreparedStatement zz = null;
        PreparedStatement pp = null;
        ResultSet result = null;
        ResultSet mn = null;
        ResultSet xx = null;
        ResultSet cc = null;

        try {
            stm = connection.prepareStatement("SELECT * FROM requests WHERE status=?");
            stm.setString(1, st);
            result = stm.executeQuery();
            ArrayList<String> list_title=new ArrayList<>();
            ArrayList<String> list_lender=new ArrayList<>();
            ArrayList<String> list_borrow=new ArrayList<>();

            while (result.next()) {
                String borrowerId = result.getString("borrower_id");
                String lenderId = result.getString("lender_id");
                String bookId = result.getString("book_id");

                oo = connection.prepareStatement("SELECT * FROM books WHERE book_id=?");
                oo.setString(1, bookId);
                mn = oo.executeQuery();
                if (mn.next()) {
                    String title = mn.getString("title");

                    zz = connection.prepareStatement("SELECT * FROM users WHERE user_id=?");
                    zz.setString(1, lenderId);
                    xx = zz.executeQuery();
                    if (xx.next()) {
                        String lenderUsername = xx.getString("username");

                        pp = connection.prepareStatement("SELECT * FROM users WHERE user_id=?");
                        pp.setString(1, borrowerId);
                        cc = pp.executeQuery();
                        if (cc.next()) {
                            String borrowerUsername = cc.getString("username");
                            list_title.add(title);
                            list_lender.add(lenderUsername);
                            list_borrow.add(borrowerUsername);

                        }
                    }
                }
            }
            out.writeInt(list_title.size());
            for (int i=0;i<list_title.size();i++){
                out.writeUTF(list_title.get(i));
                out.writeUTF(list_borrow.get(i));
                out.writeUTF(list_lender.get(i));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
