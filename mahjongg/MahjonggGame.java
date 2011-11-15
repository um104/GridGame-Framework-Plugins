package mahjongg;

import gridgame.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Write a description of class MahjonggGame here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MahjonggGame extends GridGame
{
    // instance variables - replace the example below with your own
    private MahjonggBoard board;
    private MahjonggStatus status;
    private TimerLabel timer;
    
    private MahjonggCell selected = null;
    private int selectedRow = 0;
    private int selectedCol = 0;
    private int tilesLeft = 0;

    /**
     * Constructor for objects of class MahjonggGame
     */
    public MahjonggGame(GridBoard board, GridStatus status)
    {
        this.board = (MahjonggBoard)board;
        this.status = (MahjonggStatus)status;
        setGame(1);
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
        if(timer == null)
            timer = new TimerLabel();
        else
            timer.restart();

        status.setAttributes(timer);
        status.add(timer);
        tilesLeft = 84;
        selected = null;
        selectedRow = 0;
        selectedCol = 0;
        
        board.resetBoard();
        
        updateStatus();
        setChanged();
        notifyObservers(new Integer(getGame()));
    }
    
    public void restart()
    {
        init();
    }
    
    public void makeMove(int row, int col)
    {
        if(((MahjonggCell)board.getValueAt(row, col)).getRank() != 0)
        {
            if((col != 0) && (col != 11) && ((MahjonggCell)board.getValueAt(row,col-1)).getRank() != 0 && ((MahjonggCell)board.getValueAt(row,col+1)).getRank() != 0)
                {
                    selected = null;
                }
                else if((selected != null) && selected.equals(board.getValueAt(row,col)) && selected != board.getValueAt(row,col))
                {
                    board.setCell(row,col,new MahjonggCell(Suits.B, 0));
                    board.setCell(selectedRow,selectedCol,new MahjonggCell(Suits.B, 0));
                    tilesLeft = tilesLeft - 2;
                    updateStatus();
                    if(tilesLeft == 0)
                    {
                        winning();
                    }
                    selected = null;
                }
                else
                {
                    selected = (MahjonggCell) board.getValueAt(row,col);
                    selectedRow = row;
                    selectedCol = col;
                }
        }
        
        setChanged();
        notifyObservers();
    }
    
    private void winning()
    {
        timer.pause();
        
        JOptionPane.showMessageDialog(null, "You win!", "Message", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatus()
    {status.setLabelText("Tiles Left: " + tilesLeft +  "   ");
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
        
        retlist.add(new myAction("Hint", KeyEvent.VK_H) {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, "Hint: " + movesLeft(), "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        retlist.add(new myAction("Cheat", KeyEvent.VK_C) {
            public void actionPerformed(ActionEvent e)
            {
                tilesLeft = 2;
                updateStatus();
                
                for(int row = 0; row < 8; row++)
                {
                    for(int col = 0; col < 12; col++)
                    {
                        board.setCell(row,col,new MahjonggCell(Suits.B, 0));
                    }
                    board.setCell(3,5, new MahjonggCell(Suits.B, 1));
                    board.setCell(3,6, new MahjonggCell(Suits.B, 1));
                    
                    setChanged();
                    notifyObservers();
                }
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
    
    private String movesLeft()
    {
        ArrayList<MahjonggCell> list = new ArrayList<MahjonggCell>();
        for(int row = 0; row < 8; row++)
        {
            for(int col = 0; col < 12; col++)
            {
                if(((MahjonggCell)board.getValueAt(row, col)).getRank() != 0)
                {
                    if((col == 0) || (col == 11) || ((MahjonggCell)board.getValueAt(row,col-1)).getRank() == 0 || ((MahjonggCell)board.getValueAt(row,col+1)).getRank() == 0)
                    {
                        if(list.contains((MahjonggCell)board.getValueAt(row,col)))
                        {
                            return ((MahjonggCell)board.getValueAt(row,col)).toString();
                        }
                        list.add((MahjonggCell)board.getValueAt(row,col));
                    }
                }
            }
        }
        
        return "No moves available.";
    }
    
    abstract class myAction extends AbstractAction {
        public myAction(String text, int keyevent)
        {
            super(text);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyevent, ActionEvent.ALT_MASK));
        }
    }
}
