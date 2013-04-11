package sandbox.client.service;

import java.util.ArrayList;

import sandbox.client.widgets.automaton.Rule;
import sandbox.shared.ServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("main")
public interface MainService extends RemoteService {
	
	long saveModel(Rule rule) throws ServiceException;
	ArrayList<Rule> getRules(boolean shared);
	void publishModel(String name) throws ServiceException;
	void removeModel(String name) throws ServiceException;
}
