import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class Server {
    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(1234);
            System.out.println("Server started.");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "Y.ashraf253");

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Client connected: " + clientSocket);
                ClientHandler clientHandle1=new ClientHandler(clientSocket);
                clientHandle1.start();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    }
