import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.io.*;

public class Server 
{
	public static void main( String[] args ) throws IOException{
		ServerSocket ss = new ServerSocket(9257);
		Socket s = ss.accept();

		System.out.println("client connected");

		InputStreamReader in = new InputStreamReader(s.getInputStream());
		BufferedReader bf = new BufferedReader(in);

		String str = bf.readLine();
		System.out.println("client: " + str);

		PrintWriter pr = new PrintWriter(s.getOutputStream());
		pr.println("yes");
		pr.flush();
	}
}