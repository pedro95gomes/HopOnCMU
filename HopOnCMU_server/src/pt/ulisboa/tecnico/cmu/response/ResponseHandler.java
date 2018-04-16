package pt.ulisboa.tecnico.cmu.response;

public interface ResponseHandler {
	public void handle(HelloResponse hr);
	public void handle(SignUpResponse sur);
	public void handle(LogInResponse linr);
	public void handle(LogOutResponse loutr);
	public void handle(ListLocationsResponse llr);
	public void handle(DownloadQuestionsResponse dqr);
	public void handle(PostAnswersResponse postqr);
	public void handle(QuizResultsResponse qrr);
}
