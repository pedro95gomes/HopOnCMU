package pt.ulisboa.tecnico.cmu.response;

public class SignUpResponse implements Response {

    private static final long serialVersionUID = 734457624276534179L;
    private String username, message;
    private boolean register_success = true;

    public SignUpResponse(String username, String message) {
        this.username = username;
        this.message = message;

        if(message == null) { register_success = false; }
    }

    public String getUsername() {
        return this.username;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean getSuccess(){
        return this.register_success;
    }
}
