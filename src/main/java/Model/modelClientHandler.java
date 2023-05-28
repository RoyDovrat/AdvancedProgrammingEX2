package Model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import Server.ClientHandler;

public class modelClientHandler implements ClientHandler {
    PrintWriter out;
	Scanner in;	
    model model;
    int numOfPlayers;
    public modelClientHandler(int numOfPlayers, model m){
        this.model=m;
        this.numOfPlayers=numOfPlayers;
    }
// in handle client 1. new to buffer reader (in scrabble habdler) in+ out
// new read lines that gets the login request from the player
// busy wait untill number of players is complete
// host sends guest a message of start game.
// init game in model- make order of player+init tiles
//loop that checks if game hasnt finished
//read request - run a methood and return result.
    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {

        out=new PrintWriter(outToClient);
		in=new Scanner(inFromclient);
		String loginRequest = in.nextLine();
        System.out.println("model client handler, input= "+ loginRequest);
        //add player with the name received
        out.println("welcome to the game");
        out.flush();
       // throw new UnsupportedOperationException("Unimplemented method 'handleClient'");
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'close'");
    }
    
}
