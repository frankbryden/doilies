import javax.swing.*;
import java.awt.*;

public class Display extends JFrame {

    // This is the Display class. It holds all the panel components : a Doily Panel, a Control Panel and a Gallery Panel.

    private DoilyPanel doilyPanel;
    private ControlPanel controlPanel;
    private GalleryPanel galleryPanel;

    public Display(){

        doilyPanel = new DoilyPanel();
        galleryPanel = new GalleryPanel(this);
        controlPanel = new ControlPanel(doilyPanel, galleryPanel);

        // This panel holds both the Control Panel and the Doily Panel
        JPanel doilyControlPanel = new JPanel();
        doilyControlPanel.setLayout(new GridBagLayout());

        doilyControlPanel.add(doilyPanel);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.8;
        doilyControlPanel.add(controlPanel, c);

        // This tabbed display has 2 tabs. One for the Panel holding (Control Panel; Doily Panel) and one for the Gallery Panel
        TabbedDisplay tabbedDisplay = new TabbedDisplay();
        tabbedDisplay.addComponent(doilyControlPanel, "Draw");
        tabbedDisplay.addComponent(galleryPanel, "Gallery");

        // Add the Tabbed display to the frame.
        add(tabbedDisplay);

        setPreferredSize(new Dimension(1500, 830));

        // Swing setup.
        setVisible(true);
        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }



}
