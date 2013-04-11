package sandbox.server.pojo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import sandbox.client.widgets.automaton.RuleItem;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class RuleObject implements Serializable {

	private static final long serialVersionUID = 525135480838256158L;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent(serialized = "true")
	private String name;
	
	@Persistent(serialized = "true")
	private boolean square;
	
	@Persistent(serialized = "true")
	private int radius;
	
	@Persistent(serialized = "true")
	private List<RuleItem> rules = new LinkedList<RuleItem>();

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RuleItem> getRules() {
		return rules;
	}

	public void setRules(List<RuleItem> rules) {
		this.rules = rules;
	}

	public boolean isSquare() {
		return square;
	}

	public void setSquare(boolean square) {
		this.square = square;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
}
