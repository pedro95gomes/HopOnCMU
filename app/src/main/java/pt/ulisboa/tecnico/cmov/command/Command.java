package pt.ulisboa.tecnico.cmov.command;

import java.io.Serializable;

import pt.ulisboa.tecnico.cmov.response.Response;

public interface Command extends Serializable {
	Response handle(CommandHandler ch);
}
