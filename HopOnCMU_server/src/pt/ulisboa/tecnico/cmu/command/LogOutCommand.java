package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class LogOutCommand implements Command{
    private static final long serialVersionUID = -8807331723807741905L;
    private String username;
    private String sessionId;
    
    public LogOutCommand(String username, String sessionId){
    	this.username = username;
    	this.sessionId = sessionId;
    }

    @Override
    public Response handle(CommandHandler ch)   {
        return ch.handle(this);
    }

	public String getUsername() {
		return this.username;
	}

	public String getSessionId() {
		return this.sessionId;
	}
}
