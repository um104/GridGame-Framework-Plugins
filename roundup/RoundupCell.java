package roundup;
import gridgame.*;

/**
 * Write a description of class RoundupCell here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoundupCell implements Renderable
{
    private Imgs type;
    public boolean isRed = false;
    
    /**
     * Constructor for objects of class RoundupCell
     */
    public RoundupCell(Imgs type)
    {
        this.type = type;
        if (this.type.toString().contains("red"))
        {
            isRed = true;
        }
    }

    public RenderDescriptor getRenderDescriptor()
    {
        RenderDescriptor desc = new RenderDescriptor();
        
        desc.isImage = true;
        desc.text = type.toString();
        
        return desc;
    }
    
    public Imgs getType()
    {
        return this.type;
    }
    
    public void setType(Imgs type)
    {
        this.type = type;
        if (type.toString().contains("red"))
        {
            isRed = true;
        }
        else
        {
            isRed = false;   
        }
    }
}
