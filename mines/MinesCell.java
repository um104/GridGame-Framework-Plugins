package mines;
import gridgame.*;

/**
 * Write a description of class MinesCell here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MinesCell implements Renderable
{
    private Piece type;
    public int bombNeighbors = 0;
    public boolean isBomb = false;
    
    public MinesCell(Piece cellType)
    {
        this.type = cellType;
    }

    public RenderDescriptor getRenderDescriptor()
    {
        RenderDescriptor desc = new RenderDescriptor();
        
        if(type == Piece.empty)
        {
            desc.isImage = false;
            desc.isInverse = true;
            desc.isStrong = true;
            desc.text = (bombNeighbors>0)?(Integer.toString(bombNeighbors)):("");
        }
        else
        {
            desc.isImage = true;
            desc.text = type.toString();
        }
        
        return desc;
    }
    
    public Piece getType()
    {
        return this.type;
    }
    
    public void setType(Piece type)
    {
        
        this.type = type;
    }
}
