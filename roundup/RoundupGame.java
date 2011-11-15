package roundup;

import gridgame.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Write a description of class RoundupGame here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoundupGame extends GridGame implements CursorKeyAdapter
{
    private RoundupBoard board;
    private RoundupStatus status;
    private TimerLabel timer;
    
    private int moves = 0;
    private RoundupCell selected = null;
    private int selectedRow;
    private int selectedCol;
    
    private String movesMade = "";
    
    private boolean gameOver = false;

    /**
     * Constructor for objects of class RoundupGame
     */
    public RoundupGame(GridBoard board, GridStatus status)
    {
        this.board = (RoundupBoard)board;
        this.status = (RoundupStatus)status;
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
        updateStatus();
        
        setChanged();
        notifyObservers(new Integer(getGame()));
    }
    
    public void restart()
    {
        board.setGame(getGame());
        selected = null;
        moves = 0;
        gameOver = false;
        updateStatus();
        movesMade = "";
        
        setChanged();
        notifyObservers("bkgd");
    }
    
    public void makeMove(int row, int col)
    {
        //moves++;
        //updateStatus();
        
        RoundupCell cell = (RoundupCell)board.getValueAt(row, col);
        if(cell.getType() != Imgs.dot && cell.getType() != Imgs.empty)
        {
            selected = cell;
            selectedRow = row;
            selectedCol = col;
        }
        else
        {
            selected = null;
        }
        
        setChanged();
        notifyObservers();
    }
    
    private void winning()
    {
        timer.pause();
        
        if(JOptionPane.showConfirmDialog(null, "You won!\nSave your time?", "Win Dialog", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            /*String name = JOptionPane.showInputDialog(null, "Your score of " + timer.getFormattedTime()
                + " will be entered into the Hall of Fame.\n Enter your name:", "Hall of Fame Entry", JOptionPane.INFORMATION_MESSAGE);
            if(name != null)
            {*/
                saveScore("" + timer.getFormattedTime() + " " + getGame() + " " + movesMade);
                //Maybe make my own?
                //
            //}
        }
        
        gameOver = true;
    }
    
    private void defeat()
    {
        setChanged();
        notifyObservers("fadedbkgd");
        
        status.setLabelText("LOSE   ");
        gameOver = true;
    }
    
    private void updateStatus()
    {
        status.setLabelText("Moves: " + moves + "   ");
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
                if(getGame() == 18)
                {
                    setGame(1);
                }
                else
                {
                    setGame(getGame()+1);
                }
                timer.restart();
                
                
                setChanged();
                notifyObservers(new Integer(getGame()));
                
                restart();
            }
        });
        
        retlist.add(new myAction("Select Game", KeyEvent.VK_S) {
            public void actionPerformed(ActionEvent e)
            {
                String num = JOptionPane.showInputDialog(null, "Enter desired game number (1 - 18):", "Select Game", JOptionPane.INFORMATION_MESSAGE);
                if(num != null && Integer.parseInt(num) > 0 && Integer.parseInt(num) <= 18)
                {
                    setGame(Integer.parseInt(num));
                }
                
                setChanged();
                notifyObservers(new Integer(getGame()));
                
                restart();
            }
        });
        
        retlist.add(new myAction("Hall of Fame", KeyEvent.VK_H) {
            public void actionPerformed(ActionEvent e)
            {
                showHighScores();
            }
        });
        
        retlist.add(new myAction("About", KeyEvent.VK_A) {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, "Roundup Version 1.0\n" + 
                "Click on a robot then press a cursor key to move the robot.\n" + 
                "The robot will move in a straight line in the direction specified.\n" + 
                "The robot can only stop when it runs into another robot, or \n" +
                "walks off the board. Your goal is to guide the red robot to \n" +
                "end up positioned on the center square.\n" + 
                "The are 18 puzzles that can be played in any order." +
                "Android Icon by http://madeliniz.deviantart.com", "About", JOptionPane.INFORMATION_MESSAGE);
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
    
    public void processUp()
    {
        if(selected == null || gameOver == true)
        {   return; }
        
        moves++;
        clearDots();
        updateStatus();
        boolean red = selected.isRed;
        movesMade += Integer.toString(selectedRow) + Integer.toString(selectedCol) + "U";
        
        setChanged();
        notifyObservers();
        
        RoundupCell destination = ((RoundupCell)board.getValueAt(selectedRow-1, selectedCol));
        
        while(destination.getType() == Imgs.empty)
        {
            ((RoundupCell)board.getValueAt(selectedRow, selectedCol)).setType(Imgs.dot);
            
            if(selectedRow-1 == 0)
            {
                destination.setType(Imgs.deadup);
                defeat();
                break;
            }
            else if(red)
            {
                destination.setType(Imgs.redup);
            }
            else
            {
                destination.setType(Imgs.greenup);
            }
            
            selectedRow--;
            selected = ((RoundupCell)board.getValueAt(selectedRow, selectedCol));
            destination = ((RoundupCell)board.getValueAt(selectedRow-1, selectedCol));
        }
        
        setChanged();
        notifyObservers();
        
        if(red && selectedRow == 3 && selectedCol == 3)
        {
            winning();
        }
        
        selected = null;
    }
    
    public void processDown()
    {
        if(selected == null || gameOver == true)
        {   return; }
        
        moves++;
        updateStatus();
        clearDots();
        boolean red = selected.isRed;
        movesMade += Integer.toString(selectedRow) + Integer.toString(selectedCol) + "D";
        
        setChanged();
        notifyObservers();
        
        RoundupCell destination = ((RoundupCell)board.getValueAt(selectedRow+1, selectedCol));
        
        while(destination.getType() == Imgs.empty)
        {
            ((RoundupCell)board.getValueAt(selectedRow, selectedCol)).setType(Imgs.dot);
            
            if(selectedRow+1 == 6)
            {
                destination.setType(Imgs.deaddown);
                defeat();
                break;
            }
            else if(red)
            {
                destination.setType(Imgs.reddown);
            }
            else
            {
                destination.setType(Imgs.greendown);
            }
            
            selectedRow++;
            selected = ((RoundupCell)board.getValueAt(selectedRow, selectedCol));
            destination = ((RoundupCell)board.getValueAt(selectedRow+1, selectedCol));
        }
        
        setChanged();
        notifyObservers();
        
        if(red && selectedRow == 3 && selectedCol == 3)
        {
            winning();
        }
        
        selected = null;
    }
    
    public void processLeft()
    {
        if(selected == null || gameOver == true)
        {   return; }
        
        moves++;
        updateStatus();
        clearDots();
        boolean red = selected.isRed;
        movesMade += Integer.toString(selectedRow) + Integer.toString(selectedCol) + "L";
        
        setChanged();
        notifyObservers();
        
        RoundupCell destination = ((RoundupCell)board.getValueAt(selectedRow, selectedCol-1));
        
        while(destination.getType() == Imgs.empty)
        {
            ((RoundupCell)board.getValueAt(selectedRow, selectedCol)).setType(Imgs.dot);
            
            if(selectedCol-1 == 0)
            {
                destination.setType(Imgs.deadleft);
                defeat();
                break;
            }
            else if(red)
            {
                destination.setType(Imgs.redleft);
            }
            else
            {
                destination.setType(Imgs.greenleft);
            }
            
            selectedCol--;
            selected = ((RoundupCell)board.getValueAt(selectedRow, selectedCol));
            destination = ((RoundupCell)board.getValueAt(selectedRow, selectedCol-1));
        }
        
        setChanged();
        notifyObservers();
        
        if(red && selectedRow == 3 && selectedCol == 3)
        {
            winning();
        }
        
        selected = null;
    }
    
    public void processRight()
    {
        if(selected == null || gameOver == true)
        {   return; }
        
        moves++;
        updateStatus();
        clearDots();
        boolean red = selected.isRed;
        movesMade += Integer.toString(selectedRow) + Integer.toString(selectedCol) + "R";
        
        setChanged();
        notifyObservers();
        
        RoundupCell destination = ((RoundupCell)board.getValueAt(selectedRow, selectedCol+1));
        
        while(destination.getType() == Imgs.empty)
        {
            ((RoundupCell)board.getValueAt(selectedRow, selectedCol)).setType(Imgs.dot);
            
            if(selectedCol+1 == 6)
            {
                destination.setType(Imgs.deadright);
                defeat();
                break;
            }
            else if(red)
            {
                destination.setType(Imgs.redright);
            }
            else
            {
                destination.setType(Imgs.greenright);
            }
            
            selectedCol++;
            selected = ((RoundupCell)board.getValueAt(selectedRow, selectedCol));
            destination = ((RoundupCell)board.getValueAt(selectedRow, selectedCol+1));
        }
        
        setChanged();
        notifyObservers();
        
        if(red && selectedRow == 3 && selectedCol == 3)
        {
            winning();
        }
        
        selected = null;
    }
    
    private void clearDots()
    {
        //clear all the dots from the board, making them Imgs.empty instead
        for(int row = 1; row <= 5; row++)
        {
            for(int col = 1; col <= 5; col++)
            {
                if(((RoundupCell)board.getValueAt(row, col)).getType() == Imgs.dot)
                {
                    ((RoundupCell)board.getValueAt(row, col)).setType(Imgs.empty);
                }
            }
        }
    }
    
    public CursorKeyAdapter getKeyAdapter()
    {
        return this;
    }
}
