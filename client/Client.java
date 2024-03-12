import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        if (args.length == 1 && args[0].equals("list")) {
            listFiles();
        } else if (args.length == 2 && args[0].equals("put")) {
            String fileName = args[1];
            sendFile(fileName);
        } else {
            System.out.println("Usage: java Client <command>");
            System.out.println("Where <command> is list or put <fname>");
        }
    }

    private static void listFiles() throws IOException {
        try (Socket socket = new Socket("localhost", 9257);
             PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            pr.println("list");
            pr.flush();
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Error listing files: " + e.getMessage());
        }
    }

    private static void sendFile(String fileName) throws IOException {
        try (Socket socket = new Socket("localhost", 9257);
             PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            pr.println("put " + fileName);
            String line;
            while ((line = fileReader.readLine()) != null) {
                pr.println(line);
            }
            pr.println();
            pr.flush();
            String response = in.readLine();
            System.out.println("Server: " + response);
        } catch (IOException e) {
            System.out.println("Error sending file: " + e.getMessage());
        }
    }
}