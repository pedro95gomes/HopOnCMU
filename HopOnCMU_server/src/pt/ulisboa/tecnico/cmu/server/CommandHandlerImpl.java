package pt.ulisboa.tecnico.cmu.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		
		String code = suc.getBusCode();
		System.out.println(code);
		
		String username = suc.getUsername();
		if (sv.verifyCode(code) && sv.verifyUsername(username)){
			sv.registerUser(suc.getUsername(), code);
			sv.addUsedCode(code);
			sv.saveCodes();
			return new SignUpResponse(suc.getUsername(),code);
		}
		
		return new SignUpResponse(suc.getUsername(),null);
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
        List<String> locations = sv.getTourLocations();
        ListLocationsResponse listLocations = new ListLocationsResponse(locations);
        return listLocations;
	}
	
	@Override
	public Response handle(LogOutCommand lgoutc){
        System.out.println("Logging out... " + lgoutc.getUsername() +" with sessionID "+ lgoutc.getSessionId());
        
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
        System.out.println("Submiting tourist answers for " + pac.getQuizzName());

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
        Map<String, Integer> results = new HashMap<String, Integer>();
        Map<String, Integer> numQuestions = new HashMap<String,Integer>();
        String[] answered_quizes = qrc.getQuizzName();
        for(Quizz quizz : quizzes) {
        	for(String name : answered_quizes){
        		if(quizz.getName().equals(name)){
        			int result = sv.checkAnswers(qrc.getUserSSID(), quizz);
        			results.put(name, result);
        			numQuestions.put(name, quizz.getNumQuestions());
        		}
        		else{
        			results.put(name, 0);
        			numQuestions.put(name, quizz.getNumQuestions());
        		}
        	}
        }
        QuizResultsResponse response = new QuizResultsResponse(results, numQuestions);
        
        return response;
    }
}