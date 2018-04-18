package pt.ulisboa.tecnico.cmu.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pt.ulisboa.tecnico.cmu.command.CommandHandler;
import pt.ulisboa.tecnico.cmu.command.DownloadQuestionsCommand;
import pt.ulisboa.tecnico.cmu.command.HelloCommand;
import pt.ulisboa.tecnico.cmu.command.ListLocationsCommand;
import pt.ulisboa.tecnico.cmu.command.LogInCommand;
import pt.ulisboa.tecnico.cmu.command.LogOutCommand;
import pt.ulisboa.tecnico.cmu.command.PostAnswersCommand;
import pt.ulisboa.tecnico.cmu.command.QuizResultsCommand;
import pt.ulisboa.tecnico.cmu.command.SignUpCommand;
import pt.ulisboa.tecnico.cmu.domain.Quizz;
import pt.ulisboa.tecnico.cmu.domain.ServerUtil;
import pt.ulisboa.tecnico.cmu.response.DownloadQuestionsResponse;
import pt.ulisboa.tecnico.cmu.response.HelloResponse;
import pt.ulisboa.tecnico.cmu.response.ListLocationsResponse;
import pt.ulisboa.tecnico.cmu.response.LogInResponse;
import pt.ulisboa.tecnico.cmu.response.LogOutResponse;
import pt.ulisboa.tecnico.cmu.response.PostAnswersResponse;
import pt.ulisboa.tecnico.cmu.response.QuizResultsResponse;
import pt.ulisboa.tecnico.cmu.response.Response;
import pt.ulisboa.tecnico.cmu.response.SignUpResponse;

public class CommandHandlerImpl implements CommandHandler {

	ServerUtil sv = new ServerUtil();
	
	@Override
	public Response handle(HelloCommand hc) {
		System.out.println("Received: " + hc.getMessage());
		return new HelloResponse("Hi from Server!");
	}

	@Override
	public Response handle(SignUpCommand suc){
		System.out.println("Username:" + suc.getUsername() + " | Code: " + suc.getBusCode());
		// Get list of used codes
		List<String> used_codes = sv.getCodes();
		SignUpResponse registered;

		String code = null;
		if(!used_codes.contains(suc.getBusCode())) { // Check if code was already used: if not, register user
			code = suc.getBusCode();
			sv.registerUser(suc.getUsername(), code);
			registered =  new SignUpResponse(suc.getUsername(),code);
		}else
			registered =  new SignUpResponse(suc.getUsername(),null);
		
		return registered;
	}
	
	@Override
	public Response handle(LogInCommand lginc){
		System.out.println("Username:" + lginc.getUsername() + " | Password: " + lginc.getPassword());
		String sessionId = null;
		// Check if password/busCode for user is correct
		if(sv.isPassword(lginc.getUsername(), lginc.getPassword())) {
			UUID uuid = UUID.randomUUID();
			sessionId = uuid.toString();
			sv.setSessionId(lginc.getUsername(), sessionId); // Generate and set user session Id
		}
		LogInResponse logedIn =  new LogInResponse(lginc.getUsername(), sessionId);
		return logedIn;
	}
	
	@Override
	public Response handle(ListLocationsCommand llc){
        System.out.println("Getting tour locations...");
        // Obter localização dos spots da tour
        ArrayList<String> locations = sv.getTourLocations();
        ListLocationsResponse listLocations = new ListLocationsResponse(locations);
        return listLocations;
	}
	
	@Override
	public Response handle(LogOutCommand lgoutc){
        System.out.println("Logging out...");
        
        // Removes sessionID associated with user X
        sv.revokeSessionId(lgoutc.getUsername(), lgoutc.getSessionId());
        LogOutResponse loggedOut = new LogOutResponse(lgoutc.getUsername());
        
        return loggedOut;
    }
    
    @Override
	public Response handle(DownloadQuestionsCommand dqc){
        System.out.println("Getting quizz questions...");

        List<String[]> questions = null;
        // Gets questions from quizz Y
        List<Quizz> quizzes = sv.getQuizzes();
        for(Quizz quizz : quizzes) {
        	if(quizz.getName().equals(dqc.getName())) {
        		 questions = quizz.getQuestions();
        	}
        }
        DownloadQuestionsResponse response = new DownloadQuestionsResponse(questions);
        
        return response;
    }
    
    @Override
	public Response handle(PostAnswersCommand pac){
        System.out.println("Submiting tourist answers...");

        // Calculates results for User X in quizz Y
        List<Quizz> quizzes = sv.getQuizzes();
        boolean success = false;
        for(Quizz quizz : quizzes) {
        	if(quizz.getName().equals(pac.getQuizzName())) {
        		 sv.setUserAnswers(pac.getUserName(), quizz.getName(), pac.getAnswers());
        		 success = true;
        	}
        }
        PostAnswersResponse postResponse = new PostAnswersResponse(success);
        
        return postResponse;
    }
    
    @Override
	public Response handle(QuizResultsCommand qrc){
        System.out.println("Getting quizz results...");

        // Gets results for user X in quiz Y
        List<Quizz> quizzes = sv.getQuizzes();
        double result = 0;
        for(Quizz quizz : quizzes) {
        	if(quizz.getName().equals(qrc.getQuizzName())) {
        		 result = sv.checkAnswers(qrc.getUserName(), quizz);
        	}
        }
        QuizResultsResponse results = new QuizResultsResponse(result);
        
        return results;
    }
}