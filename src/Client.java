import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);

        try {
            InetAddress ip = InetAddress.getLocalHost();

            Socket socket = new Socket(ip, 1234);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());



            int choise=0;
            while (true){
                String str = in.readUTF();
                System.out.println(str);
                choise=scanner.nextInt();
                if (choise==1){
                  String role= Login(in, out);
                    if (role.equals("user")){
                        Website(in,out);

                    }else {
                        adminbookStore(in,out);
                    }






                    break;

                }else if (choise==2){
                    Registration(in, out);

                }else {
                    System.out.println("Invalid Input.");
                }
            }

            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private static void adminbookStore(DataInputStream in, DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println(in.readUTF());
            String choice=scanner.nextLine();
            out.writeUTF(choice);
            switch (choice) {
                case "1":
                    int size=in.readInt();
                    if (size>0){
                        for (int i=0;i<size;i++){
                            System.out.println("----------------------");
                            System.out.println((i+1)+"- "+in.readUTF());
                            System.out.println("-----------------------");
                        }
                    }else {
                        System.out.println("no borrowed books");
                    }


                    break;
                case "2":
                    int s= in.readInt();
                    System.out.println("List of accepted requests");
                    for (int i=0;i<s;i++){
                        System.out.println("---------------------");
                        System.out.println("Book name: "+in.readUTF());
                        System.out.println("borrower name: " +in.readUTF()+
                                "   ,   lender name: "+in.readUTF());

                        System.out.println("---------------------");

                    }




                    break;
                case "3":
                    int k= in.readInt();
                    System.out.println("List of rejected requests");
                    for (int i=0;i<k;i++){
                        System.out.println("---------------------");
                        System.out.println("Book name: "+in.readUTF());
                        System.out.println("borrower name: " +in.readUTF()+
                                "   ,   lender name: "+in.readUTF());

                        System.out.println("---------------------");

                    }

                    break;
                case "4":
                    int r= in.readInt();
                    System.out.println("List of pending requests");
                    for (int i=0;i<r;i++){
                        System.out.println("---------------------");
                        System.out.println("Book name: "+in.readUTF());
                        System.out.println("borrower name: " +in.readUTF()+
                                "   ,   lender name: "+in.readUTF());

                        System.out.println("---------------------");

                    }
                    break;
                case "5":
                    System.out.println("good bye :)");

                    break;
                default:
                    System.out.println("Invalid Input , Try again");
            }
        }




    }
    private static void Registration(DataInputStream in, DataOutputStream out) throws IOException {
        Scanner scanner=new Scanner(System.in);

        out.writeUTF("2");
        while (true){
            String usernamePrompt = in.readUTF();
            System.out.println(usernamePrompt);
            out.writeUTF(scanner.nextLine());
            String namePrompt = in.readUTF();
            if (namePrompt.equals("404")){

                System.out.println("Username already taken. ");


            }else {

                System.out.println(namePrompt);
                out.writeUTF(scanner.nextLine());
                String passwordPrompt = in.readUTF();
                System.out.println(passwordPrompt);
                out.writeUTF(scanner.nextLine());
                String response = in.readUTF();
                System.out.println(response);
                break;

            }

        }





    }

    private static String Login(DataInputStream in, DataOutputStream out) throws IOException {
        Scanner scanner=new Scanner(System.in);

        out.writeUTF("1");
        while (true){
            String usernamePrompt = in.readUTF();
            System.out.println(usernamePrompt);


            out.writeUTF(scanner.nextLine());

            String passwordPrompt = in.readUTF();
            if(passwordPrompt.equals("404")){
                System.out.println("username doesn't exist");
            }else {
                System.out.println(passwordPrompt);

                out.writeUTF(scanner.nextLine());
                String response = in.readUTF();
                if (response.equals("401")){
                    System.out.println("wrong password");
                }else {
                    System.out.println(response);
                    return in.readUTF();


                }


            }

        }

    }

    private static void Website(DataInputStream in, DataOutputStream out) throws IOException, SQLException {
        Scanner scanner=new Scanner(System.in);

        label:
        while (true) {
            String str= in.readUTF();
            System.out.println(str);
            String choice=scanner.nextLine();
            out.writeUTF(choice);
            switch (choice) {
                case "1":

                    Search(in, out);

                    break;
                case "2":

                    AddBook(in, out);

                    break;
                case "3":

                RemoveBook(in,out);

                    break;
                case "4":
                SubmitRequest(in,out);
                    break;
                case "5":
                    HandleRequest(in,out);

                    break;
                case "6":
                    RequestHistory(in,out);
                    break;
                case "7":
                    System.out.println("good bye :)");
                    break ;
                default:
                    System.out.println("Invalid Input , Try again");
            }
        }


    }

    private static void Search(DataInputStream in, DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1-Display all books \n" +
                "2-search for specific book\n");
        String choise =scanner.nextLine();
        out.writeUTF(choise);
        if (choise.equals("1")){
            int size=in.readInt();
            for (int i=0;i<size;i++ ){
                System.out.println(in.readUTF());
                System.out.println(in.readUTF());
                System.out.println(in.readUTF());

            }
        }else if (choise.equals("2")){
            System.out.println(in.readUTF());
            out.writeUTF(scanner.nextLine());
            System.out.println(in.readUTF());
            out.writeUTF(scanner.nextLine());
            int size=in.readInt();
            for (int i=0;i<size;i++ ){
                System.out.println(in.readUTF());
                System.out.println(in.readUTF());
                System.out.println(in.readUTF());

            }
        }



    }


    private static void AddBook(DataInputStream in, DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                String str = in.readUTF();
                System.out.println(str);
                out.writeUTF(scanner.nextLine());

                str = in.readUTF();
                System.out.println(str);
                out.writeUTF(scanner.nextLine());

                str = in.readUTF();
                System.out.println(str);
                out.writeUTF(scanner.nextLine());

                str = in.readUTF();
                System.out.println(str);
                out.writeUTF(scanner.nextLine());

                String result = in.readUTF();

                if (result.equals("Book Added")) {
                    System.out.println(result);
                    break;
                } else {
                    System.out.println(result);
                    System.out.println("Try again? Enter 1 for YES, anything else for NO");
                    int choice = scanner.nextInt();

                    if (choice != 1) {
                        break;
                    }
                }
            } catch (Exception e) {

            }
        }

    }


    private static void RemoveBook(DataInputStream in, DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println(in.readUTF());
            out.writeUTF(scanner.nextLine());
            System.out.println(in.readUTF());

        } catch (Exception e) {

        }

    }
    private static void SubmitRequest(DataInputStream in, DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("Book name : ");
            out.writeUTF(scanner.nextLine());
            String res=in.readUTF();
            if (res.equals("1")){
                System.out.println("Request Submitted");
                break;
            }else {
                System.out.println("Not found.");
            }
        }






    }
    private static void HandleRequest(DataInputStream in, DataOutputStream out) throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        try {
            int size= in.readInt();
            System.out.println(size);
            for (int i=0;i<size;i++){
                System.out.println("-------------------");
                System.out.println((i+1)+"- Request for "+in.readUTF()+" book");
                System.out.println("Status :" + in.readUTF());
                System.out.println("--------------------");

            }
            System.out.println("Enter Request number :");
            int c=scanner.nextInt();
            c=c-1;
            out.writeInt(c);
            System.out.println("1-Accept Request\n"+
                    "2-Reject Request");

            out.writeInt(scanner.nextInt());
            System.out.println(in.readUTF());




        }catch  (IOException e) {
            e.printStackTrace();
        }



    }

    private static void RequestHistory(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            int size = in.readInt();
            System.out.println(size);

            System.out.println("-------------------------------");
            if (size == 0) {
                System.out.println(in.readUTF());
            } else {
                for (int i = 0; i < size; i++) {
                    System.out.println("Request for Book: " + in.readUTF());
                    System.out.println("Status: " + in.readUTF());
                    System.out.println();
                }
            }
            System.out.println("-------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    }
