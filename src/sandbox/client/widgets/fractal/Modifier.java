package sandbox.client.widgets.fractal;

import java.util.ArrayList;

import org.vaadin.gwtgraphics.client.Line;

public class Modifier
{
    private FractalRule[] rules;

    public Modifier(Line[] lines)
    {
        rules = new FractalRule[lines.length];
        Point vector = new Point(lines[lines.length - 1].getX2() - lines[0].getX1(),
        		lines[lines.length - 1].getY2() - lines[0].getY1());
        double baseLength = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        double baseAngle = Math.atan(-1.0 * vector.y / vector.x);
        if (vector.y < 0 && vector.x < 0)
            baseAngle = -Math.PI + baseAngle;
        else if (vector.x < 0)
            baseAngle = -Math.PI + baseAngle;
        for (int i = 0; i < lines.length; i++)
            rules[i] = new FractalRule(lines[i], baseLength, baseAngle);
    }

    public void modify(Line line, ArrayList<Line> dest, boolean reverse)
    {
    	Point vector = new Point(line.getX2() - line.getX1(), line.getY2() - line.getY1());
        double currLength = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        double angleShift = Math.atan(1.0 * vector.y / vector.x);
        if (vector.y < 0 && vector.x < 0)
            angleShift = -Math.PI + angleShift;
        else if (vector.x < 0)
            angleShift = -Math.PI + angleShift;
        Point curr = new Point(line.getX1(), line.getY1());
        for (int i = 0; i < rules.length; i++)
        {
        	Point newPoint = rules[i].modify(curr, currLength, angleShift);
            if (reverse && i % 2 == 0) dest.add(new Line(newPoint.x, newPoint.y, curr.x, curr.y));
            else dest.add(new Line(curr.x, curr.y, newPoint.x, newPoint.y));
            curr = newPoint;
        }
    }

    public int getCount()
    {
        return rules.length;
    }
}
