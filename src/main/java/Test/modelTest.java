package Test;

import java.util.Scanner;

import Model.modelGuest;
import Model.modelGuestHandler;
import Model.modelHost;

public class modelTest {
    static modelHost mHost=new modelHost(5000, new modelGuestHandler(), 2, true);
    static modelGuest mGuest;
    
    public static boolean setPlayerName_Test(){
        mHost.setPlayerName("Revital");
        mGuest.setPlayerName("Roy");
        if (mHost.players.contains("Revital") & mHost.players.contains("Roy")){
            return true;
        }
        return false;
    }
    
    public static boolean testServer(){
        boolean ok=true;
        mHost.start();
        try{
            mGuest= new modelGuest(5000);
        }catch(Exception e) {
			System.out.println("some exception was thrown while testing your server, cannot continue the test (-100)");			
			ok=false;
		}
        if(setPlayerName_Test()){
            System.out.println("setPlayerName function works!");
        }
        mHost.close(); //chenged 
        mGuest.close();

        
        return ok; 
    }
    
    public static boolean mRequestRestartLetterTiles_test(){
        char[] tiles=mHost.mRequestRestartLetterTiles("Revital");
        System.out.println("Revital's Tiles:");
        for(int i=0; i<tiles.length; i++){
            System.out.print(tiles[i]);
            if(i!=6){
                System.out.print(", ");
            }
        }
        System.out.println("");

        if(tiles.length!=7){
            return false;
        }
        
        tiles=mGuest.mRequestFillLetterTiles("Roy");
        System.out.println("Roy's Tiles:");
        for(int i=0; i<tiles.length; i++){
            System.out.print(tiles[i]);
            if(i!=6){
                System.out.print(", ");
            }
        }
        System.out.println("");

        if(tiles.length!=7){
            return false;
        }
        return true;
    }
    

    public static void testConnectionToWordServer() throws InterruptedException{
        
        if(!mHost.checkWordInServer("Revital")){
            System.out.println("right 1- 'revital'! word not in dictionarys");
        }
        Thread.sleep(2000);
        if(mHost.checkWordInServer("no")){
            System.out.println("right 2- 'no'! word in dictionarys");
        }
        Thread.sleep(2000);
        /* 
        if(mHost.checkWordInServer("what")){
            System.out.println("right 3 what! word in dictionarys");
        }
        Thread.sleep(2000);
        if(!mHost.checkWordInServer("bsdfbsmdfb")){
            System.out.println("right 4 bsdfbsmdfb! word not in dictionarys");
        }
        Thread.sleep(2000);
        */   
    }

    public static void checkWord_Test(){
        Scanner scanner = new Scanner(System.in);
        boolean inTiles=false;
        System.out.print("Enter word using the tiles you are given (OFin Capital letters):");
        while(!inTiles){
            String word = scanner.nextLine();
            inTiles= mHost.CheckWordInTiles("Revital", word);
            if(!inTiles){
                System.out.println("Try again, not in tiles");
            }
            System.out.println("Great! word in tiles");
        }
        scanner.close();
    }

    public static void SubmitWord_Test(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter word using the tiles you are given (Revital's), pay attantion to capital letters:");
        char[][] board;
        boolean validWord=false;
        while(!validWord){
            String word = scanner.nextLine();
            board= mHost.mSubmitWord("Revital", word, 7, 6);
            if (!mHost.tryAgain){
                validWord=true;
                System.out.println("Word is correct");
            }   
        }
        System.out.print("Enter word using the tiles you are given (Roy's), pay attantion to capital letters:");
        validWord=false;
        while(!validWord){
            String word = scanner.nextLine();
            board= mHost.mSubmitWord("Roy", word, 2, 2);
            if (!mHost.tryAgain){
                validWord=true;
                System.out.println("Word is correct");
            }   
        }
        scanner.close();
    }
    
    public static void RequestFillLetterTiles_Test(){
        char[] tiles= mHost.mRequestFillLetterTiles("Revital");
        System.out.println("Rrevital's new tiles after filling: ");
        for(int i=0; i<tiles.length; i++){
            System.out.print(tiles[i]);
            if(i!=6){
                System.out.print(", ");
            }
        }
        System.out.println("");

        tiles= mHost.mRequestFillLetterTiles("Roy");
        System.out.println("Roy's new tiles after filling: ");
        for(int i=0; i<tiles.length; i++){
            System.out.print(tiles[i]);
            if(i!=6){
                System.out.print(", ");
            }
        }
        System.out.println("");

    }
    
    public static void getScores_Test(){
        int[] scores=mHost.getScores();
        System.out.println("Revital's score:"+scores[0]);
        System.out.println("Revital's score:"+scores[1]);
    }

    public static void main(String[] args) throws InterruptedException{
        mHost.start();
        try{
            mGuest= new modelGuest(5000);
        }catch(Exception e) {
            System.out.println("some exception was thrown while testing your server, cannot continue the test (-100)");			
        }
        if(setPlayerName_Test()){
            System.out.println("setPlayerName function works!");
        }
        System.out.println("\n");
        System.out.println("Server Working");
        System.out.println("\n");
        testConnectionToWordServer();
        if(mRequestRestartLetterTiles_test()){
            System.out.println("Get new Tiles from bag for each player - works!");
        }
        System.out.println();
        System.out.println("Score before entering word");
        getScores_Test();
        System.out.println();
        SubmitWord_Test();
        System.out.println();
        System.out.println("Score after entering word");
        getScores_Test();
        System.out.println();
        RequestFillLetterTiles_Test();
        mHost.close(); //chenged 
        mGuest.close();
    }
}
