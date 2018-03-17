import java.awt.*;

public class Dot {
    // Stores drawing attributes for every dot, along with polar coordinates.

    // Dot location
    private Point point;

    //Dot radius
    private int radius;

    // Dot colour
    private Color color;

    // Dot Theta -> angle between dot and closest sector (going anti-clockwise)
    private double polar;

    // Dot polar radius
    private int distFromCenter;


    public Dot(double polar, int distFromCenter, int radius, Color color){
        this.polar = polar;
        this.distFromCenter = distFromCenter;
        this.radius = radius;
        this.color = color;
    }

    public Dot(Dot dot){
        // Constructor to create a copy of a dot
        this.polar = dot.polar;
        this.distFromCenter = dot.distFromCenter;
        this.radius = dot.radius;
        this.color = dot.color;
    }

    public boolean overlapping(Dot otherDot){
        // Returns true if this dot and otherDot are overlapping
        double distance = Math.sqrt(Math.pow(this.distFromCenter, 2) + Math.pow(otherDot.distFromCenter, 2) - 2*this.distFromCenter*otherDot.distFromCenter*Math.cos(otherDot.polar - this.polar));
        return (distance < this.radius);
    }

    //Getters and setters

    public void setPolar(double polar) {
        this.polar = polar;
    }

    public double getPolar() {
        return polar;
    }

    public int getDistFromCenter() {
        return distFromCenter;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
