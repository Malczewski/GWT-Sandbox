package sandbox.client.widgets.fractal;

import org.vaadin.gwtgraphics.client.Line;

public class FractalUtils {

	public static double distance(Point p1, Point p2) {
		return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y));
	}

	public static double distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	public static double distance(Point p, Line line) {
		Point p0 = new Point(line.getX1(), line.getY1());
		Point p1 = new Point(line.getX2(), line.getY2());
		double up = (p0.y - p1.y) * p.x + (p1.x - p0.x) * p.y
				+ (p0.x * p1.y - p1.x * p0.y);
		double down = Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y)
				* (p1.y - p0.y));
		return Math.abs(up / down);
	}

	public static boolean between(Point p, Line line) {
		float x1 = Math.min(line.getX2(), line.getX1());
		float x2 = Math.max(line.getX2(), line.getX1());
		float y1 = Math.min(line.getY2(), line.getY1());
		float y2 = Math.max(line.getY2(), line.getY1());
		return (p.x < x2 && p.x > x1 || p.y < y2 && p.y > y1);
	}
	
	public static boolean isAtomic(Line line) {
		return (line.getX2() - line.getX1()) * (line.getX2() - line.getX1()) < 2
                && (line.getY2() - line.getY1()) * (line.getY2() - line.getY1()) < 2;
	}
	
	public static Line[] createShape(Shape shape, int width, int height) {
		switch (shape) {
		case Line:
			return new Line[] { 
					new Line(width / 6, height / 2, width * 5 / 6, height / 2) 
				};
		case Triangle:
			return new Line[] {
					new Line(width / 6, height * 5 / 6, 
							width / 2, height * 5 / 6 - (int)(Math.cos(Math.PI/6) * width * 2 / 3) ),
					new Line(width / 2, height * 5 / 6 - (int)(Math.cos(Math.PI/6) * width * 2 / 3), 
							width * 5 / 6, height * 5 / 6),
					new Line(width * 5 / 6, height * 5 / 6, width / 6, height * 5 / 6) 
				};
		case Rectangle:
			return new Line[] {
					new Line(width / 6, height / 6, width * 5 / 6, height / 6),
					new Line(width * 5 / 6, height / 6, width * 5 / 6, height * 5 / 6),
					new Line(width * 5 / 6, height * 5 / 6, width / 6, height * 5 / 6),
					new Line(width / 6, height * 5 / 6, width / 6, height / 6)
				};
		default:
			return null;
		}
	}

}
