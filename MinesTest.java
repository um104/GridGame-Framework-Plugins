import org.uispec4j.*;
import org.uispec4j.interception.*;
import gridgame.*;
/** Test for the GUI */
public class MinesTest extends UISpecTestCase 
{

  //Need to call this to initialize UiSpec4J
  static    {
    UISpec4J.init();
  }

  Window window;

  //Intercept the window and store it off
  protected void setUp() throws Exception {
    window = WindowInterceptor.run(new Trigger() {
      public void run() {
        String[] args = {"Mines"};
        GridGameLoader.main(args);
        //Mahjongg2D myFrame = new Mahjongg2D();
        //myFrame.layoutGUI();
        //myFrame.setVisible(true);
      }
    });
  }

  private void pause()
  {
    try {
        Thread.sleep(20);
    } catch (InterruptedException ex) {}
  }
  
  public void testMinesGamePlay()
  {
    WindowInterceptor.init(window.getMenuBar().getMenu("Game").getSubMenu("Select Game").triggerClick())
    .process(BasicHandler.init().assertContainsText("Enter desired game number").setText("1").triggerButtonClick("OK")).run();
    boolean titleCorrect = window.titleContains("Mines").isTrue();
    assertTrue("Title bar text incorrect.",titleCorrect);

    
    WindowInterceptor.init(window.getMenuBar().getMenu("Game").getSubMenu("Select Game").triggerClick())
    .process(BasicHandler.init().assertContainsText("Enter desired game number").setText("1").triggerButtonClick("OK")).run();
    titleCorrect = window.titleContains("Mines").isTrue();
    assertTrue("Title bar text incorrect.",titleCorrect);
    TextBox theLabel = window.getTextBox("Status");
    //assertEquals("Status label text incorrect.","Moves: 0", theLabel.getText().substring(0,8));
    // Select Game
    WindowInterceptor.init(window.getMenuBar().getMenu("Game").getSubMenu("Select Game").triggerClick())
    .process(BasicHandler.init().assertContainsText("Enter desired game number").setText("1").triggerButtonClick("OK")).run();
    assertTrue("Title bar text incorrect.",window.titleContains(" - board 1"));    
    theLabel = window.getTextBox("Status");
    System.out.println(theLabel.getText());
    assertTrue("Status label empty.",theLabel.getText().length() > 0);
    assertTrue("Status label wrong flags/bombs",theLabel.getText().contains(" 0/8"));

    // Restart
    window.getMenuBar().getMenu("Game").getSubMenu("Restart").click();  
    assertTrue("Title bar text incorrect.",window.titleContains(" - board 1"));    
    // New Game
    window.getMenuBar().getMenu("Game").getSubMenu("New Game").click(); 
    assertTrue("Title bar text incorrect.",window.titleContains(" - board 2"));    
    window.getMenuBar().getMenu("Game").getSubMenu("New Game").click(); 
    assertTrue("Title bar text incorrect.",window.titleContains(" - board 3"));    
    
    // Select Game - range check
    WindowInterceptor.init(window.getMenuBar().getMenu("Game").getSubMenu("Select Game").triggerClick())
    .process(BasicHandler.init().assertContainsText("Enter desired game number").setText("0").triggerButtonClick("OK")).run();
    assertTrue("Title bar text incorrect.",window.titleContains(" - board 3"));    
    WindowInterceptor.init(window.getMenuBar().getMenu("Game").getSubMenu("Select Game").triggerClick())
    .process(BasicHandler.init().assertContainsText("Enter desired game number").setText("5001").triggerButtonClick("OK")).run();
    assertTrue("Title bar text incorrect.",window.titleContains(" - board 3"));    
    WindowInterceptor.init(window.getMenuBar().getMenu("Game").getSubMenu("Select Game").triggerClick())
    .process(BasicHandler.init().assertContainsText("Enter desired game number").setText("5000").triggerButtonClick("OK")).run();
    assertTrue("Title bar text incorrect.",window.titleContains(" - board 5000"));    
    // New Game wraparound
    window.getMenuBar().getMenu("Game").getSubMenu("New Game").click(); 
    assertTrue("Title bar text incorrect.",window.titleContains(" - board 1"));    

    // High Scores dialog
    WindowInterceptor.init(window.getMenuBar().getMenu("Game").getSubMenu("Scores").triggerClick())
    .process(new WindowHandler()
    {
        public Trigger process(Window dialog) 
        {
            assertTrue("Wrong high scores dialog title",dialog.titleEquals("Hall of Fame"));
            return dialog.getButton("OK").triggerClick();
        }
    }).run();
    
    
    Table table = window.getTable();
    assertTrue("Default board size not 8.",table.columnCountEquals(8));
    //java.awt.Component widget = table.getSwingRendererComponentAt(1,1);
    //System.out.println(widget);
    Renderable p = (Renderable) table.getContentAt(0,0, new ModelTableCellValueConverter());
//    assertEquals("empty",p.toString());
    String rdtext = p.getRenderDescriptor().text;
    //System.out.println(rdtext);
    assertEquals("hidden",rdtext);
    //dumpModel(table);  // display cell contents empty/bomb
    
    // Cheat
    window.getMenuBar().getMenu("Game").getSubMenu("Cheat").click(); 
    //dumpRenderText(table);
    assertTrue("Cheat board 1 incorrect",compareTable(table,board1revealed));

    window.getMenuBar().getMenu("Game").getSubMenu("Restart").click();     
    //dumpRenderText(table);
    assertTrue(compareTable(table,newboard));
    
    // Reveal one square
    table.click(1,1);
    p = (Renderable) table.getContentAt(1,1, new ModelTableCellValueConverter());
    assertEquals("Revealing square 1,1 should be 2.","2",p.getRenderDescriptor().text);
    // shouldn't reveal the neighbors
    p = (Renderable) table.getContentAt(0,1, new ModelTableCellValueConverter());
    assertEquals("Revealing square 1,1 shouln't reveal neighbors","hidden",p.getRenderDescriptor().text);

    table.click(0,7);
    table.click(0,0);
    pause();
    // See if the tiles remaining label is decremented
    theLabel = window.getTextBox("Status");
    assertTrue("Status label empty.",theLabel.getText().length() > 0);
    assertEquals("Moves not incremented correctly.","Moves: 3", theLabel.getText().substring(0,8));
    
    // Click a bomb and capture the lose message.
    WindowInterceptor.init(table.triggerClick(0,1,Key.Modifier.NONE))
    .process(BasicHandler.init().assertContainsText("You lost.").triggerButtonClick("OK")).run();    
    p = (Renderable) table.getContentAt(0,1, new ModelTableCellValueConverter());
    assertEquals("Exploded square incorrect.","exploded",p.getRenderDescriptor().text);
    
    // Do a flag
    window.getMenuBar().getMenu("Game").getSubMenu("Restart").click();     
    table.click(0,0);
    table.click(0,2);
    table.rightClick(0,1);
    theLabel = window.getTextBox("Status");
    assertTrue("Status label empty.",theLabel.getText().length() > 0);
    assertTrue("Moves/Flags not incremented correctly, or label text not formatted correctly - 1/8", theLabel.textContains(" 1/8")); 
    
    // Click the last tile and capture the win message.
    window.getMenuBar().getMenu("Game").getSubMenu("Cheat").click(); 
    WindowInterceptor.init(table.triggerClick(0,0,Key.Modifier.NONE))
    .process(BasicHandler.init().assertContainsText("Game 1 Cleared!").triggerButtonClick("No")).run();

    // Cheating then clicking not on a bomb should win
    window.getMenuBar().getMenu("Game").getSubMenu("Restart").click();     
    window.getMenuBar().getMenu("Game").getSubMenu("Cheat").click();     
    
    WindowInterceptor.init(table.triggerClick(0,0,Key.Modifier.NONE))
    .process(BasicHandler.init().assertContainsText("Game 1 Cleared!").triggerButtonClick("Yes"))
    .process(BasicHandler.init().assertContainsText("Enter your name:").triggerButtonClick("Cancel")).run();

    WindowInterceptor.init(window.getMenuBar().getMenu("Edit").getSubMenu("Preferences").triggerClick())
    .process(new WindowHandler("hall of fame")
    {
        public Trigger process(Window window)
        {
            assertTrue("Small boardsize not selected in preferences",window.getRadioButton("small").isSelected());
            window.getRadioButton("medium").click();
            return window.getButton("Done").triggerClick();
        }
    })
    .run();

    table = window.getTable();
    assertTrue("Medium board size not set to 10.",table.columnCountEquals(10));

    WindowInterceptor.init(window.getMenuBar().getMenu("Edit").getSubMenu("Preferences").triggerClick())
    .process(new WindowHandler("hall of fame")
    {
        public Trigger process(Window window)
        {
            assertTrue("Small boardsize not selected in preferences",window.getRadioButton("small").isSelected());
            window.getRadioButton("large").click();
            return window.getButton("Done").triggerClick();
        }
    })
    .run();

    table = window.getTable();
    assertTrue("Large board size not set to 12.",table.columnCountEquals(12));
    
    // don't use the Quit menu or the test will terminate
  }
  private void dumpModel(Table table)
  {
      int size = table.getColumnCount();
      for (int row=0; row<size; row++)
      {
          for (int col=0; col<size; col++)
          {
              
          Renderable p = (Renderable) table.getContentAt(row,col, new ModelTableCellValueConverter());
          System.out.print(p.toString()+" ");
          }
          System.out.println("}");
      }
  }
  private void dumpRenderText(Table table)
  {
      int size = table.getColumnCount();
      System.out.print("{");
      for (int row=0; row<size; row++)
      {
          System.out.print("{");
          for (int col=0; col<size; col++)
          {
              
          Renderable p = (Renderable) table.getContentAt(row,col, new ModelTableCellValueConverter());
          System.out.print("\"" + p.getRenderDescriptor().text+"\",");
          }
          System.out.println("},");
      }
  }
  private boolean compareTable(Table table,String[][] grid)
  {
      int size = table.getColumnCount();
      
      boolean result = true;
      for (int row=0; row<size; row++)
      {
          for (int col=0; col<size; col++)
          {              
          Renderable p = (Renderable) table.getContentAt(row,col, new ModelTableCellValueConverter());
          result = result && (grid[row][col].equals(p.getRenderDescriptor().text));
          }
      }
      return result;
  }
String[][] newboard = 
{{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",},
{"hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden","hidden",}};
  

String[][] board1revealed = 
{{"2","bomb","1","","","","","",},
{"bomb","2","1","","1","1","1","",},
{"1","1","1","1","2","bomb","1","",},
{"","","1","bomb","2","1","1","",},
{"1","1","1","1","1","","","",},
{"bomb","1","","","","","","",},
{"2","2","1","1","2","2","1","",},
{"1","bomb","1","1","bomb","bomb","1","",},
};


}