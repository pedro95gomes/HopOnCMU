package pt.ulisboa.tecnico.cmu.command;

import java.util.List;

import pt.ulisboa.tecnico.cmu.response.Response;

public class PostAnswersCommand implements Command{
    private static final long serialVersionUID = -8807331723807741905L;
    private String sessionId;
    private String quizzname;
    private List<String> answers;
    
    public PostAnswersCommand(String sessionId, String quizzname, List<String> answers){
    	this.sessionId = sessionId;
    	this.quizzname = quizzname;
    	this.answers = answers;
    }

    @Override
    public Response handle(CommandHandler ch)   {
        return ch.handle(this);
    }

	public String getQuizzName() {
		return this.quizzname;
	}

	public List<String> getAnswers() {
		return this.answers;
	}

	public String getSessionId() {
		return this.sessionId;
	}
}
