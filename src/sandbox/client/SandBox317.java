package sandbox.client;

import sandbox.client.widgets.MainWidget;
import sandbox.client.widgets.MessageBox;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SandBox317 implements EntryPoint {

	
	public void onModuleLoad() {
		boolean debug = false;
		if (debug) {
			GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				
				@Override
				public void onUncaughtException(Throwable e) {
					MessageBox.show("Error", e.getMessage());
				}
			});
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				RootPanel.get().add(new MainWidget());
			}
		});
	}
}
