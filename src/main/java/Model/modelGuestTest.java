package Model;

public class modelGuestTest {
    
    
    public static void main(String[] args){
        model m= new model();
        
        modelGuest guest= new modelGuest("Noam");
        m.initServerForGuest(5000);
        guest.createConnection(5000);
        
    }
    
}
