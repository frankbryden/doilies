import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private BufferedImage image;

    // Scale factor. This means images in the gallery will be half the width and half the weight of the original doily.
    private final int scale = 2;

    // Each image has a selected field. By default images are unselected.
    private boolean selected = false;


    public ImagePanel(BufferedImage unscaledImage, Dimension dimension){
        if (unscaledImage != null){

            this.image = new BufferedImage(unscaledImage.getWidth()/scale, unscaledImage.getHeight()/scale, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(unscaledImage  , 0, 0, unscaledImage.getWidth()/scale, unscaledImage.getHeight()/scale, 0, 0, unscaledImage.getWidth(), unscaledImage.getHeight(), null);

        } else {
            //In the case of an empty panel, simply set BufferedImage to null
            this.image = null;
        }
        //setPreferredSize(new Dimension(DoilyPanel.WIDTH/scale, DoilyPanel.HEIGHT/scale));
        setPreferredSize(new Dimension(360, 360));

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if (this.image != null){
            // If this is an image panel with a buffered image, draw the image.
            g.drawImage(this.image, 0, 0, this);

        } else {
            /* Place holder referred to as "EmptyPanel". Used to fill space
               when we have less than 12 images displayed in gallery. */
            g.setColor(Color.white);

            // A white filled square to replace an image in the gallery
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
