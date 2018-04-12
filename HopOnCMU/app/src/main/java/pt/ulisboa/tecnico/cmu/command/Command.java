package pt.ulisboa.tecnico.cmu.command;

import java.io.Serializable;

import pt.ulisboa.tecnico.cmu.response.Response;

public interface Command extends Serializable {
	Response handle(CommandHandler ch);
}
