package pt.ulisboa.tecnico.cmov.command;

import pt.ulisboa.tecnico.cmov.response.Response;

public interface CommandHandler {
	public Response handle(HelloCommand hc);
	public Response handle(SignUpCommand suc);

}
