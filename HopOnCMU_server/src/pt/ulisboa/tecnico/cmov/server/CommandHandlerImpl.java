package pt.ulisboa.tecnico.cmu.server;

import pt.ulisboa.tecnico.cmu.command.CommandHandler;
import pt.ulisboa.tecnico.cmu.command.HelloCommand;
import pt.ulisboa.tecnico.cmu.command.SignUpCommand;
import pt.ulisboa.tecnico.cmu.response.HelloResponse;
import pt.ulisboa.tecnico.cmu.response.Response;
import pt.ulisboa.tecnico.cmu.response.SignUpResponse;

public class CommandHandlerImpl implements CommandHandler {

	@Override
	public Response handle(HelloCommand hc) {
		System.out.println("Received: " + hc.getMessage());
		return new HelloResponse("Hi from Server!");
	}

	@Override
	public Response handle(SignUpCommand suc){
		System.out.println("Username:" + suc.getUsername() + " | Code: " + suc.getBusCode());
		SignUpResponse registered =  new SignUpResponse(suc.getUsername(),suc.getBusCode());
		return registered;
	}
	
	@Override
	public Response handle(LogInCommand lginc){
		System.out.println("Username:" + lginc.getUsername() + " | Password: " + lginc.getPassword());
		//TODO
		// Gerar session ID e associar a username
		String sessionId = "TESTESTESTESTESTE"
		LogInResponse logedIn =  new LogInResponse(lginc.getUsername(), sessionId);
		return logedIn;
	}
	
	@Override
	public Response handle(ListLocationsCommand llc){
        System.out.println("Getting tour locations...");
        //TODO
        // Obter localização dos spots da tour
        ArrayList<String> locations = null;
        ListLocationsResponse listLocations = new ListLocationsResponse(locations);
        return listLocations;
	}
	
	@Override
	public Response handle(LogOutCommand lgoutc){
        System.out.println("Logging out...");

        //TODO
        // Removes sessionID associated with user X
        LogOutResponse loggedOut = new LogOutResponse();
        
        return loggedOut;
    }
    
    @Override
	public Response handle(DownloadQuestionsCommand dqc){
        System.out.println("Getting quizz questions...");

        //TODO
        // Gets questions from quizz Y
        DownloadQuestionsCommand quizzes = new DownloadQuestionsResponse();
        
        return quizzes;
    }
    
    @Override
	public Response handle(PostAnswersCommand pac){
        System.out.println("Submiting tourist answers...");

        //TODO
        // Calculates results for User X in quizz Y
        PostAnswersResponse postResponse = new PostAnswersResponse();
        
        return postResponse;
    }
    
    @Override
	public Response handle(QuizResultsCommand qrc){
        System.out.println("Getting quizz results...");

        //TODO
        // Gets results for user X in quiz Y
        QuizResultsResponse results = new QuizResultsResponse();
        
        return results;
    }

}
