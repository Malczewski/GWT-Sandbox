package sandbox.client.widgets.fractal;

import org.vaadin.gwtgraphics.client.Line;

public class FractalRule {

        private double angle;
        private double length;

        public FractalRule(Line line, double baseLength, double baseAngle)
        {
            Point vector = new Point(line.getX2() - line.getX1(), line.getY2() - line.getY1());
            length = Math.sqrt(vector.x * vector.x + vector.y * vector.y) / baseLength;
            angle = Math.atan(1.0 * vector.y / vector.x) + baseAngle;

            if (vector.y < 0 && vector.x < 0)
                angle = -Math.PI + angle;
            else if (vector.x < 0)
                angle = -Math.PI + angle;
        }

        public Point modify(Point start, double currLength, double angleShift)
        {
            return new Point(start.x + (int)(Math.cos(angle + angleShift) * length * currLength),
                start.y + (int)(Math.sin(angle + angleShift) * length * currLength));
        }
}
