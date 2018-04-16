package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class LogOutCommand implements Command{
    private static final long serialVersionUID = -8807331723807741905L;

    public LogOutCommand(){}

    @Override
    public Response handle(CommandHandler ch)   {
        return ch.handle(this);
    }
}
