import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class GalleryPanel extends JPanel{
    static int MAX_IMAGES = 12;

    private JScrollPane scrollPane;
    private JPanel gridPanel;
    ArrayList<ImagePanel> imagePanels;
    ArrayList<ImagePanel> emptyPanels;
    // This gallery panel can hold up to 12 ( 6 * 2 ) images.
    private final int ROW_COUNT = 2;
    private final int COL_COUNT = 6;
    private final int MAX_PANELS_SCREEN = ROW_COUNT * COL_COUNT;
    private Dimension doilyPanelDimensions;
    private Display display;
    private Border selectedBorder, unselectedBorder;
    private ImagePanelClickListener imagePanelClickListener;

    private JButton removeButton, selectAllButton, deselectAllButton;


    public GalleryPanel(Display display){
        // Use a border layout. Scrolled Pane will be in the center, three control buttons will be NORTH.
        setLayout(new BorderLayout());

        // Store a reference to the display
        this.display = display;
        this.doilyPanelDimensions = new Dimension(DoilyPanel.WIDTH, DoilyPanel.HEIGHT);

        // Lists to store image panels and empty panels
        imagePanels = new ArrayList<>();
        emptyPanels = new ArrayList<>();

        // Two borders : one for selected panels and one for unselected panels.
        selectedBorder = BorderFactory.createLineBorder(Color.red, 2);
        unselectedBorder = BorderFactory.createLineBorder(Color.blue, 2);

        // One click listener for all image panels
        imagePanelClickListener = new ImagePanelClickListener();

        // Create a gridpanel to store all images. This will be the parent component that will reside in the scroll pane.
        gridPanel = new JPanel(new GridLayout(ROW_COUNT, COL_COUNT));
        gridPanel.setPreferredSize(new Dimension(6 * 370, 2*370));

        // ScrollPane containing one JPanel with a gridlayout. Our application can only display 8 doilies at a time. ScrollPanel enables showing of all 12.
        scrollPane = new JScrollPane(gridPanel);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);


        // Create the three control buttons
        removeButton = new JButton("Remove Doily");
        selectAllButton = new JButton("Select All");
        deselectAllButton = new JButton("Deselect All");

        // Add labels "Action Commands" to buttons so we can identify the source of the click in the event listener.
        removeButton.setActionCommand("remove");
        selectAllButton.setActionCommand("select");
        deselectAllButton.setActionCommand("deselect");


        // Register the click listener with the buttons
        ActionListener myBtnClickListener = new ButtonClickListener();
        removeButton.addActionListener(myBtnClickListener);
        selectAllButton.addActionListener(myBtnClickListener);
        deselectAllButton.addActionListener(myBtnClickListener);

        // This "mini control panel" is a JPanel which stores the three control buttons for the gallery panel.
        // This panel will be added in the Border Layout North.
        JPanel miniControlPanel = new JPanel(new GridLayout(1, 3));
        miniControlPanel.add(removeButton);
        miniControlPanel.add(selectAllButton);
        miniControlPanel.add(deselectAllButton);
        // Add the mini control panel to NORTH
        add(miniControlPanel, BorderLayout.NORTH);


        // Finally add the scroll pane (which contains the Panel which itself contains the images) to CENTER.
        add(scrollPane, BorderLayout.CENTER);

    }

    public void addImage(BufferedImage bufferedImage){
        // Creates an image panel to add to the gallery panel.
        ImagePanel imagePanel = new ImagePanel(bufferedImage, doilyPanelDimensions);

        // By default, the imagepanel is unselected and therefore has an unselected border style.
        imagePanel.setBorder(unselectedBorder);

        // Listen for clicks. Needed for selection capability.
        imagePanel.addMouseListener(imagePanelClickListener);

        // Add the newly created image panel to gridPanel and the List of image panels. (references are stored to enable event listening, deletion, changing borders).
        gridPanel.add(imagePanel);
        imagePanels.add(imagePanel);
        update();
    }

    public void update(){
        // Update the Gallery Panel (called when Doily is added or removed).

        //Clear everything.
        clear();

        //Add everything again. This recalculates the number of empty panels to add.
        addAll();
    }

    public void clear(){
        // Clears the gallery panel. This is used to redraw the gallery panel in the event of a doily being added or removed.

        //Remove all the image panels from the grid (gui elements).
        for (ImagePanel imagePanel: imagePanels){
            gridPanel.remove(imagePanel);
        }

        //Redraw the gridpanel
        gridPanel.revalidate();
        gridPanel.repaint();
        repaint();


        //Remove all the empty panels.
        for (ImagePanel emptyPanel : emptyPanels){
            gridPanel.remove(emptyPanel);
        }

        /*Clear the array list storing the empty panels.
          This is not very efficient as we're continuously destroying and recreating empty panels.
          However, efficiency is not an issue here, and empty image panels are relatively light weight. */
        emptyPanels.clear();
    }

    public void addAll(){
        // Adds all the image panels to the gallery, then fills the remaining spaces with empty panels (if required).
        for (ImagePanel imagePanel: imagePanels){
            gridPanel.add(imagePanel);
        }

        fillHoles();
    }

    public void fillHoles(){
        for (int i = 0; i < MAX_PANELS_SCREEN - imagePanels.size(); i++){
            //Fill the remaining spaces with empty panels
            //This loop will not run if the screen is full
            addEmptyPanel();
        }
    }

    public void addEmptyPanel(){
        // Adds an empty panel. This empty panel is filled with a green circle.
        ImagePanel emptyPanel = new ImagePanel(null, this.doilyPanelDimensions);

        gridPanel.add(emptyPanel);
        emptyPanels.add(emptyPanel);

        repaint();
    }

    public void selectAll(){
        /* For each image panel : set its selected field to true and add a border around
           the panel to show the user that the panel has been selected */
        for (ImagePanel imagePanel : imagePanels){
            imagePanel.setSelected(true);
            imagePanel.setBorder(selectedBorder);
        }

        repaint();
    }

    public void deselectAll(){
        /* For each image panel : set its selected field to false and remove the border around
           the panel to show the user that the panel has been deselected */
        for (ImagePanel imagePanel : imagePanels){
            imagePanel.setSelected(false);
            imagePanel.setBorder(unselectedBorder);
        }

        repaint();
    }

    public void removeSelected(){
        // Remove currently selected imagePanels. This may (and most of the time will) remove multiple images.
        Iterator<ImagePanel> it = imagePanels.iterator();
        ImagePanel imagePanel = null;
        while (it.hasNext()){
            imagePanel = it.next();
            if (imagePanel.isSelected()){
                gridPanel.remove(imagePanel);
                it.remove();
            }
        }

        update();
    }

    public int getNumberImages(){
        //Returns the current number of images being stored and displayed in the gallery panel
        return imagePanels.size();
    }

    class ImagePanelClickListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            //Mouse Click listener to let the user select/deselect one or more image panels
            ImagePanel source = (ImagePanel) e.getSource();

            // Loop through image panels until we find the one that was clicked.
            for (ImagePanel imagePanel: imagePanels){
                if (imagePanel.equals(source)){

                    //Toggle the selected state. If this panel has been selected, deselect it and vice versa.
                    if (imagePanel.isSelected()){
                        imagePanel.setSelected(false);
                        imagePanel.setBorder(unselectedBorder);
                    } else {
                        imagePanel.setSelected(true);
                        imagePanel.setBorder(selectedBorder);
                    }
                    break;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    class ButtonClickListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //Listener to react to the clicks of the three buttons above the gallery.
            switch (e.getActionCommand()){
                case "remove":
                    removeSelected();
                    break;
                case "select":
                    selectAll();
                    break;
                case "deselect":
                    deselectAll();
                    break;

            }
        }
    }

}
