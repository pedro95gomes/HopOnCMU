package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class QuizResultsCommand implements Command{
    private static final long serialVersionUID = -8807331723807741905L;

    private String ssid;
    private String[] quiz_names;

    public QuizResultsCommand(String ssid, String[] quiz_names){
    	this.ssid = ssid;
    	this.quiz_names = quiz_names;
    }

    @Override
    public Response handle(CommandHandler ch)   {
        return ch.handle(this);
    }

	public String[] getQuizzName() {
		return this.quiz_names;
	}

	public String getUserSSID() {
		return this.ssid;
	}
}
