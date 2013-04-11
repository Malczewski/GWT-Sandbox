package sandbox.client;

import sandbox.client.service.MainService;
import sandbox.client.service.MainServiceAsync;

import com.google.gwt.core.client.GWT;

public class ApplicationContext {
	
	private static ApplicationContext instance = new ApplicationContext();
	
	private MainServiceAsync mainService = GWT.create(MainService.class);
	
	private ApplicationContext() {}
	
	public static ApplicationContext get() {
		return instance;
	}
	
	public MainServiceAsync getMainService() {
		return mainService;
	}
}
