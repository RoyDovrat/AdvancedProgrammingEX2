package Model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import Server.ClientHandler;

public class modelGuestHandler implements ClientHandler {
    PrintWriter out;
	Scanner in;	
    modelHost host;
    int numOfPlayers;
    String nowPlayingName;

    public void setHost(modelHost host){ //figure out
        this.host=host;
    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {

        out=new PrintWriter(outToClient);
		in=new Scanner(inFromclient);
        
        while (in.hasNext()) {//read from guest
            String input = in.nextLine();
            //System.out.println("model client handler, input= "+ input);
            String [] args= input.split(",");
            this.nowPlayingName = args[0];
             if (args[1].equals("submitName")){
                host.setPlayerName(this.nowPlayingName);
                String str = this.nowPlayingName + ",wait" ;
                out.println(str);
            }
            if (args[1].equals("updateBoard")){
                System.out.println("updateBoard");
                char[][] arr = host.getBoardChars();  //check if returns current board
                char[] oneDArray = new char[15 * 15];
                System.out.println("oneDArray is: "+oneDArray.toString());
                int index = 0; // Index of current position in the 1D array
                System.out.println("index:");
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 15; j++) {
                        oneDArray[index] = arr[i][j];
                        index++;
                    }
                }
                String str = String.valueOf(oneDArray)+","+host.CurrentPlayer();//send board+next player name
                System.out.println("board str is: "+str);
                //setResponse(str);
                out.println(str);
            }
            if (args[1].equals("updateScores")){
                System.out.println("guest handler-updateScores");
                int[] scores= host.getScores(); 
                Integer[] integerArray = new Integer[scores.length];
                for (int i = 0; i < scores.length; i++) {
                    integerArray[i] = Integer.valueOf(scores[i]);
                }
                String str = Arrays.toString(integerArray);
                System.out.println("guest handler"+str);
                out.println(str);
            }
            if (args[1].equals("SkipTurn")){
                System.out.println("guest handler-mSkipTurn");
                host.tryAgain=false;
                host.nextPlayerTurn();
            }
            if (args[1].equals("joingame")){
                System.out.println("guest handler-joingame");
                String str =  this.nowPlayingName + ",enough,"+ host.mCheckIfEnoughPlayers();
                System.out.println("guest handler"+str);
                out.println(str);
            }
            if (args[1].equals("getTiles")){
                System.out.println("guest handler-getTiles");
                String str = host.restartLetterTiles(this.nowPlayingName); 
                System.out.println("guest handler"+str);
                out.println(str);
            }
            if (args[1].equals("requestCurrentPlayer")){
                System.out.println("requestCurrentPlayer");
                String str = host.getCurrentPlayer(); 
                System.out.println("guest handler"+str);
                out.println(str);
            }
            if (args[1].equals("playersNames")){
                System.out.println("requestCurrentPlayer");
                String str = host.getPlayersName(); 
                System.out.println("guest handler"+str);
                out.println(str);
            }
            if (args[1].equals("getCurrentBoard")){
                System.out.println("requestCurrentPlayer");
                char[][] arr = host.getBoardChars();  //check if returns current board
                char[] oneDArray = new char[15 * 15];
                System.out.println("oneDArray is: "+oneDArray);
                int index = 0; // Index of current position in the 1D array
                System.out.println("index:");
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 15; j++) {
                        oneDArray[index] = arr[i][j];
                        index++;
                    }
                }
                String str = String.valueOf(oneDArray)+","+host.CurrentPlayer();//send board+next player name
                System.out.println("board str is: "+str);
                //setResponse(str);
                out.println(str);
            }
            if (args[1].equals("wordsub")){
                System.out.println("host: wordsub was recieved to host from guest");
                char[][] arr = host.mSubmitWord(this.nowPlayingName, args[4]/*board*/, Integer.valueOf(args[2])/*mouseRow*/,Integer.valueOf(args[3])/*mouseCol*/); 
                char[] oneDArray = new char[15 * 15];
                System.out.println("oneDArray is: "+oneDArray);
                int index = 0; // Index of current position in the 1D array
                System.out.println("index:");
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 15; j++) {
                        oneDArray[index] = arr[i][j];
                        index++;
                    }
                }
                String str = String.valueOf(oneDArray)+","+host.CurrentPlayer();//send board+next player name
                System.out.println("board str is: "+str);
                //setResponse(str);
                out.println(str);
                //send next player's turn
            }
            if (args[1].equals("FillTiles")){
                String str = host.fillLetterTilesFromBag(this.nowPlayingName); 
                out.println(str);
            }

            out.flush();
        }  
    
    }

    private void sleep(int i) {
    }

    @Override
    public void close() {
   
    }


}
