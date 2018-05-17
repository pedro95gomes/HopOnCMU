package pt.ulisboa.tecnico.cmu.crypto;

import java.io.Serializable;
import java.security.PublicKey;

import pt.ulisboa.tecnico.cmu.command.Command;
import pt.ulisboa.tecnico.cmu.response.Response;


public class Message implements Serializable{

	private static final long serialVersionUID = 8409517480516262028L;

	private String sender;
    private String destination;
    private Command command;
    private Response response;

    
    public Message(String sender, String destination, Command command){
        this.sender = sender;
        this.destination=destination;
        this.command = command;
    }
    
    public Message(String sender, String destination, Response response){
        this.sender = sender;
        this.destination=destination;
        this.response=response;
    }

    public String getSender() {
        return sender;
    }

    public String getDestination() {
		return destination;
	}
    
    public Command getCommand() {
    	return this.command;
    }

	public Response getResponse() {
		return response;
	}
	
}
