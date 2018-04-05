package pt.ulisboa.tecnico.cmov.server;

import pt.ulisboa.tecnico.cmov.command.CommandHandler;
import pt.ulisboa.tecnico.cmov.command.SignUpCommand;
import pt.ulisboa.tecnico.cmov.command.HelloCommand;
import pt.ulisboa.tecnico.cmov.response.HelloResponse;
import pt.ulisboa.tecnico.cmov.response.Response;
import pt.ulisboa.tecnico.cmov.response.SignUpResponse;

public class CommandHandlerImpl implements CommandHandler {

	@Override
	public Response handle(HelloCommand hc) {
		System.out.println("Received: " + hc.getMessage());
		return new HelloResponse("Hi from Server!");
	}
	
	@Override
	public Response handle(SignUpCommand su) {
		System.out.println("Received: " + su.getUsername() + su.getBusCode());
		return new SignUpResponse(su.getUsername(), "You just signed up (not yet)");
	}
	
}
