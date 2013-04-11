package sandbox.client.service;

import java.util.ArrayList;

import sandbox.client.widgets.automaton.Rule;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface MainServiceAsync {
	void saveModel(Rule rule, AsyncCallback<Long> callback);
	void removeModel(String name, AsyncCallback<Void> callback);
	void getRules(boolean shared, AsyncCallback<ArrayList<Rule>> callback);
	void publishModel(String name, AsyncCallback<Void> callback);
}
