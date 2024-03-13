/**
 * a server that handles file transfer and listing requests from clients.
 *
 * @author CJ Coleman
 *
 * This program creates a server socket on port 9257 and listens for client connections.
 * It uses a fixed thread pool to handle up to 20 client request concurrently.
 * The server currently supports two commands:
 * - "list": Lists all the files in the server directory.
 * - "put <filename>": Receives a file from the client and saves it in the server directory.
 * The server then logs each valid client request to a log file named "log.txt".
 */



// imports 
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    // constant definitions
    private static final String SERVER_FILES_DIR = "serverFiles";
    private static final String LOG_FILE = "log.txt";
    private static final int THREAD_POOL_SIZE = 20;

    private static ExecutorService executorService;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        final int PORT = 9257;

        try {
            // creates a new ServerSocket on port 9257
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Listening on port " + PORT);

            // creates the serverFiles directory if it doesn't exist
            createServerFilesDirectory();

            // creates the log file
            createLogFile();

            // creates a fixed thread-pool with 20 connections
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            // adds a shutdown hook to shut down the server properly
            Runtime.getRuntime().addShutdownHook(new Thread(Server::shutdown));

            // infinite loop to accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                executorService.submit(() -> handleClient(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    // creates the serverFiles directory if it doesn't exist
    private static void createServerFilesDirectory() { 
        File serverFilesDir = new File(SERVER_FILES_DIR);
        if (!serverFilesDir.exists()) {
            boolean created = serverFilesDir.mkdir(); 
            if (!created) {
                System.err.println("Failed to create serverFiles directory.");
            }
        }
    }

    // creates the log file
    private static void createLogFile() {
        File logFile = new File(LOG_FILE);
        if (logFile.exists()) {
            boolean deleted = logFile.delete();
            if (!deleted) {
                System.err.println("Failed to delete existing log file.");
            }
        }
        try {
            boolean created = logFile.createNewFile();
            if (!created) { 
                System.err.println("Failed to create log file.");
            }
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }
    }

    // shuts down the server gracefully
    private static void shutdown() {
        System.out.println("Shutting down the server...");

        try {
            // closes the server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket closed.");
            }

            // shuts down the executor service
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
                System.out.println("Executor service shut down.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during server shutdown: " + e.getMessage());
        }

        System.out.println("Server shutdown complete.");
    }

    // handles commands given by the client
    private static void handleClient(Socket clientSocket) {
        // attempts to create a reader and writer
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String command = in.readLine();

            // handles the "list" command
            if (command.equals("list")) {
                listFiles(out);
                logRequest(clientSocket.getInetAddress().getHostAddress(), "list");
            }
            // handles the "put" command
            else if (command.startsWith("put ")) {
                String fileName = command.substring(4);
                receiveFile(fileName, in, out);
                logRequest(clientSocket.getInetAddress().getHostAddress(), "put");
            }
            else {
                out.println("Invalid command: " + command); // CHANGED
            }

            System.out.println("Client disconnected: " + clientSocket);
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }

    // handles the "list" command
    private static void listFiles(PrintWriter out) {
        File serverFilesDir = new File(SERVER_FILES_DIR);
        String[] files = serverFilesDir.list();

        // if there are files, print them to the command line
        if (files != null && files.length > 0) { // CHANGED
            for (String file : files) {
                out.println(file);
            }
        } else {
            out.println("No files found in the server directory."); // CHANGED
        }
    }

    // allows files to be sent from client to server
    private static void receiveFile(String fileName, BufferedReader in, PrintWriter out) throws IOException {
        File serverFilesDir = new File(SERVER_FILES_DIR);
        File file = new File(serverFilesDir, fileName);

        // checks for a file with the same name on the server
        if (file.exists()) {
            out.println("Error: File with the name '" + fileName + "' already exists on the server."); // CHANGED
        }
        else {
            // sends the file line by line
            try (FileWriter writer = new FileWriter(file)) {
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    writer.write(line + "\n");
                }
                out.println("File '" + fileName + "' received successfully."); // CHANGED
            } catch (IOException e) {
                out.println("Error receiving file '" + fileName + "': " + e.getMessage()); // CHANGED
            }
        }
    }

    // creates and writes to a log file
    private static void logRequest(String clientIP, String request) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {

            // sets and formats datetime
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss");

            // creates entry to log file, then writes to it using writer
            String logEntry = String.format("%s|%s|%s%n", now.format(formatter), clientIP, request);
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Error logging request: " + e.getMessage());
        }
    }
}