package pt.ulisboa.tecnico.cmu.response;

public class LogOutResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private String sessionId;
    private String username;
    
    public LogOutResponse(String ssid, String username){
    	this.sessionId = ssid;
    	this.username = username;
    }

    public String getSessionId() {
        return this.sessionId;
    }
    
    public String getUserName() {
        return this.username;
    }
}
