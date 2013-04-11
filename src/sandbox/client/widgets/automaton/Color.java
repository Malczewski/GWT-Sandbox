package sandbox.client.widgets.automaton;

public enum Color {
	
	White("#FFFFFF"), Gray("#808080"), Black("#000000"),
	LightGray("#c0c0c0"), DarkGray("#404040"), 
	Red("#FF0000"), Pink("#FF00FF"),
	Orange("#FFA500"), Yellow("#FFFF00"),
	Green("#00FF00"), Magenta("#FF00FF"),
	Cyan("#00FFFF"), Blue("#0000FF");

	private String color;
	
	private Color(String color) {
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
}
