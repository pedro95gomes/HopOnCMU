package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class HelloCommand implements Command {
	
	private static final long serialVersionUID = -8807331723807741905L;
	private String message;

	public HelloCommand(String message) {
		this.message = message;
	}
	
	@Override
	public Response handle(CommandHandler chi) {
		return chi.handle(this);
	}
	
	public String getMessage() {
		return this.message;
	}
}
