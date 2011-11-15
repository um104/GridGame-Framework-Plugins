package mahjongg;
import gridgame.*;


/**
 * Write a description of class MahjonggCell here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MahjonggCell implements Renderable
{
    private final int rank;
    private final Suits suit;
    
    /**
     * Constructor for objects of class mahjonggCell
     */
    public MahjonggCell(Suits suit, int rank)
    {
        this.suit = suit;
        this.rank = rank;
    }

    public RenderDescriptor getRenderDescriptor()
    {
        RenderDescriptor desc = new RenderDescriptor();
        
        if(rank == 0)
        {
            desc.isImage = false;
            desc.text = "";
        }
        else
        {
            desc.isImage = true;
            desc.text = suit.toString() + rank;
        }
        
        return desc;
    }
    
    public Suits getSuit()
    {
        return suit;
    }
    
    public int getRank()
    {
        return rank;
    }
    
    public boolean equals(Object o)
    {
        if(o == this)
        {   return true;    }
        if(!(o instanceof MahjonggCell))
        {   return false;   }
        MahjonggCell var = (MahjonggCell) o;
        
        if(var.getSuit() == suit && var.getRank() == rank)
        {   return true;    }
        
        return false;
    }
    
    public String toString()
    {
        String retval = "";
        if(suit == Suits.B)
        {   retval += "Bamboo "; }
        else if(suit == Suits.C)
        {   retval += "Character ";  }
        else if(suit == Suits.D)
        {   retval += "Dots ";   }
        
        retval += rank;
        return retval;
    }
}
