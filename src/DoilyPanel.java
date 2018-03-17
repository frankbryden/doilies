import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import javax.imageio.ImageIO;

public class DoilyPanel extends JPanel {
    // This is the Doily Panel. Takes care of the drawing of doilies.

    public static final int WIDTH = 740;
    public static final int HEIGHT = 740;

    private final int SECTOR_LINE_WIDTH = 2;

    //Drawing parameters. Updated by ControlPanel when gui components are interacted with.
    private int numberOfSectors;
    private double angleStep;
    private int penSize;
    private Color penColor;
    private boolean drawSectorLines, reflectDrawnPoints;
    private boolean erasing = false;

    private Point centerPoint;

    private BufferedImage panelImageBuffer;


    private Stack<Dot> dots;
    private Stack<Dot> undoneDots;
    private Stack<Dot> eraserDots;
    public DoilyPanel(){
        numberOfSectors = 4;
        dots = new Stack<Dot>();
        undoneDots = new Stack<Dot>();
        eraserDots = new Stack<Dot>();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        panelImageBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        penSize = 10;
        penColor = Color.RED;
        drawSectorLines = true;
        reflectDrawnPoints = false;
        // The angle step is the full radian span divided by the number of numberOfSectors
        angleStep = 2*Math.PI/ numberOfSectors;



        DrawListener drawListener = new DrawListener();
        addMouseListener(drawListener);
        addMouseMotionListener(drawListener);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Create a background
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        centerPoint = new Point(getWidth()/2, getHeight()/2);

        int lineLength = (int) (getWidth()/2 * 1.4);

        if (numberOfSectors % 2 == 1){
            g2d.rotate(- angleStep/2, centerPoint.x, centerPoint.y);
        }

        for (int i = 0; i < numberOfSectors; i++){
            double angle = i * angleStep;
            double xNorm = Math.cos(angle);
            double yNorm = Math.sin(angle);
            int x = (int) Math.round(xNorm * lineLength);
            int y = (int) Math.round(yNorm * lineLength);
            if (drawSectorLines){
                g2d.setColor(Color.white);
                g2d.setStroke(new BasicStroke(SECTOR_LINE_WIDTH));
                g2d.drawLine(centerPoint.x, centerPoint.y, centerPoint.x + x, centerPoint.y + y);
            }

            // Save state so it can be restored later.
            Color oldColor = g.getColor();
            AffineTransform oldTransform = g2d.getTransform();

            //Rotate graphics so dots are drawn in correct sector.
            g2d.rotate(angle, centerPoint.x, centerPoint.y);
            drawDots(dots, g, reflectDrawnPoints);

            //Restore previous state;
            g.setColor(oldColor);
            g2d.setTransform(oldTransform);
        }

    }

    public void updateErasing(){
        /*
            Erasing works using a stack. When the user draws with an eraser, the dots created are pushed onto the eraserDots stack.
            In this function, we create a new stack called newDots. This stack is filled by the stack of dots (the one we use to keep
            track of the current Doily). We pop the dots off the old stack and push them onto the new stack if they do not overlap with
            an eraser dot.
            Every time an eraser dot is checked, we pop it off

         */
        Stack<Dot> newDots = new Stack<>();
        while (!eraserDots.empty()){
            Dot eraserDot = eraserDots.pop();
            Dot reflectedEraserDot = null;

            // If we reflect drawn points, don't forget to reflect the eraser dot too !
            if (reflectDrawnPoints){
                reflectedEraserDot =  new Dot(eraserDot);
                reflectedEraserDot.setPolar(1.0 - eraserDot.getPolar());
            }

            while(!dots.empty()){
                boolean overlapping = false;
                Dot dot = dots.pop();
                if (eraserDot.overlapping(dot)){
                    overlapping = true;
                }
                if (reflectedEraserDot != null){
                    if (reflectedEraserDot.overlapping(dot)){
                        overlapping = true;
                    }
                }

                /* If the eraser dot and pen dot do not overlap, push the dot onto the
                   the new stack so it can be drawn next paint iteration */
                if (!overlapping){
                    newDots.push(dot);
                }
            }
        }
        dots = newDots;
    }

    public void setErasing(boolean erasing){
        // Switch from paint brush to eraser and vice versa
        this.erasing = erasing;
    }

    @Override
    public Dimension getPreferredSize() {
        /* Override getPreferredSize() to ensure our doily panel is square. If it
           is not square, it does not look good as points drawn in the corner may be out of
           the bounds of the panel on other corners.
         */
        Dimension d;
        Container c = getParent();
        if (c != null) {
            d = c.getSize();
        } else {
            return new Dimension(WIDTH, HEIGHT);
        }
        int w = (int) d.getWidth();
        int h = (int) d.getHeight();
        int s = (w < h ? w : h);
        // Pick the smallest of width/height and make both width and height the same size -> square
        return new Dimension(s, s);
    }


