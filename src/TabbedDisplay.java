import javax.swing.*;
import java.awt.*;

public class TabbedDisplay extends JPanel {
    // This tabbed display is used to hold the (Gallery Panel, Doily Panel) combo on one side, and the Gallery Panel as another tab.
    private JTabbedPane tabbedPane;

    public TabbedDisplay(){
        super();
        setLayout(new GridLayout(1, 1));

        tabbedPane = new JTabbedPane();
        add(tabbedPane);
    }


    public void addComponent(JPanel panel, String title){
        tabbedPane.add(panel, title);
    }
}
