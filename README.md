# File Transfer Client-Server Application

This is a Java-based client-server application that allows file transfer and file listing functionality. The server hosts files and provides functionality to list and receive files from clients. The client can send files to the server and request a list of files hosted on the server.

## Features

- Client can send files to the server using the `put` command
- Client can request a list of files hosted on the server using the `list` command
- Server stores received files in a dedicated directory (`serverFiles`)
- Server handles multiple client connections concurrently using a thread pool
- Server logs client requests to a log file (`log.txt`)
- Server gracefully shuts down using a shutdown hook

## Prerequisites

- Java Development Kit (JDK) installed on both the client and server machines

## Usage

### Server

1. Compile the server code using the following command:
  ```javac Server.java```
2. Start the server using the following command:
  ```java Server```
The server will start running and listening for client connections on port 9257.


### Client
1. Compile the client code using the following command:
   ```javac Client.java```
2. Run the client using the following command:
  ```java Client <command>```

Replace ```<command>``` with one of the following:
  - ```list```: Lists all the files hosted on the server.
  - ```put <fname>```: Sends the file specified by <fname> to the server.

## Logging
- The server logs each valid client request to the ```log.txt``` file.
- The log entries include the date, time, client IP address, and the requested command (```list``` or ```put```).

## Concurrency
- The server uses a fixed thread pool with 20 connections to handle multiple client requests concurrently.
- Each client connection is assigned to a worker thread from the thread pool for processing.

## Authors

- CJ Coleman

## Version

- 1.0

## Date

- 13-04-24


