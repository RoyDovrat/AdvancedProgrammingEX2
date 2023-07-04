package Model;

import com.mongodb.client.*;
import org.bson.Document;
import java.util.*;

import static java.lang.Thread.sleep;

public class database {

    public static String collectionName = "ap";


    // public DBcom() {
    // }

    public void saveToDB() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scra");
        MongoCollection<Document> collection = database.getCollection(collectionName);
        int server =1324; //TODO: take the server port of the guest not the host
        Document document = new Document("Game port" , server);
        //document.append("current_player" ,hm.current_player);
     

        collection.insertOne(document);
        mongoClient.close();
    }
}
