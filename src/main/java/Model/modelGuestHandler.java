package Model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import Server.ClientHandler;

public class modelGuestHandler implements ClientHandler {
    PrintWriter out;
	Scanner in;	
    modelHost host;
    int numOfPlayers;
    
// in handle client 1. new to buffer reader (in scrabble habdler) in+ out
// new read lines that gets the login request from the player
// busy wait untill number of players is complete
// host sends guest a message of start game.
// init game in model- make order of player+init tiles
//loop that checks if game hasnt finished
//read request - run a methood and return result.

    public void setHost(modelHost host){ //figure out
        this.host=host;
    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {

        out=new PrintWriter(outToClient);
		in=new Scanner(inFromclient);
        
        while (in.hasNext()) {
            String input = in.nextLine();
            System.out.println("model client handler, input= "+ input);
            String [] args= input.split(",");

            if (args[0].equals("joinGame")){
                String str = host.restartLetterTiles(); 
                out.println(str);
            }
            if (args[0].equals("wordsub")){
                System.out.println("host: wordsub was recieved to host from guest");
                char[][] arr = host.mSubmitWord(args[3]/*board*/, Integer.valueOf(args[1])/*mouseRow*/,Integer.valueOf(args[2])/*mouseCol*/); 
                char[] oneDArray = new char[15 * 15];
                System.out.println("oneDArray is: "+oneDArray);
                int index = 0; // Index of current position in the 1D array
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 15; j++) {
                        oneDArray[index++] = arr[i][j];
                    }
                }
                //String str = oneDArray.toString();
                String str = String.valueOf(oneDArray);
                System.out.println("board str is: "+str);
                //setResponse(str);
                out.println(str);
            }

            out.flush();
        }  
    
    }

    @Override
    public void close() {
   
    }
    
}
