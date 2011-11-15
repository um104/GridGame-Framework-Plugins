package roundup;
import gridgame.*;
import java.util.*;


/**
 * Write a description of class RoundupBoard here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoundupBoard extends GridBoard<RoundupCell>
{
    String[] boards = {
                      "",
          /* 1 */     "11 15 32R 34 51 55",
                      "22R 14 31 42 44 55",
                      "11 21 31R 51 15 45",
                      "11 22R 31 35 51 54",
          /* 5 */     "11 23 25 31 41R 44",
                      "21 22 13R 33 42 43",
                      "11 14R 31 33 34 44",
                      "11 13R 15 21 45 51",
                      "11 15 21 33 41R 44",
          /* 10 */    "11 13 21 25R 31 54",
                      "13 15 25R 31 44 52",
                      "11 15 21 23R 45 51",
                      "11 13 15 31 51 55R",
                      "11R 15 41 44 53 54",
          /* 15 */    "13 15 21R 25 52 55",
                      "11R 25 41 51 54 55",
                      "14 21R 34 41 45 52",
                      "11 15 21 43R 45 51",
                      };

    public RoundupBoard()
    {   
        grid = new RoundupCell[7][7];
        
        setGame(1);
    }
    
    public void setGame(int num)
    {
       if(num <= 0 || num > 18)
        {
            return;
        }
        
        for(int row = 0; row < 7; row++)
        {
            for(int col = 0; col < 7; col++)
            {
                grid[row][col] = new RoundupCell(Imgs.empty);
            }
        }
        
        Scanner scan = new Scanner(boards[num]);
        while(scan.hasNext())
        {
            String cell = scan.next();
            int row = Integer.parseInt("" + cell.charAt(0));
            int col = Integer.parseInt("" + cell.charAt(1));
            if(cell.length() == 3)
            {
                grid[row][col] = new RoundupCell(Imgs.redup);
            }
            else
            {
                grid[row][col] = new RoundupCell(Imgs.greenup);
            }
        }
    }
}
