package sandbox.shared;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 3770054641585960677L;

	public ServiceException() {
		super();
	}
	
	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, Throwable caught) {
		super(message, caught);
	}
	
}
