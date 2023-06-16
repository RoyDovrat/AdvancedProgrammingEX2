package com.example;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import Model.model;
import ViewModel.view_model;

public class App extends Application {
    public static boolean isHost;
    public static int port_num;
    public static int port_guest, numOfPlayersChosen;
    private static Scene scene;
    public static String nowPlaying;
    @Override
    public void start(Stage stage) throws IOException{
        scene = new Scene(loadFXML("gameEntry"), 900, 640);
        stage.setScene(scene);
        stage.show(); 
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    static void setIsHost(boolean bool) throws IOException {
        isHost = bool;
    }

    static void setPortNumber(int port) throws IOException {
        port_num = port;
    }


    public static void main(String[] args) {
        launch();
    }
}