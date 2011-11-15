package mines;
import gridgame.*;


/**
 * Write a description of class MinesBoard here.
 * 
 * @author Mark Lerner 
 * @version Nov 8
 */
public class MinesBoard extends GridBoard<MinesCell>
{
    public MinesBoard()
    { 
        resetBoard();
    }
    
    public void resetBoard()
    {
        int size;
        if(parent == null || parent.getPreference("Board Size") == null)
            size = 8;
        else
            size = Integer.parseInt(parent.getPreference("Board Size"));
        
        grid = new MinesCell[size][size];
        
        for(int row = 0; row < size; row++)
        {
            for(int col = 0; col < size; col++)
            {
                grid[row][col] = new MinesCell(Piece.hidden);
            }
        }
    }
}