    // Force square dimensions by "redirecting" calls of getMaximumSize() and getMinimumSize() to getPreferredSize()
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public BufferedImage getBufferedImage(){
        /* this method creates a snapshot of the current doily and paints it onto a BufferedImage.
           This BufferedImage will be used to store the doilies in the gallery panel */
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        /* This call paints the doily data onto the buffered image */
        paint(g2d);

        return image;
    }

    private void drawDots(Stack<Dot> dots, Graphics g, boolean reflectDrawnPoints){
        // This function draws all the dots within ONE sector. The function is called once per sector, e.g. it is called 4 times if there are 4 sectors.
        for (Dot d: dots){
            g.setColor(d.getColor());
            int x = (int) (Math.cos(d.getPolar() * angleStep) * d.getDistFromCenter()) + centerPoint.x;
            int y = (int) (Math.sin(d.getPolar() * angleStep) * d.getDistFromCenter()) + centerPoint.y;
            x -= Math.cos(d.getPolar()) * (d.getRadius()/2);
            y -= Math.sin(d.getPolar()) * (d.getRadius()/2);
            g.fillOval(x, y, d.getRadius(), d.getRadius());

            if (reflectDrawnPoints){
                int x2 = (int) (Math.cos(angleStep * (1.0 - d.getPolar())) * d.getDistFromCenter()) + centerPoint.x;
                int y2 = (int) (Math.sin(angleStep * (1.0 - d.getPolar())) * d.getDistFromCenter()) + centerPoint.y;
                g.fillOval(x2, y2, d.getRadius(), d.getRadius());
            }
        }
    }

    public void undoDots(int undoOperations){
        // Undo function. Pops dots off the dots stack and pushes them onto the undoneDots stack.
        undoOperations = (undoOperations > dots.size()) ? dots.size() : undoOperations;
        for (int i = 0; i < undoOperations; i++){
            undoneDots.push(dots.pop());
        }
        repaint();
    }

    public void redo(int redoOperations){
        // Redo function. Pops previously pushed dots off the undoneDots stack back onto the dots stack so they can be drawn again.
        redoOperations = (redoOperations > undoneDots.size()) ? undoneDots.size() : redoOperations;
        for (int i = 0; i < redoOperations; i++){
            dots.push(undoneDots.pop());
        }
        repaint();
    }

    public void clearDisplay(){
        // Clears display. Resets dots and undo operations.
        this.dots.clear();
        this.undoneDots.clear();
        repaint();
    }

    private int getPenSize() {
        return penSize;
    }

    public Color getPenColor() {
        return penColor;
    }

    public void setDrawSectorLines(boolean drawSectorLines) {
        this.drawSectorLines = drawSectorLines;
    }

    public void setReflectDrawnPoints(boolean reflectDrawnPoints) {
        this.reflectDrawnPoints = reflectDrawnPoints;
    }

    public BufferedImage getPanelImageBuffer() {
        return panelImageBuffer;
    }

    public int getNumberOfSectors() {
        return numberOfSectors;
    }

    public void setPenSize(int penSize) {
        this.penSize = penSize;
    }

    public void setPenColor(Color penColor) {
        this.penColor = penColor;
    }

    public void setNumberOfSectors(int numberOfSectors) {
        // Sets the number of sectors and updates the angle between each sector.
        this.numberOfSectors = numberOfSectors;
        angleStep = 2*Math.PI/ numberOfSectors;
    }

    public void addDot(MouseEvent e){
        /* Centralise the adding of dots. Events are going to come from both the dragging and the clicking of the mouse */

        // Polar is the Theta part of a polar coordinate. Corresponds to the angle between the dot and the closest sector.
        double polar = ((Math.atan2(e.getY() - centerPoint.y, e.getX() - centerPoint.x) + Math.PI) % angleStep) / angleStep;// This is now a percentage of the section

        // centerDist is the radius part of a polar coordinate. Corresponds to the distance separating the dot from the center of the doily panel.
        int centerDist = (int) Math.sqrt(Math.pow(centerPoint.x - e.getX(), 2) + Math.pow(centerPoint.y - e.getY(), 2));

        // Create the dot using the previously calculated polar coordinates.
        Dot dot = new Dot(polar, centerDist, getPenSize(), getPenColor());

        // Depending on the current drawing mode (paint brush or eraser), add the new dot to the corresponding stack.
        if (erasing){
            // If we add it to the eraser stack, call updateErasing() to process the eraser dots.
            eraserDots.push(dot);
            updateErasing();
        } else {
            dots.push(dot);
        }

        // Whenever we add a dot, repaint the doily panel to make the changes visible to the user.
        repaint();
    }

    class DrawListener extends MouseAdapter{
        @Override
        public void mouseDragged(MouseEvent e) {
            addDot(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            addDot(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }
}
