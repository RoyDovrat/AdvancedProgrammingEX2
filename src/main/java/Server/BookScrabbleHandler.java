package Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler{
	PrintWriter out;
	Scanner in;		
	@Override
	public void handleClient(InputStream inFromclient, OutputStream outToClient) {
		out=new PrintWriter(outToClient);
		in=new Scanner(inFromclient);
		String text = in.nextLine();
		boolean result=false;
		DictionaryManager dm=DictionaryManager.get();
		if (text.startsWith("Q,")) {
		    String[] filenames = text.substring(2).split(",");
		    result = dm.query(filenames);
		} else if (text.startsWith("C,")) {
		    String[] filenames = text.substring(2).split(",");
		    result = dm.challenge(filenames);
		}
		out.println(result);
		out.flush();
	}

	@Override
	public void close() {
		in.close();
		out.close();
		
	}
}
