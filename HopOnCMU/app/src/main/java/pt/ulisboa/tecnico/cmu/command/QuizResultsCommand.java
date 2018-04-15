package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

class QuizResultsCommand implements Command{
    private static final long serialVersionUID = -8807331723807741905L;

    QuizResultsCommand(){}

    @Override
    public Response handle(CommandHandler ch)  {
        return ch.handle(this);
    }
}
