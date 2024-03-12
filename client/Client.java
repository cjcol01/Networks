import java.net.*;
import java.io.*;

public class Client 
{
	public static void main( String[] args ) throws IOException {
		// if(args.length == 1 && args[0].equals("list")){
		// 	System.out.println("server files: ");
		// }
		// else if (args.length == 2 && args[0].equals("put")){
		// 	System.out.println(String.format("file '%s' put on server", args[1]));
		// }
		// else{
		// 	System.out.println("Usage: java Client <command>");
		// 	System.out.println("Where <command> is list or put <fname>");
		// }
		
		Socket S = new Socket("localhost", 9257);

		PrintWriter pr = new PrintWriter(S.getOutputStream());
		pr.println("is it working");
		pr.flush();

		InputStreamReader in = new InputStreamReader(S.getInputStream());
		BufferedReader bf = new BufferedReader(in);

		String str = bf.readLine();
		System.out.println("server: " + str);
	}
}