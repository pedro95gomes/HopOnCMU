package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public interface CommandHandler {
	public Response handle(HelloCommand hc);
	public Response handle(SignUpCommand suc);
	public Response handle(LogInCommand lin);
	public Response handle(LogOutCommand lout);
	public Response handle(ListLocationsCommand ll);
	public Response handle(DownloadQuestionsCommand dq);
	public Response handle(PostAnswersCommand postq);
	public Response handle(QuizResultsCommand qr);
	public Response handle(RankingCommand rank);

}
