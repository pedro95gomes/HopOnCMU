package pt.ulisboa.tecnico.cmu.command;

import java.util.List;

import pt.ulisboa.tecnico.cmu.response.Response;

public class PostAnswersCommand implements Command{
	private static final long serialVersionUID = -8807331723807741905L;
	private String username;
	private String quizzname;
	private List<String> answers;
	private int time_taken;

	public PostAnswersCommand(String username, String quizzname, List<String> answers,  int time_taken){
		this.username = username;
		this.quizzname = quizzname;
		this.answers = answers;
		this.time_taken=time_taken;
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

	public String getUserName() {
		return this.username;
	}

	public int getTime_taken(){
		return time_taken;
	}
}
