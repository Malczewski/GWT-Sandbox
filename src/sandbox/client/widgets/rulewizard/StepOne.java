package sandbox.client.widgets.rulewizard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import sandbox.client.widgets.automaton.Color;
import sandbox.client.widgets.automaton.Rule;
import sandbox.client.widgets.automaton.RuleItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.DoubleBox;

public class StepOne extends Composite {
	
	private static StepOneUiBinder uiBinder = GWT.create(StepOneUiBinder.class);
	@UiField RadioButton rbRound;
	@UiField RadioButton rbSquare;
	@UiField IntegerBox radius;
	@UiField TextBox modelName;
	@UiField ListBox colorFrom;
	@UiField ListBox colorTo;
	@UiField ListBox colorWhat;
	@UiField ListBox binaryOperation;
	@UiField Button addButton;
	@UiField Button removeButton;
	@UiField IntegerBox valueField;
	@UiField ListBox ruleTable;
	
	@UiField DoubleBox randomValue;
	@UiField ListBox binaryOperation2;
	@UiField IntegerBox valueField2;
	
	private Color[] colors = Color.values();
	private Map<Long, RuleItem> rules = new LinkedHashMap<Long, RuleItem>();
	private long current = 0;

	interface StepOneUiBinder extends UiBinder<Widget, StepOne> {
	}

	public StepOne() {
		initWidget(uiBinder.createAndBindUi(this));
		fillOperations();
		fillColors(colorFrom);
		fillColors(colorTo);
		fillColors(colorWhat);
	}
	
	public StepOne(Rule rule) {
		initWidget(uiBinder.createAndBindUi(this));
		
		disable(rbRound);disable(rbSquare);disable(radius);disable(modelName);
		disable(colorFrom);disable(colorTo);disable(colorWhat);disable(binaryOperation);
		disable(addButton);disable(removeButton);disable(valueField);disable(randomValue);
		disable(binaryOperation2);disable(valueField2);disable(ruleTable);
		
		modelName.setText(rule.getName());
		rbRound.setValue(!rule.isSquare());
		rbSquare.setValue(rule.isSquare());
		radius.setText(rule.getRadius() + "");
		for (LinkedList<RuleItem> itemList : rule.getRuleMap().values())
			for (RuleItem item : itemList)
				ruleTable.addItem(item.toString());
		
	}
	
	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
		Integer value = valueField.getValue();
		Integer value2 = valueField2.getValue();
		Double random = randomValue.getValue();
		if (value == null || value2 == null || random == null) {
			Window.alert("Bad number format");
			return;
		}
		RuleItem item = new RuleItem(getColor(colorFrom), 
				getColor(colorTo), getColor(colorWhat), 
				getValue(binaryOperation), value, 
				random, getValue(binaryOperation2), value2);
		rules.put(++current, item);
		ruleTable.addItem(item.toString(), ""+current);
	}
	@UiHandler("removeButton")
	void onRemoveButtonClick(ClickEvent event) {
		if (ruleTable.getSelectedIndex() != -1) {
			Long value = Long.valueOf(getValue(ruleTable));
			ruleTable.removeItem(ruleTable.getSelectedIndex());
			rules.remove(value);
		}
	}
	
	private int getValue(ListBox list) {
		return Integer.valueOf(list.getValue(list.getSelectedIndex()));
	}
	
	private Color getColor(ListBox list) {
		return colors[Integer.valueOf(list.getValue(list.getSelectedIndex()))];
	}
	
	private void fillOperations() {
		for (int i = 0; i < RuleItem.operations.length; i++) {
			binaryOperation.addItem(RuleItem.operations[i], ""+i);
			binaryOperation2.addItem(RuleItem.operations[i], ""+i);
		}
		binaryOperation2.setSelectedIndex(0);
	}
	
	private void fillColors(ListBox list) {
		for (Color c : Color.values())
			list.addItem(c.name(), ""+c.ordinal());
	}
	
	public Rule getRule() {
		return new Rule(modelName.getText(), rbSquare.getValue(), radius.getValue(), 
				new ArrayList<RuleItem>(rules.values()));
	}
	
	public String validate() {
		if (radius.getValue() == null)
			return "Radius cannot be empty";
		if (modelName.getText() == null || modelName.getText().trim().equals(""))
			return "Model name cannot be empty";
		if (rules.isEmpty())
			return "You should create at least one rule";
		return null;
	}
	
	public void disable(HasEnabled w){
		w.setEnabled(false);
	}
}
