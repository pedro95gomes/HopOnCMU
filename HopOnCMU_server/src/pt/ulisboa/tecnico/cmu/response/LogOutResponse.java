package pt.ulisboa.tecnico.cmu.response;

public class LogOutResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private String username;
    
    public LogOutResponse(String username){
    	this.username = username;
    }

    public String getUsername() {
    	return this.username;
    }

}
