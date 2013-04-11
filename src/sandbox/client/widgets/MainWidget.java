package sandbox.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainWidget extends Composite {
	
	interface MainWidgetUiBinder extends UiBinder<Widget, MainWidget> {}
	private static MainWidgetUiBinder uiBinder = GWT.create(MainWidgetUiBinder.class);
	
	public MainWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
