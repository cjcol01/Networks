public class Client 
{
	public static void main( String[] args )
	{
		if(args.length == 1 && args[0].equals("list")){
			System.out.println("server files: ");
		}
		else if (args.length == 2 && args[0].equals("put")){
			System.out.println(String.format("file '%s' put on server", args[1]));
		}
		else{
			System.out.println("Usage: java Client <command>");
			System.out.println("Where <command> is list or put <fname>");
		}
	}
}