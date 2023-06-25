package Test;

import java.io.IOException;

import com.example.App;
import com.example.gameEntryController;
public class viewTest {

    //@Test
    public void testGameEntryController() throws IOException {
        System.out.println("here");
        gameEntryController controller = new gameEntryController();
        controller.HostChosen();
    }
    public void main(String[] args) throws IOException{
        System.out.println("here");
        App app=new App();
        testGameEntryController();
    }
    

}
