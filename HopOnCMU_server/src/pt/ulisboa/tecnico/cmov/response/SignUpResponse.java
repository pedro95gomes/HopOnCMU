package pt.ulisboa.tecnico.cmov.response;

public class SignUpResponse implements Response {

	private static final long serialVersionUID = 734457624276534179L;
	private String username, message;
	
	public SignUpResponse(String username, String message) {
		this.username = username;
		this.message = message;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getMessage() {
		return this.message;
	}
}
