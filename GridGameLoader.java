import gridgame.*;
import java.lang.reflect.*;

/**
 * Write a description of class GridGameLoader here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GridGameLoader
{
    public static void main(String[] args)
    {
        String gamePrefix = args[0];
        String pack = gamePrefix.toLowerCase();
        
        GridGame gridGame = null;     // The instance of the game
        GridBoard gridBoard = null;   // The instance of the board
        GridStatus gridStatus = null; // The instance of status
        
        // Create the game components                  
        
        // Use dynamic extension to instantiate the desired game class
        try {
            Class myGame = Class.forName(pack +"."+ gamePrefix + "Game");
            Class myBoard = Class.forName(pack +"."+ gamePrefix + "Board");
            Class myStatus = Class.forName(pack +"."+ gamePrefix + "Status");
            
            gridBoard = (GridBoard) myBoard.newInstance();
            gridStatus = (GridStatus) myStatus.newInstance();
            
            
            Constructor constructor = myGame.getConstructor(new Class[] {GridBoard.class, GridStatus.class});
            
            gridGame =(GridGame) constructor.newInstance(new Object[] {gridBoard, gridStatus});
        }
         catch (Throwable ex) {
            System.err.println(ex);
            ex.printStackTrace();
            System.exit(1);
        }
        
        gridBoard.setParent(gridGame);
        
        // setup the game
        gridGame.init();

        // Create the GUI 
        GridGUI frame = new GridGUI(gamePrefix, gridGame);
        frame.createUI();   
        
        // Link the model and view to each other
        gridGame.addObserver(frame);
       
        // Make the GUI visible and available for user interaction
        frame.setVisible(true);       
    }
}
