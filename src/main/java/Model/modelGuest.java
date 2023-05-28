package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;



public class modelGuest extends Observable implements interfaceModel {
    PrintWriter out;
    Scanner in;
    String playerName;
    Socket server;
    String line = "";
    Boolean flag=false;
    public modelGuest(String name){
        this.playerName=name;
    }
    public void createConnection(int port){
        try {
            port=5000;
			server=new Socket("localhost",port);
			out=new PrintWriter(server.getOutputStream());
			in=new Scanner(server.getInputStream());

            
			out.println(playerName);
			out.flush();
			String res=in.nextLine(); //get server-welcome
            System.out.println("model guest input"+ res);
			//in.close();
			//out.close();
			//server.close();
            
            System.out.println("in model guest line1 "+line);
		} catch (IOException e) {
			System.out.println("your code ran into an IOException (-10)");
		}
         
 
         // keep reading until "Over" is input
         Runnable r= ()->{
            while (!line.equals("over")) {
                System.out.println("1");
                //out.writeUTF(line);
            }
        };
        new Thread(r).start();

    
        System.out.println("in model guest line "+line);
            
        //}
        try {
            in.close();
            out.close();
            server.close();
        }
        catch (IOException i) {
            System.out.println(i);
        }
    }
    @Override
    public char[] restartLetterTiles() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'restartLetterTiles'");
    }

    @Override
    public char[] fillLetterTilesFromBag() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fillLetterTilesFromBag'");
    }
    
}
