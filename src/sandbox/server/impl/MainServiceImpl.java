package sandbox.server.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import sandbox.client.service.MainService;
import sandbox.client.widgets.automaton.Rule;
import sandbox.client.widgets.automaton.RuleItem;
import sandbox.server.helpers.Serializator;
import sandbox.server.pojo.RuleObject;
import sandbox.shared.ServiceException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Text;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MainServiceImpl extends RemoteServiceServlet implements MainService {

	private static final long serialVersionUID = -8679132554685578489L;
	
	private final static String RULE_KIND = "Rules";
	
	@Override
	public long saveModel(Rule rule) throws ServiceException {
		String sessionId = getSessionId();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		List<Filter> filters = new ArrayList<Filter>(2);
		filters.add(new Query.FilterPredicate("sessionId", FilterOperator.EQUAL, sessionId));
		filters.add(new Query.FilterPredicate("name", FilterOperator.EQUAL, rule.getName()));
		
		Query query = new Query(RULE_KIND)
		.setFilter(new Query.CompositeFilter(CompositeFilterOperator.AND, filters));
		if (datastore.prepare(query)
				.asList(FetchOptions.Builder.withDefaults()).isEmpty()) {
			Entity model = new Entity(RULE_KIND);
			model.setProperty("sessionId", sessionId);
			model.setProperty("shared", false);
			model.setProperty("name", rule.getName());
			try {
				model.setProperty("rule",
						new Text(Serializator.toString(serialize(rule))));
				return datastore.put(model).getId();
			} catch (Exception e) {
				log(e.toString(), e);
				throw new ServiceException("Database error.");
			}
		} else
			throw new ServiceException("Model with this name already exists");
	}
	
	@Override
	public void removeModel(String name) throws ServiceException {
		String sessionId = getSessionId();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		List<Filter> filters = new ArrayList<Filter>(2);
		filters.add(new Query.FilterPredicate("sessionId", FilterOperator.EQUAL, sessionId));
		filters.add(new Query.FilterPredicate("name", FilterOperator.EQUAL, name));
		
		Query query = new Query(RULE_KIND)
			.setFilter(new Query.CompositeFilter(CompositeFilterOperator.AND, filters));
		
		Entity model = datastore.prepare(query).asSingleEntity();
		if (model == null)
			throw new ServiceException("Model not found.");
		datastore.delete(model.getKey());
	}
	
	@Override
	public void publishModel(String name) throws ServiceException {
		String sessionId = getSessionId();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		List<Filter> filters = new ArrayList<Filter>(2);
		filters.add(new Query.FilterPredicate("sessionId", FilterOperator.EQUAL, sessionId));
		filters.add(new Query.FilterPredicate("name", FilterOperator.EQUAL, name));
		
		Query query = new Query(RULE_KIND)
			.setFilter(new Query.CompositeFilter(CompositeFilterOperator.AND, filters));
		
		Entity model = datastore.prepare(query).asSingleEntity();
		if (model == null)
			throw new ServiceException("Model not found.");
		model.setProperty("shared", true);
		datastore.put(model);
	}

	@Override
	public ArrayList<Rule> getRules(boolean shared) {
		Filter filter = shared ? new Query.FilterPredicate("shared", FilterOperator.EQUAL, true)
			: new Query.FilterPredicate("sessionId", FilterOperator.EQUAL, getSessionId());
		
		Query query = new Query(RULE_KIND)
			.setFilter(filter);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		QueryResultIterator<Entity> iterator = datastore.prepare(query).asQueryResultIterator();
		
		ArrayList<Rule> result = new ArrayList<Rule>();
		while (iterator.hasNext()) 
			try {
				result.add(deserialize(Serializator.fromString(
						((Text)iterator.next().getProperty("rule")).getValue())));
			} catch (Exception e) {
				log(e.toString(), e);
			}
		return result;
	}
	
	private String getSessionId() {
		HttpServletRequest request = this.getThreadLocalRequest();
		return request.getSession().getId();
	}
	
	private Rule deserialize(Object object) {
		RuleObject rule = (RuleObject) object;
		return new Rule(rule.getName(), rule.isSquare(), rule.getRadius(), rule.getRules());
	}
	
	private RuleObject serialize(Rule rule) {
		RuleObject object = new RuleObject();
		object.setName(rule.getName());
		object.setRadius(rule.getRadius());
		object.setSquare(rule.isSquare());
		List<RuleItem> rules = new ArrayList<RuleItem>();
		for (LinkedList<RuleItem> list : rule.getRuleMap().values())
			rules.addAll(list);
		object.setRules(rules);
		return object;
	}
	
}
