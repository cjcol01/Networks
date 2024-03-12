import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final String SERVER_FILES_DIR = "serverFiles";
    private static final int THREAD_POOL_SIZE = 20;

    public static void main(String[] args) {
        final int PORT = 9257;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);

            // Create the serverFiles directory if it doesn't exist
            File serverFilesDir = new File(SERVER_FILES_DIR);
            if (!serverFilesDir.exists()) {
                serverFilesDir.mkdir();
            }

            // Create a fixed thread-pool with 20 connections
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String command = in.readLine();
            if (command.equals("list")) {
                // Handle the "list" command
                listFiles(out);
            } else if (command.startsWith("put ")) {
                String fileName = command.substring(4);
                receiveFile(fileName, in, out);
            } else {
                out.println("Invalid command");
            }

            System.out.println("Client disconnected: " + clientSocket);
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }

    private static void listFiles(PrintWriter out) {
        File serverFilesDir = new File(SERVER_FILES_DIR);
        String[] files = serverFilesDir.list();
        if (files != null) {
            for (String file : files) {
                out.println(file);
            }
        }
    }

    private static void receiveFile(String fileName, BufferedReader in, PrintWriter out) throws IOException {
        File serverFilesDir = new File(SERVER_FILES_DIR);
        File file = new File(serverFilesDir, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                writer.write(line + "\n");
            }
            out.println("File received successfully");
        } catch (IOException e) {
            out.println("Error receiving file: " + e.getMessage());
        }
    }
}