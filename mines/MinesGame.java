package mines;
import gridgame.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * Write a description of class MinesGame here.
 * 
 * @author Mark Lerner
 * @version Nov 8
 */
public class MinesGame extends GridGame
{
    private MinesBoard board;
    private MinesStatus status;
    private TimerLabel timer;
    
    private int moves = 0;
    private int hiddenPieces = 0;
    private int flags = 0;
    private int numBombs = 0;
    private boolean isCheating = false;
    private int maxBombs = 0;
    
    public MinesGame(GridBoard board, GridStatus status)
    {
        this.board = (MinesBoard)board;
        this.status = (MinesStatus)status;
        setRandomGame();
    }
    
    public GridBoard getBoardToView()
    {
        return board;
    }
    
    public GridStatus getStatusToView()
    {
        return status;
    }
    
    public void init()
    {
        int size;
        if(getPreference("Board Size") == null)
            size = 8;
        else
            size = Integer.parseInt(getPreference("Board Size"));
            
        int difficulty;
        if(getPreference("Difficulty") == null)
            difficulty = 8;
        else
            difficulty = Integer.parseInt(getPreference("Difficulty"));
        
        //change board in relation to boardNumber
        
        moves = 0;
        flags = 0;
        hiddenPieces = size*size;
        numBombs = 0;
        isCheating = false;
        maxBombs = size*size / difficulty;
        
        Random generator = new Random(getGame());
        board.resetBoard();
        
        for(int i = 0; i < maxBombs; i++)
        {
            int someRow = generator.nextInt(size);
            int someCol = generator.nextInt(size);
            
            if(((MinesCell)board.getValueAt(someRow, someCol)).isBomb == true)
            {
                continue;
            }
            numBombs++;
            ((MinesCell)board.getValueAt(someRow, someCol)).isBomb = true;
            
            //Change the bombNeighbor number for every neighbor of the new bomb
            if(someRow>0)
            {
                if(someCol > 0)
                {
                    ((MinesCell)board.getValueAt(someRow-1, someCol-1)).bombNeighbors++;
                }
                ((MinesCell)board.getValueAt(someRow-1, someCol)).bombNeighbors++;
                if(someCol < size-1)
                {
                    ((MinesCell)board.getValueAt(someRow-1, someCol+1)).bombNeighbors++;
                }
            }
            if(someCol>0)
            {
                ((MinesCell)board.getValueAt(someRow, someCol-1)).bombNeighbors++;
            }
            if(someCol<size-1)
            {
                ((MinesCell)board.getValueAt(someRow, someCol+1)).bombNeighbors++;
            }
            if(someRow<size-1)
            {
                if(someCol > 0)
                {
                    ((MinesCell)board.getValueAt(someRow+1, someCol-1)).bombNeighbors++;
                }
                ((MinesCell)board.getValueAt(someRow+1, someCol)).bombNeighbors++;
                if(someCol < size-1)
                {
                    ((MinesCell)board.getValueAt(someRow+1, someCol+1)).bombNeighbors++;
                }
            }
        }
        
        
        if(timer == null)
            timer = new TimerLabel();
        else
            timer.restart();

        status.setAttributes(timer);
        status.add(timer);
        updateStatus();
        
        setChanged();
        notifyObservers(new Integer(getGame()));
    }
    
    
    public void restart()
    {
        init();
    }
    
    public void handleRightClick(int row, int col)
    {       
        MinesCell cell = (MinesCell)board.getValueAt(row,col);
        
        if(cell.getType() == Piece.flagged)
        {
            cell.setType(Piece.hidden);
            flags--;
        }
        else if(cell.getType() == Piece.hidden)
        {
            cell.setType(Piece.flagged);
            flags++;
        }
        updateStatus();
        
        setChanged();
        notifyObservers();
    }
    
    public void makeMove(int row, int col)
    {
        if(isCheating && hiddenPieces != -1)
        {
            try
            {
                winning();
            }
            catch(java.io.IOException e)
            {
                System.err.println("Save file isn't where it should be!");
            }
            return;
        }
        
        MinesCell cell = (MinesCell)board.getValueAt(row,col);
            
        moves++;
        updateStatus();
        if(cell.getType() == Piece.hidden && hiddenPieces != -1)
        {
            if(cell.isBomb)
            {
                defeat(cell);
            }
            else
            {       
                revealCell(row, col);
            }
        }
        setChanged();
        notifyObservers();
        
        if(hiddenPieces == numBombs)
        {
            try{
                winning();
            }
            catch(java.io.IOException e)
            {
                System.err.println("Save file isn't where it should be!");
            }
        }
    }
    
