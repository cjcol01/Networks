// imports
import java.net.*;
import java.io.*;

/**
 * Client program that interacts with the file transfer server.
 *
 * @author CJ Coleman
 *
 * This program allows the user to send requests to the server to list files or transfer a file.
 * The client supports two commands:
 * - "list": Requests the server to list all the files in the server directory.
 * - "put <filename>": Sends a file to the server to be saved in the server directory.
 */


public class Client {
    public static void main(String[] args) {
        // tests arg lengths to fit constraints
        if (args.length == 1 && args[0].equals("list")) {
            try {
                listFiles();
            } catch (IOException e) {
                System.err.println("Error listing files: " + e.getMessage()); 
            }
        }
        else if (args.length == 2 && args[0].equals("put")) {
            String fileName = args[1];
            try {
                sendFile(fileName);
            } catch (IOException e) {
                System.err.println("Error sending file: " + e.getMessage()); 
            }
        }
        else {
            printUsage(); 
        }
    }

    // prints the usage instructions for the client
    private static void printUsage() { 
        System.out.println("Usage: java Client <command>");
        System.out.println("Where <command> is:");
        System.out.println("  list       - Lists all the files in the server directory");
        System.out.println("  put <fname> - Sends a file to the server");
    }

    // requests the server to list all the files in the server directory
    private static void listFiles() throws IOException {
        try (Socket socket = new Socket("localhost", 9257);
             PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the "list" command to the server
            pr.println("list");
            pr.flush();

            // Read and print the server's response
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException e) {
            throw new IOException("Error communicating with the server", e);
        }
    }

    // sends a file to the server to be saved in the server directory
    private static void sendFile(String fileName) throws IOException {
        try (Socket socket = new Socket("localhost", 9257);
             PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
                
            // Send the "put" command along with the file name to the server
            pr.println("put " + fileName);

            // Read the file contents and send them to the server
            String line;
            while ((line = fileReader.readLine()) != null) {
                pr.println(line);
            }
            pr.println();
            pr.flush();

            // Read and print the server's response
            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found: " + fileName);
        } catch (IOException e) {
            throw new IOException("Error communicating with the server", e);
        }
    }
}