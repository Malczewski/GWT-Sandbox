package sandbox.client.widgets;

import java.util.ArrayList;
import java.util.LinkedList;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;

import sandbox.client.ApplicationContext;
import sandbox.client.widgets.fractal.FractalUtils;
import sandbox.client.widgets.fractal.Modifier;
import sandbox.client.widgets.fractal.Point;
import sandbox.client.widgets.fractal.Shape;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FractalWidget extends Composite {
	interface AutomatonWidgetUiBinder extends UiBinder<Widget, FractalWidget> {}
	private static AutomatonWidgetUiBinder uiBinder = GWT.create(AutomatonWidgetUiBinder.class);

	@UiField HTML title;
	@UiField Button ruleButton;
	@UiField Button startButton;
	
	@UiField HTML stepLabel;
	@UiField Button oneStepButton;
	@UiField TextBox stepCount;
	@UiField Button multiStepButton;
	
	@UiField CheckBox reverseCheckBox;
	@UiField CheckBox gridCheckBox;
	
	@UiField ListBox shapeList;
	
	@UiField ScrollPanel content;
	
	@UiField ListBox userModels;
	@UiField ListBox sharedModels;
	@UiField Button deleteButton;
	@UiField Button shareButton;
	
	@UiField DockLayoutPanel dockPanel;
	
	
	//private ArrayList<Rule> userRules = new ArrayList<Rule>();
	//private ArrayList<Rule> sharedRules = new ArrayList<Rule>();
	//private Rule currentRule = new NewsRule();
	
	private final int RADIUS = 20;
	private static final int GRID = 10;
	
	private DrawingArea area;
	private LinkedList<Line> baseLines;
	private ArrayList<Line> lines;
	private Line edit1, edit2;
	private int currentStep;
	
	MultiStepTimer steps;
	
	private Modifier rule;

	public FractalWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		dockPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		//refreshModels(null);
		for (Shape shape : Shape.values())
			shapeList.addItem(shape.name());
		shapeList.setSelectedIndex(0);
		
		baseLines = new LinkedList<Line>();
		
		initArea();
		setControls(false);
	}
	
	private void initArea() {
		area = new DrawingArea(content.getElement().getClientWidth(), 
				content.getElement().getClientHeight());
		
		area.addDomHandler(new MouseDownHandler(){
            @Override
            public void onMouseDown(MouseDownEvent event){
                event.preventDefault();
            }
        }, MouseDownEvent.getType());
		area.addDomHandler(new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
			}
		}, ContextMenuEvent.getType());
		area.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (currentStep != 0 || baseLines.isEmpty())
					return;
				
				boolean newPoint = true;
				Line l = baseLines.get(0);
				if (FractalUtils.distance(l.getX1(), l.getY1(), event.getX(), event.getY()) < RADIUS) {
					edit1 = null;
					edit2 = l;
					newPoint = false;
				} else
					for (int i = 1; i < baseLines.size(); i++) {
						l = baseLines.get(i);
						if (FractalUtils.distance(l.getX1(), l.getY1(), event.getX(), event.getY()) < RADIUS) {
							edit1 = baseLines.get(i - 1);
							edit2 = baseLines.get(i);
							newPoint = false;
							break;
						}
					}
				if (newPoint) {
					l = baseLines.get(baseLines.size() - 1);
					if (FractalUtils.distance(l.getX1(), l.getY1(), event.getX(), event.getY()) < RADIUS) {
						edit1 = l;
						edit2 = null;
						newPoint = false;
					}
				}
				
				// if we move old point
				if (!newPoint) {
					return;
				}
				
				Point point = new Point(event.getX(), event.getY());
				double min = Double.MAX_VALUE;
                int k = -1;
                for (int i = 0; i < baseLines.size(); i++)
                {
                    Line line = (Line)baseLines.get(i);
                    if (!FractalUtils.between(point, line))
                        continue;
                    double dist = FractalUtils.distance(point, line);
                    if (dist < min)
                    {
                        min = dist;
                        k = i;
                    }
                }
                if (k == -1)
                    return;
                edit1 = baseLines.get(k);
                edit2 = new Line(point.x, point.y, edit1.getX2(), edit1.getY2());
                edit1.setX2(point.x);
                edit1.setY2(point.y);
                baseLines.add(edit2);
                area.add(edit2);
			}
		});
		area.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				if (currentStep != 0)
					return;
				edit1 = null;
				edit2 = null;
			}
		});
		area.addMouseMoveHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (currentStep != 0)
					return;
				int x = event.getX();
				int y = event.getY();
				if (edit1 != null){
					edit1.setX2(x);
					edit1.setY2(y);
				}
				if (edit2 != null){
					edit2.setX1(x);
					edit2.setY1(y);
				}
			}
		});
		
		
		content.add(area);
	}
	
	@UiHandler("ruleButton")
	public void ruleHandler(ClickEvent event) {
		currentStep = 0;
		updateStep();
		steps = null;
		clearArea(true);
		setControls(false);
		startButton.setText("Start");
	}
	
	@UiHandler("startButton")
	public void clearHandler(ClickEvent event) {
		startButton.setText("Clear");
		currentStep = 0;
		updateStep();
		steps = null;

		rule = new Modifier(baseLines.toArray(new Line[0]));
		clearArea(false);
		setControls(true);
	}
	
	private void clearArea(boolean rule) {
		lines = null;
		area.clear();
		area.setHeight(content.getElement().getClientHeight() - 10);
		area.setWidth(content.getElement().getClientWidth() - 10);
		baseLines.clear();

		int width = area.getWidth();
		int height = area.getHeight();
		
		Line[] lines = FractalUtils.createShape(rule ? Shape.Line 
				: Shape.valueOf(shapeList.getItemText(shapeList.getSelectedIndex())),
				width, height);
		
		for (Line line : lines) {
			area.add(line);
			baseLines.add(line);
		}
		
		if (gridCheckBox.getValue()) {
			int cellWidth = width / GRID;
			int cellHeight = height / GRID;
			for (int i = 0; i < GRID; i++) {
				Line l = new Line(0, (i+1) * cellHeight + 1, width,(i+1) * cellHeight + 1);
				l.setStrokeColor("#cccccc");
				area.add(l);
				l = new Line((i+1) * cellWidth + 1, 0, (i+1) * cellWidth + 1,height);
				l.setStrokeColor("#cccccc");
				area.add(l);
			}
		}
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
	
	
	
	private void oneStep() {
		if (lines == null)
			lines = new ArrayList<Line>(baseLines);
		ArrayList<Line> newLines = new ArrayList<Line>(rule.getCount());
		boolean reverse = reverseCheckBox.getValue();
		for (Line line : lines) {
			if (!FractalUtils.isAtomic(line))
				rule.modify(line, newLines, reverse);
			else newLines.add(line);
		}
		lines.clear();
		lines = newLines;
		area.clear();
		for (Line line : lines)
			area.add(line);
		currentStep++;
		updateStep();
	}
	
	private void updateStep() {
		stepLabel.setHTML("" + currentStep);
	}
	
	/*public void refreshModels(final RunAsyncCallback callback) {
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
	}*/
	
	/*@UiHandler("deleteButton")
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
	}*/
	
	/*@UiHandler("shareButton")
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
	*/
	
	private void setControls(boolean enabled) {
		oneStepButton.setEnabled(enabled);
		multiStepButton.setEnabled(enabled);
	}
	
	private void setButtons(boolean enabled) {
		userModels.setEnabled(enabled);
		sharedModels.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
		shareButton.setEnabled(enabled);
	}
	
	@UiHandler("userModels")
	public void userModelsHandler(ChangeEvent event) {
		//currentRule = findRule(userRules, userModels);
	}
	
	@UiHandler("sharedModels")
	public void sharedModelsHandler(ChangeEvent event) {
		//currentRule = findRule(sharedRules, sharedModels);
	}
	
	@UiHandler("userModels")
	public void userModelsHandler(DoubleClickEvent event) {
		//createWizard(findRule(userRules, userModels));
	}
	
	@UiHandler("sharedModels")
	public void sharedModelsHandler(DoubleClickEvent event) {
		//createWizard(findRule(sharedRules, sharedModels));
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