    private void winning() throws java.io.IOException
    {
        timer.pause();
        hiddenPieces = -1;
        
        if(JOptionPane.showConfirmDialog(null, "Game " + getGame() + " cleared!\nSave your time of " + timer.getFormattedTime() + "?", "Win Dialog", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                /*String name = JOptionPane.showInputDialog(null, "Your score of " + timer.getFormattedTime()
                    + " will be entered into the Hall of Fame.\n Enter your name:", "Hall of Fame Entry", JOptionPane.INFORMATION_MESSAGE);
                if(name != null)
                {*/
                    saveScore("" + timer.getFormattedTime());
                //}
            }
    }
    
    private void defeat(MinesCell cell)
    {
        timer.pause();
        
        for(int row = 0; row < board.getRowCount(); row++)
        {
            for(int col = 0; col < board.getColumnCount(); col++)
            {
                if(((MinesCell)board.getValueAt(row,col)).isBomb)
                {
                    ((MinesCell)board.getValueAt(row,col)).setType(Piece.bomb);
                }
                else
                {
                    ((MinesCell)board.getValueAt(row,col)).setType(Piece.empty);
                }
            }
        }
        cell.setType(Piece.exploded);
        hiddenPieces = -1;
        
        setChanged();
        notifyObservers();
        
        JOptionPane.showMessageDialog(null, "You lost.", "Lose Dialog", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Takes a row and col that correspond to a specific KaboomCell on the board
     * that is being revealed. If the cell is completely empty, it will reveal all
     * neighboring cells.
     * 
     * @param row an int representing the row that the cell is on
     * @param col an int representing the col that the cell is on
     */
    private void revealCell(int row, int col)
    {
        int size;
        if(getPreference("Board Size") == null)
            size = 8;
        else
            size = Integer.parseInt(getPreference("Board Size"));
        
        MinesCell cell = (MinesCell)board.getValueAt(row,col);
        if(cell.getType() == Piece.hidden || cell.getType() == Piece.flagged)
        {
            cell.setType(Piece.empty);
            hiddenPieces--;
        }
        if(cell.bombNeighbors == 0)
        {
            if(row>0)
            {
                if(col > 0)
                {
                    if(((MinesCell)board.getValueAt(row-1,col-1)).getType() == Piece.hidden
                        || ((MinesCell)board.getValueAt(row-1,col-1)).getType() == Piece.flagged)
                        revealCell(row-1, col-1);
                }
                if(((MinesCell)board.getValueAt(row-1,col)).getType() == Piece.hidden
                    || ((MinesCell)board.getValueAt(row-1,col)).getType() == Piece.flagged)
                    revealCell(row-1, col);
                if(col < size-1)
                {
                    if(((MinesCell)board.getValueAt(row-1,col+1)).getType() == Piece.hidden
                        || ((MinesCell)board.getValueAt(row-1,col+1)).getType() == Piece.flagged)
                        revealCell(row-1, col+1);
                }
            }
            if(col>0)
            {
                if(((MinesCell)board.getValueAt(row,col-1)).getType() == Piece.hidden
                    || ((MinesCell)board.getValueAt(row,col-1)).getType() == Piece.flagged)
                    revealCell(row, col-1);
            }
            if(col<size-1)
            {
                if(((MinesCell)board.getValueAt(row,col+1)).getType() == Piece.hidden
                    || ((MinesCell)board.getValueAt(row,col+1)).getType() == Piece.flagged)
                    revealCell(row, col+1);
            }
            if(row<size-1)
            {
                if(col > 0)
                {
                    if(((MinesCell)board.getValueAt(row+1,col-1)).getType() == Piece.hidden
                        || ((MinesCell)board.getValueAt(row+1,col-1)).getType() == Piece.flagged)
                        revealCell(row+1, col-1);
                }
                if(((MinesCell)board.getValueAt(row+1,col)).getType() == Piece.hidden
                    || ((MinesCell)board.getValueAt(row+1,col)).getType() == Piece.flagged)
                    revealCell(row+1, col);
                if(col < size-1)
                {
                    if(((MinesCell)board.getValueAt(row+1,col+1)).getType() == Piece.hidden
                        || ((MinesCell)board.getValueAt(row+1,col+1)).getType() == Piece.flagged)
                        revealCell(row+1, col+1);
                }
            }
        }
    }
    
    private void updateStatus()
    {
        String flagString = String.format("%1$6s", flags + "/" + numBombs);
        
        status.setLabelText("Moves: " + moves + "   Flags:" + flagString + "   ");
    }
    
    public java.util.List<javax.swing.Action> getMenuActions()
    {
        ArrayList<javax.swing.Action> retlist = new ArrayList<javax.swing.Action>();
        
        retlist.add(new myAction("Restart", KeyEvent.VK_R) {
            public void actionPerformed(ActionEvent e)
            {
                restart();
            }
        });
        
        retlist.add(new myAction("New Game", KeyEvent.VK_N) {
            public void actionPerformed(ActionEvent e)
            {
                incrementGame();
                restart();
            }
        });
        
        retlist.add(new myAction("Select Game", KeyEvent.VK_G) {
            public void actionPerformed(ActionEvent e)
            {
                String num = JOptionPane.showInputDialog(null, "Enter desired game number (1 - 5000):", "Select Game", JOptionPane.INFORMATION_MESSAGE);
                if(num != null && Integer.parseInt(num) > 0 && Integer.parseInt(num) <= 5000)
                {
                    setGame(Integer.parseInt(num));
                }
                restart();
            }
        });
        
        retlist.add(new myAction("Scores", KeyEvent.VK_S) {
            public void actionPerformed(ActionEvent e)
            {
                showHighScores();
            }
        });
        
        retlist.add(new myAction("Cheat", KeyEvent.VK_C) {
            public void actionPerformed(ActionEvent e)
            {
                int size;
                if(getPreference("Board Size") == null)
                    size = 8;
                else
                    size = Integer.parseInt(getPreference("Board Size"));
                    
                for(int row = 0; row < size; row++)
                {
                    for(int col = 0; col < size; col++)
                    {
                        if(((MinesCell)board.getValueAt(row, col)).isBomb)
                        {
                            ((MinesCell)board.getValueAt(row, col)).setType(Piece.bomb);
                        }
                        else
                        {
                            ((MinesCell)board.getValueAt(row, col)).setType(Piece.empty);
                        }
                    }
                }
                isCheating = true;
                
                setChanged();
                notifyObservers();
            }
        });
        
        retlist.add(new myAction("Quit", KeyEvent.VK_Q) {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        
        return retlist;
    }
    
    abstract class myAction extends AbstractAction {
        public myAction(String text, int keyevent)
        {
            super(text);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyevent, ActionEvent.ALT_MASK));
        }
    }
}
