package sandbox.client.widgets.automaton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Rule implements Serializable {

	private static final long serialVersionUID = 525135480838256158L;
	
	private String name = "";
	private LinkedList<int[]> neighbors = new LinkedList<int[]>();
	public ArrayList<Integer> colors = new ArrayList<Integer>();
	private LinkedHashMap<Color, LinkedList<RuleItem>> ruleMap = new LinkedHashMap<Color, LinkedList<RuleItem>>();
	
	private boolean square;
	private int radius;
	
	private int size;
	private int[][] values;
	
	protected Rule() {}
	
	public Rule(String name,boolean square, int radius, List<RuleItem> rules) {
		this.name = name;
		this.square = square;
		this.radius = radius;
		for (RuleItem rule : rules) {
			if (ruleMap.get(rule.getColorFrom()) == null)
				ruleMap.put(rule.getColorFrom(), new LinkedList<RuleItem>());
			ruleMap.get(rule.getColorFrom()).add(rule);
			if (!colors.contains(rule.getColorFrom().ordinal()))
				colors.add(rule.getColorFrom().ordinal());
			if (!colors.contains(rule.getColorTo().ordinal()))
				colors.add(rule.getColorTo().ordinal());
			if (!colors.contains(rule.getColorWhat().ordinal()))
				colors.add(rule.getColorWhat().ordinal());
		}
		
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if (i == 0 && j == 0)
					continue;
				if (square)
					neighbors.add(new int[]{i,j});
				else if (length(i,j) <= radius)
					neighbors.add(new int[]{i,j});
			}
		}
	}
	
	public void setValues(int[][] values) {
		this.values = values;
		this.size = values.length;
	}
	
	public int nextValue(int i, int j) {
		List<RuleItem> items = ruleMap.get(Color.values()[values[i][j]]);
		if (items == null)
			return values[i][j];
		for (RuleItem item : items) {
			if (item.check(count(item.getColorWhat().ordinal(), i, j)))
				return item.getColorTo().ordinal();
		}
		return values[i][j];
	}

	public int getValue(int i, int j) {
		return values[i][j];
	}
	
	public int[] getColors() {
		int[] array = new int[colors.size()];
		for (int i = 0; i < colors.size(); i++)
			array[i] = colors.get(i);
		return array;
	}

	private double length(int x, int y) {
		return Math.sqrt(1.0 * (x * x + y * y));
	}
	
	private int count(int color, int i, int j) {
		int count = 0;
		for (int[] k : neighbors)
			if (values[getRow(i, k)][getCol(j, k)] == color)
				count++;
		return count;
	}

	private int getRow(int i, int[] k) {
		int res = i + k[0];
		if (res < 0)
			res = size - 1;
		if (res >= size)
			res = 0;
		return res;
	}

	private int getCol(int j, int[] k) {
		int res = j + k[1];
		if (res < 0)
			res = size - 1;
		if (res >= size)
			res = 0;
		return res;
	}

	public int getColor(int color, int direction) {
		int index = -1;
		for (int i = 0; i < colors.size(); i++)
			if (colors.get(i).intValue() == color) {
				index = i;
				break;
			}
		if (index == -1)
			throw new IllegalArgumentException("Wrong color");
		index += direction;
		if (index < 0)
			return colors.get(colors.size() - 1);
		if (index >= colors.size())
			return colors.get(0);
		return colors.get(index);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isSquare() {
		return square;
	}

	public int getRadius() {
		return radius;
	}

	public LinkedHashMap<Color, LinkedList<RuleItem>> getRuleMap() {
		return ruleMap;
	}

	public void setRuleMap(LinkedHashMap<Color, LinkedList<RuleItem>> ruleMap) {
		this.ruleMap = ruleMap;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + radius;
		result = prime * result + ((ruleMap == null) ? 0 : ruleMap.hashCode());
		result = prime * result + (square ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Rule))
			return false;
		Rule other = (Rule) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (radius != other.radius)
			return false;
		if (ruleMap == null) {
			if (other.ruleMap != null)
				return false;
		} else if (!ruleMap.equals(other.ruleMap))
			return false;
		if (square != other.square)
			return false;
		return true;
	}
	
	
}
