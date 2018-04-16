package pt.ulisboa.tecnico.cmu.domain;

import java.util.HashMap;
import java.util.Map;

public class ServerUtil {
	private static final String path_quizzes = "quizzes/";
	private static final String path_users = "users/";
	private Map<String, String> user_session;
	
	public ServerUtil(){
		this.user_session = new HashMap();
	}
	
	public void setSessionId(String username, String sessionId){
		if(!user_session.containsKey(username)){
			user_session.put(username, sessionId);
		}
	}
	
	public void revokeSessionId(String username){
		if(user_session.containsKey(username)){
			user_session.remove(username);
		}
	}
	
	public Quizz getQuizz(String name){
		//FileReader fr = new FileReader(path_quizzes + name);
		
		return null;
	}
}
