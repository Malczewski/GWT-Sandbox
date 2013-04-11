package sandbox.client.widgets;

import java.util.ArrayList;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import sandbox.client.ApplicationContext;
import sandbox.client.widgets.automaton.NewsRule;
import sandbox.client.widgets.automaton.Color;
import sandbox.client.widgets.automaton.Rule;
import sandbox.client.widgets.rulewizard.RuleWizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class AutomatonWidget extends Composite {
	interface AutomatonWidgetUiBinder extends UiBinder<Widget, AutomatonWidget> {}
	private static AutomatonWidgetUiBinder uiBinder = GWT.create(AutomatonWidgetUiBinder.class);

	@UiField HTML title;
	@UiField TextBox sizeTextBox;
	@UiField TextBox scaleTextBox;
	@UiField Button startButton;
	@UiField CheckBox historyCheckBox;
	
	@UiField HTML stepLabel;
	@UiField Button oneStepButton;
	@UiField TextBox stepCount;
	@UiField Button multiStepButton;
	
	@UiField ScrollPanel content;
	@UiField Button chartButton;
	@UiField Button wizardButton;
	
	@UiField ListBox userModels;
	@UiField ListBox sharedModels;
	@UiField Button deleteButton;
	@UiField Button shareButton;
	
	@UiField DockLayoutPanel dockPanel;
	
	
	private ArrayList<Rule> userRules = new ArrayList<Rule>();
	private ArrayList<Rule> sharedRules = new ArrayList<Rule>();
	private Rule currentRule = new NewsRule();
	
	private DrawingArea area;
	private int size;
	private int scale;
	private Rectangle[][] rectangles;
	private int[][] values;
	private int[] counts;
	private ArrayList<int[]> history = new ArrayList<int[]>();
	private boolean isHistory;
	private int currentStep;
	
	MultiStepTimer steps;
	
	private Rule rule = null;//new AutomatonRule();
			/*new GenericRule("Life", false, 2, Arrays.asList(
			new RuleItem(Color.White, Color.Black, Color.Black, 2, 3),
			new RuleItem(Color.Black, Color.White, Color.Black, 0, 2),
			new RuleItem(Color.Black, Color.White, Color.Black, 4, 3)));
	*/
	public AutomatonWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		dockPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		chartButton.setEnabled(false);
		VisualizationUtils.loadVisualizationApi(new Runnable() {
			@Override
			public void run() {
				chartButton.setEnabled(true);
			}
		}, LineChart.PACKAGE);
		//refreshModels(null);
	}
	
	@UiHandler("fillButton")
	public void fillHandler(ClickEvent event) {
		if (rule == null)
			return;
		history.clear();
		currentStep = 0;
		steps = null;
		int[] colors = rule.getColors();
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				updateState(i, j, colors[Random.nextInt(colors.length)], false);
		updateHistory();
	}
	
	@UiHandler("startButton")
	public void startHandler(ClickEvent event) {
		setRule(currentRule);
		content.clear();
		history.clear();
		isHistory = historyCheckBox.getValue();
		chartButton.setEnabled(isHistory);
		if (area != null)
			area.clear();
		currentStep = 0;
		steps = null;
		try {
			size = Integer.parseInt(sizeTextBox.getValue());
			scale = Integer.parseInt(scaleTextBox.getValue());
			if (size <= 0 || scale <= 0)
				throw new IllegalArgumentException();
		} catch (Exception e) {
			MessageBox.show("Error", "Size and Scale must be a natural number.");
			return;
		}
		area = new DrawingArea(size * scale, size * scale);
		area.addDomHandler(new MouseDownHandler(){
            @Override
            public void onMouseDown(MouseDownEvent event){
                event.preventDefault();
                int i = event.getX() / scale;
				int j = event.getY() / scale;
				int button = event.getNativeButton() == NativeEvent.BUTTON_LEFT ?
						1 : event.getNativeButton() == NativeEvent.BUTTON_RIGHT ?
								-1 : 0;
				updateState(i, j, rule.getColor(values[i][j], button), false);
            }
        }, MouseDownEvent.getType());
		area.addDomHandler(new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
			}
		}, ContextMenuEvent.getType());

		fillArea();
		content.add(area);
		rule.setValues(values);
	}
	
	@UiHandler("oneStepButton")
	public void oneStepHandler(ClickEvent event) {
		oneStep();
	}
	
	@UiHandler("multiStepButton")
	public void multiStepHandler(ClickEvent event) {
		if (steps == null) {
			int count = 0;
			try {
				count = Integer.parseInt(stepCount.getValue());
			} catch (Exception e) {
				MessageBox.show("", "Count must be a number.");
				return;
			}
			multiStepButton.setText("Stop");
			oneStepButton.setEnabled(false);
			startButton.setEnabled(false);
			
			steps = new MultiStepTimer(count);
			steps.run();
		} else {
			steps.stop();
			steps = null;
		}
	}
	
	
	
	private void fillArea() {
		int color = rule.getColors()[0];
		rectangles = new Rectangle[size][size];
		values = new int[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++) {
				rectangles[i][j] = new Rectangle(i * scale, j * scale, scale, scale);
				rectangles[i][j].setFillColor(Color.values()[color].getColor());
				values[i][j] = color;
				area.add(rectangles[i][j]);
			}
		//updateState(size / 2, size / 2, maxValue() - 1);
		counts = new int[Color.values().length];
		counts[color] = size * size;
		//counts[maxValue() - 1] = 1;
		updateHistory();
	}
	
	private void oneStep() {
		rule.setValues(values);
		values = new int[size][size];
		int value;
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++) {
				value = rule.nextValue(i, j);
				if (value != rule.getValue(i, j)) {
					updateState(i, j, value, true);
					//counts[rule.getValue(i, j)]--;
					//counts[value]++;
				}
				else values[i][j] = value;
			}
		updateHistory();
		currentStep++;
		updateStep();
	}
	
	private void updateState(int i, int j, int value, boolean fromRule) {
		if (isHistory)
			counts[fromRule ? rule.getValue(i,j) : values[i][j]]--;
		values[i][j] = value;
		if (isHistory) 
			counts[value]++;
		rectangles[i][j].setFillColor(
				Color.values()[value].getColor());
	}
	
	private void updateStep() {
		stepLabel.setHTML("" + currentStep);
	}
	
	private void updateHistory() {
		if (!isHistory)
			return;
		int[] copy = new int[counts.length];
		for (int i = 0; i < counts.length; i++)
			copy[i] = counts[i];
		history.add(copy);
	}
	
	@UiHandler("chartButton")
	public void chartButtonHandler(ClickEvent event) {
		createChart();
	}
	
	@UiHandler("wizardButton")
	public void wizardButtonHandler(ClickEvent event) {
		createWizard(null);
	}
	
	private void createWizard(Rule rule) {
		DialogBox box = new DialogBox(false);
		box.setWidget(rule == null? new RuleWizard(box, this) : new RuleWizard(box, rule));
		box.show();
		box.center();
	}
	
	public void setRule(Rule rule) {
		this.currentRule = rule;
		this.rule = rule;
		title.setHTML(SafeHtmlUtils.fromString(rule.getName()));
	}
	
	private void createChart() {
		DataTable lineData = DataTable.create().cast();
		lineData.addColumn(ColumnType.NUMBER, "Step");
		int[] colors = rule.getColors();
		String[] sColors = new String[colors.length];
		for (int i = 0; i < colors.length; i++) {
			Color c;
			lineData.addColumn(ColumnType.NUMBER, (c = Color.values()[colors[i]]).name());
			sColors[i] = c.getColor();
		}
		lineData.addRows(history.size());
		for (int i = 0; i < history.size(); i++) {
			int[] count = history.get(i);
			lineData.setValue(i, 0, i + 1);
			for (int j = 0; j < colors.length; j++)
				lineData.setValue(i, j + 1, count[colors[j]]);
		}
		Options options = Options.create();
		options.setBackgroundColor("#eeeeee");
		options.setColors(sColors);
		options.setLegend(LegendPosition.BOTTOM);
		ChartArea area = ChartArea.create();
		area.setWidth(640);
		area.setHeight(480);
		options.setChartArea(area);
		LineChart chart = new LineChart(lineData, options);
		PopupPanel popup = new PopupPanel(true);
		popup.setWidget(chart);
		popup.center();
	    
	}
	
	public void refreshModels(final RunAsyncCallback callback) {
		ApplicationContext.get().getMainService().getRules(false, 
				new AsyncCallback<ArrayList<Rule>>() {
					@Override
					public void onSuccess(ArrayList<Rule> result) {
						userRules = result;
						userModels.clear();
						for (Rule rule : result)
							userModels.addItem(rule.getName(), "" + rule.hashCode());
						if (callback != null)
							callback.onSuccess();
					}
					@Override
					public void onFailure(Throwable caught) {
						MessageBox.show("Error", caught.getMessage());
						if (callback != null)
							callback.onFailure(caught);
					}
				});
		ApplicationContext.get().getMainService().getRules(true, 
				new AsyncCallback<ArrayList<Rule>>() {
					@Override
					public void onSuccess(ArrayList<Rule> result) {
						sharedRules = result;
						sharedModels.clear();
						for (Rule rule : result)
							sharedModels.addItem(rule.getName(), "" + rule.hashCode());
						if (callback != null)
							callback.onSuccess();
					}
					@Override
					public void onFailure(Throwable caught) {
						MessageBox.show("Error", caught.getMessage());
						if (callback != null)
							callback.onFailure(caught);
					}
				});	
	}
	
	@UiHandler("deleteButton")
	public void deleteButtonHandler(ClickEvent event) {
		if (userModels.getSelectedIndex() == -1)
			return;
		setButtons(false);
		ApplicationContext.get().getMainService().removeModel(
				userModels.getItemText(userModels.getSelectedIndex()), 
				new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						refreshModels(new RunAsyncCallback(){
							int i = 2;
							@Override
							public void onFailure(Throwable reason) {
								success();
							}
							@Override
							public void onSuccess() {
								success();
							}
							private void success() {
								if (--i <= 0)
									setButtons(true);
							}
						});
					}
					@Override
					public void onFailure(Throwable caught) {
						setButtons(true);
						MessageBox.show("Error", caught.getMessage());
					}
				});
	}
	
	@UiHandler("shareButton")
	public void shareButtonHandler(ClickEvent event) {
		if (userModels.getSelectedIndex() == -1)
			return;
		setButtons(false);
		ApplicationContext.get().getMainService().publishModel(
				userModels.getItemText(userModels.getSelectedIndex()), 
				new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						refreshModels(new RunAsyncCallback(){
							@Override
							public void onFailure(Throwable reason) {
								setButtons(true);
							}
							@Override
							public void onSuccess() {
								setButtons(true);
							}
						});
					}
					@Override
					public void onFailure(Throwable caught) {
						setButtons(true);
						MessageBox.show("Error", caught.getMessage());
					}
				});
	}
	
	private void setButtons(boolean enabled) {
		userModels.setEnabled(enabled);
		sharedModels.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
		shareButton.setEnabled(enabled);
	}
	
	@UiHandler("userModels")
	public void userModelsHandler(ChangeEvent event) {
		currentRule = findRule(userRules, userModels);
	}
	
	@UiHandler("sharedModels")
	public void sharedModelsHandler(ChangeEvent event) {
		currentRule = findRule(sharedRules, sharedModels);
	}
	
	@UiHandler("userModels")
	public void userModelsHandler(DoubleClickEvent event) {
		createWizard(findRule(userRules, userModels));
	}
	
	@UiHandler("sharedModels")
	public void sharedModelsHandler(DoubleClickEvent event) {
		createWizard(findRule(sharedRules, sharedModels));
	}
	
	private Rule findRule(ArrayList<Rule> rules, ListBox box) {
		String value = box.getValue(box.getSelectedIndex());
		for (Rule rule : rules)
			if (value.equals("" + rule.hashCode()))
				return rule;
		return null;
	}
	
	
	class MultiStepTimer extends Timer {

		private int stepsLeft;
		private boolean stop = false;
		
		public MultiStepTimer(int steps) {
			this.stepsLeft = steps;
		}
		
		@Override
		public void run() {
			if (stepsLeft > 0) {
				oneStep();
				stepsLeft--;
				if (!stop) 
					this.schedule(100);
			} else stop();
		}
		
		public void stop() {
			oneStepButton.setEnabled(true);
			startButton.setEnabled(true);
			multiStepButton.setText("Many Steps");
			stop = true;
			steps = null;
		}
	}
}
