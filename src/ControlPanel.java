import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ControlPanel extends JPanel {

    private JButton clearDisplayButton, undoButton, redoButton, saveButton, pickColorButton;
    private JToggleButton toggleErasing;
    private JColorChooser colorChooser;
    private JSlider penSizeSlider, numberOfSectorsSlider, undoSlider, redoSlider;
    private JCheckBox showSectorLinersCheckbox, reflectDrawnPointsCheckbox;
    private JLabel penSizeLabel, numberOfSectorsLabel, undoLabel, redoLabel;
    private DoilyPanel doilyPanel;
    private GalleryPanel galleryPanel;


    public ControlPanel(DoilyPanel doilyPanel, GalleryPanel galleryPanel){
        this.setLayout(new GridLayout(7, 2));
        setMaximumSize(new Dimension(50, 480));
        this.doilyPanel = doilyPanel;
        this.galleryPanel = galleryPanel;


        // Create buttons
        clearDisplayButton = new JButton("Clear Display");
        undoButton = new JButton("Undo Last Draw Point");
        redoButton = new JButton("Redo last undone point");
        toggleErasing = new JToggleButton("Switch to eraser");
        saveButton = new JButton("Save doily to gallery");
        pickColorButton = new JButton("Pick a color");

        //Create colour chooser.
        colorChooser = new JColorChooser(Color.BLUE);

        //Create various sliders.
        penSizeSlider = new JSlider(2, 42, 10);
        numberOfSectorsSlider = new JSlider(4, 24, 4);
        undoSlider = new JSlider(1, 50, 5);
        redoSlider = new JSlider(1, 50, 5);


        //Create the different checkboxes and corresponding labels.
        showSectorLinersCheckbox = new JCheckBox("Show Sector Lines");
        reflectDrawnPointsCheckbox = new JCheckBox("Reflect Drawn Points");
        penSizeLabel = new JLabel("");
        updatePenSizeLabel();
        numberOfSectorsLabel = new JLabel("");
        updateNumberOfSectorsLabel();
        undoLabel = new JLabel("");
        updateUndoLabel();
        redoLabel = new JLabel("");
        updateRedoLabel();


        // Add event listeners to sliders to respond to change of state (slider changes value).
        SliderChangeListener sliderChangeListener = new SliderChangeListener();
        penSizeSlider.addChangeListener(sliderChangeListener);
        numberOfSectorsSlider.addChangeListener(sliderChangeListener);
        undoSlider.addChangeListener(sliderChangeListener);
        redoSlider.addChangeListener(sliderChangeListener);


        // Add labels "Action Commands" to buttons so we can identify the source of the click in the event listener.
        BtnClickListener btnClickListener = new BtnClickListener();
        clearDisplayButton.setActionCommand("clearDisplay");
        undoButton.setActionCommand("undo");
        redoButton.setActionCommand("redo");
        toggleErasing.setActionCommand("toggleErasing");
        saveButton.setActionCommand("save");
        pickColorButton.setActionCommand("colorPick");


        //add event listeners to buttons. Listen for button clicks.
        clearDisplayButton.addActionListener(btnClickListener);
        undoButton.addActionListener(btnClickListener);
        redoButton.addActionListener(btnClickListener);
        toggleErasing.addActionListener(btnClickListener);
        saveButton.addActionListener(btnClickListener);
        pickColorButton.addActionListener(btnClickListener);


        //Listen to change of state of checkboxes (selected/unselected).
        CheckboxListener checkboxListener = new CheckboxListener();
        showSectorLinersCheckbox.addItemListener(checkboxListener);
        reflectDrawnPointsCheckbox.addItemListener(checkboxListener);
        //Show sector lines by default
        showSectorLinersCheckbox.setSelected(true);

        //Create Panels to hold the pair (label, slider).
        JPanel undoPanel = new JPanel();
        undoPanel.setLayout(new GridLayout(1, 2));
        undoPanel.add(undoLabel);
        undoPanel.add(undoSlider);

        JPanel redoPanel = new JPanel();
        redoPanel.setLayout(new GridLayout(1, 2));
        redoPanel.add(redoLabel);
        redoPanel.add(redoSlider);


        // Add all the components to the ControlPanel
        add(clearDisplayButton);
        add(saveButton);
        add(undoPanel);
        add(undoButton);
        add(redoPanel);
        add(redoButton);
        add(penSizeLabel);
        add(penSizeSlider);
        add(numberOfSectorsLabel);
        add(numberOfSectorsSlider);
        add(showSectorLinersCheckbox);
        add(reflectDrawnPointsCheckbox);
        add(toggleErasing);
        add(pickColorButton);

    }

    private void saveDoily(){
        if (galleryPanel.getNumberImages() >= GalleryPanel.MAX_IMAGES){
            JOptionPane.showMessageDialog(null, "Maximum number of Doilies in Gallery Panel Reached.\nRemove doilies from gallery if you would like to add more.");
        } else {
            galleryPanel.addImage(getDoilyPanel().getBufferedImage());
        }
    }

    private void updateNumberOfSectorsLabel(){
        numberOfSectorsLabel.setText("Number of Sectors : " + numberOfSectorsSlider.getValue());
    }

    private void updatePenSizeLabel(){
        penSizeLabel.setText("Pen Size : " + penSizeSlider.getValue() + " px");
    }

    private void updateUndoLabel(){
        undoLabel.setText("Undo Operations : " + undoSlider.getValue());
        undoButton.setText("Undo " + undoSlider.getValue() + " Points");
    }

    private void updateRedoLabel(){
        redoLabel.setText("Redo Operations : " + redoSlider.getValue());
        redoButton.setText("Redo " + redoSlider.getValue() + " Points");
    }

    private void updateToggleErasingButton(){
        boolean toggled = toggleErasing.isSelected();
        if (toggled){
            toggleErasing.setText("Switch to Pen");
        } else {
            toggleErasing.setText("Switch to Eraser");
        }

        doilyPanel.setErasing(toggled);
    }

    public DoilyPanel getDoilyPanel() {
        return doilyPanel;
    }

    class SliderChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            if (slider.equals(numberOfSectorsSlider)){
                updateNumberOfSectorsLabel();
                getDoilyPanel().setNumberOfSectors(slider.getValue());
            } else if (slider.equals(penSizeSlider)){
                updatePenSizeLabel();
                getDoilyPanel().setPenSize(slider.getValue());
            } else if(slider.equals(undoSlider)){
                updateUndoLabel();
            } else if (slider.equals(redoSlider)){
                updateRedoLabel();
            }

            getDoilyPanel().repaint();
        }
    }

    class BtnClickListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()){
                case "clearDisplay":
                    System.out.println("Clear display");
                    getDoilyPanel().clearDisplay();
                    break;
                case "undo":
                    System.out.println("Undo");
                    getDoilyPanel().undoDots(undoSlider.getValue());
                    break;
                case "redo":
                    System.out.println("Redo");
                    getDoilyPanel().redo(redoSlider.getValue());
                    break;
                case "toggleErasing":
                    System.out.println("Toggle eraser Pen");
                    updateToggleErasingButton();
                    break;
                case "save":
                    System.out.println("Save");
                    saveDoily();
                    break;
                case "colorPick":
                    System.out.println("Color pick");
                    getDoilyPanel().setPenColor(JColorChooser.showDialog(null, "Pick a color !", getDoilyPanel().getPenColor()));
                    break;
            }
        }
    }

    class CheckboxListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent e) {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;

            if (checkBox.equals(showSectorLinersCheckbox)){
                getDoilyPanel().setDrawSectorLines(selected);
            } else if (checkBox.equals(reflectDrawnPointsCheckbox)){
                getDoilyPanel().setReflectDrawnPoints(selected);
            }

            getDoilyPanel().repaint();
        }
    }


}
