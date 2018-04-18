package pt.ulisboa.tecnico.cmu.response;

public class HelloResponse implements Response {

	private static final long serialVersionUID = 734457624276534179L;
	private String message;
	
	public HelloResponse(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
