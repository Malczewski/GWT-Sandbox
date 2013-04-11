package sandbox.client.widgets.automaton;

import com.google.gwt.user.client.Random;

public class NewsRule extends Rule {

	private static final long serialVersionUID = -624300959032698122L;
	
	private static int[][] array = { { -1, -1 }, { -1, 0 }, { -1, 1 },
			{ 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
	private int[][] values;
	private int size;

	public NewsRule() {
		setName("News");
	}

	public int nextValue(int i, int j) {
		if (values[i][j] == 0)
			return forWhite(i, j);
		else return forGrayAndBlack(i, j);
	}
	
	public int getValue(int i, int j) {
		return values[i][j];
	}
	
	public void setValues(int[][] values) {
		this.values = values;
		this.size = values.length;
	}

	private int forWhite(int i, int j) {
		int neighbors = 0;
		for (int k = 0; k < 8; k++)
			if (values[getRow(i, k)][getCol(j, k)] == 2)
				neighbors++;
		double probability = Random.nextDouble();
		double res;
		if (neighbors <= 2)
			res = 1.5 * probability * neighbors;
		else
			res = probability * neighbors;
		return res > 1.0 ? 2 : 0;
	}

	private int forGrayAndBlack(int i, int j) {
		int neighbors = 0;
		for (int k = 0; k < 8; k++)
			if (values[getRow(i, k)][getCol(j, k)] > 0)
				neighbors++;
		return neighbors == 8 ? values[i][j] - 1 : values[i][j];
	}

	private int getRow(int i, int k) {
		int res = i + array[k][0];
		if (res < 0)
			res = size - 1;
		if (res >= size)
			res = 0;
		return res;
	}

	private int getCol(int j, int k) {
		int res = j + array[k][1];
		if (res < 0)
			res = size - 1;
		if (res >= size)
			res = 0;
		return res;
	}
	
	public int[] getColors() {
		return new int[]{0,1,2};
	}

	@Override
	public int getColor(int color, int direction) {
		color += direction;
		if (color > 2)
			color = 0;
		if (color < 0)
			color = 2;
		return color;
	}
	
	@Override
	public String toString() {
		return "Default Model";
	}
}
