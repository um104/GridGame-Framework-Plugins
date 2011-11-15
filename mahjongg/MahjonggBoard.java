package mahjongg;

import gridgame.*;
import java.util.*;

/**
 * Write a description of class MahjonggBoard here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MahjonggBoard extends GridBoard<MahjonggCell>
{
   
    public MahjonggBoard()
    {
        resetBoard();
    }
    
    public void resetBoard()
    {
        int[] ranks = {1, 2, 3, 4, 5, 6, 7};
        ArrayList<MahjonggCell> cellList = new ArrayList<MahjonggCell>();
        
        for(int rank : ranks)
        {
            for(Suits suit : Suits.values())
            {
                cellList.add(new MahjonggCell(suit, rank));
                cellList.add(new MahjonggCell(suit, rank));
                cellList.add(new MahjonggCell(suit, rank));
                cellList.add(new MahjonggCell(suit, rank));
            }
        }
        
        Random generator;
        if(parent == null)
            generator = new Random(0);
        else
            generator = new Random(parent.getGame());
        Collections.shuffle(cellList, generator);
        
        grid = new MahjonggCell[8][12];
        int[] cols = {12, 8, 10, 12, 12, 10, 8, 12};
        int buffer = 0;
        
        for(int row = 0; row < 8; row++)
        {
            if(cols[row] < 12)
            {
                //find out how much less it is than twelve, then divide that by two. set that as the buffer.
                buffer = (12-cols[row])/2;
                //add a null value to myBoard for every buffer space
                for(int i=0;i<buffer;i++)
                {   grid[row][i] = new MahjonggCell(Suits.B, 0); }
            }
            for(int col = 0; col < cols[row]; col++)
            {
                grid[row][col + buffer] = cellList.remove(0);
            }
            for(int i=0;i<buffer;i++)
            {   grid[row][i + cols[row] + buffer] = new MahjonggCell(Suits.B, 0); }
            buffer = 0;
        }
    }
    
    public void setCell(int row, int col, MahjonggCell cell)
    {
        //check bounds
        if(row < 0 || row > 7 || col < 0 || col > 11)
        {
            return;
        }
        
        grid[row][col] = cell;
    }
}
