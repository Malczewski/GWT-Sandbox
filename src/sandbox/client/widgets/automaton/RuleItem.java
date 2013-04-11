package sandbox.client.widgets.automaton;

import java.io.Serializable;

import com.google.gwt.user.client.Random;

public class RuleItem implements Serializable {
	
	private static final long serialVersionUID = 8619666643828245031L;

	public static final String[] operations = new String[]{"<","<=","=",">=",">"};
	
	private Color colorFrom;
	private Color colorTo;
	private Color colorWhat;
	private int operation;
	private int value;
	private double randomValue;
	private int operation2;
	private int value2;
	
	protected RuleItem() {
	}
	
	public RuleItem(Color from, Color to, Color what, int operation, int value,
			double randomValue, int operation2, int value2) {
		colorFrom = from;
		colorTo = to;
		colorWhat = what;
		this.operation = operation;
		this.value = value;
		this.randomValue = randomValue;
		this.operation2 = operation2;
		this.value2 = value2;
	}
	
	public boolean check(int compareValue) {
		return checkOperation(compareValue, operation, value) &&
				checkOperation(compareValue * Random.nextDouble() * randomValue, operation2, value2);
	}
	
	private boolean checkOperation(double compareValue, int operation, int value) {
		switch (operation) {
		case 0: return compareValue < value;
		case 1: return compareValue <= value;
		case 2: return compareValue == value;
		case 3: return compareValue >= value;
		case 4: return compareValue > value;
		default: throw new IllegalArgumentException("Wrong operation");
		}
	}

	public Color getColorFrom() {
		return colorFrom;
	}

	public Color getColorTo() {
		return colorTo;
	}

	public Color getColorWhat() {
		return colorWhat;
	}
	
	@Override
	public String toString() {
		return  colorFrom + " -> " + colorTo
				+ " if number(" + colorWhat + ") "
				+ operations[operation] + " " + value 
				+ ((randomValue > 1E-10) ?
						" and number(" + colorWhat + ")*random(" 
						+ randomValue + ") " + operations[operation2] + " " + value2
						: "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((colorFrom == null) ? 0 : colorFrom.hashCode());
		result = prime * result + ((colorTo == null) ? 0 : colorTo.hashCode());
		result = prime * result
				+ ((colorWhat == null) ? 0 : colorWhat.hashCode());
		result = prime * result + operation;
		result = prime * result + operation2;
		long temp;
		temp = Double.doubleToLongBits(randomValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + value;
		result = prime * result + value2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RuleItem))
			return false;
		RuleItem other = (RuleItem) obj;
		if (colorFrom != other.colorFrom)
			return false;
		if (colorTo != other.colorTo)
			return false;
		if (colorWhat != other.colorWhat)
			return false;
		if (operation != other.operation)
			return false;
		if (operation2 != other.operation2)
			return false;
		if (Double.doubleToLongBits(randomValue) != Double
				.doubleToLongBits(other.randomValue))
			return false;
		if (value != other.value)
			return false;
		if (value2 != other.value2)
			return false;
		return true;
	}
}
