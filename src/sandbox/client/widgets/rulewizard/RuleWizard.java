package sandbox.client.widgets.rulewizard;

import sandbox.client.ApplicationContext;
import sandbox.client.widgets.AutomatonWidget;
import sandbox.client.widgets.MessageBox;
import sandbox.client.widgets.automaton.Rule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class RuleWizard extends Composite {

	private static RuleWizardUiBinder uiBinder = GWT
			.create(RuleWizardUiBinder.class);
	@UiField HorizontalPanel titlePanel;
	@UiField SimplePanel contentPanel;
	@UiField HorizontalPanel buttonPanel;
	@UiField Button finishButton;
	@UiField Button cancelButton;

	interface RuleWizardUiBinder extends UiBinder<Widget, RuleWizard> {
	}

	private DialogBox parent;
	private AutomatonWidget widget;
	private StepOne step;
	
	public RuleWizard(DialogBox parent, AutomatonWidget widget) {
		this.parent = parent;
		this.widget = widget;
		initWidget(uiBinder.createAndBindUi(this));
		contentPanel.setWidget(step = new StepOne());
		finishButton.setEnabled(true);
	}
	
	public RuleWizard(DialogBox parent, Rule rule) {
		this.parent = parent;
		initWidget(uiBinder.createAndBindUi(this));
		contentPanel.setWidget(step = new StepOne(rule));
		finishButton.setEnabled(false);
	}

	@UiHandler("finishButton")
	void onFinishButtonClick(ClickEvent event) {
		String msg = step.validate();
		if (msg == null) {
			finishButton.setEnabled(false);
			final Rule rule = step.getRule();
			ApplicationContext.get().getMainService().saveModel(rule, 
					new AsyncCallback<Long>() {
						@Override
						public void onSuccess(Long result) {
							widget.setRule(rule);
							widget.startHandler(null);
							parent.hide();
							widget.refreshModels(null);
						}
						@Override
						public void onFailure(Throwable caught) {
							MessageBox.show("Error", caught.getMessage());
							finishButton.setEnabled(true);
						}
					});
		}
		else MessageBox.show("Error", msg);
	}
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		parent.hide();
	}
}
