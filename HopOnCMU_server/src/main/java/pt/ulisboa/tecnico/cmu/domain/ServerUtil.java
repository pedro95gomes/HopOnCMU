package pt.ulisboa.tecnico.cmu.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class ServerUtil {
	private static final String path_quizzes = "resources/quizzes/";
	private static final String path_users = "resources/users/";
	private static final String path_usedcodes = "resources/codes/usedcodes.txt";
	private static final String path_allcodes = "resources/codes/allcodes.txt";
	private static final String path_locations ="resources/tour/locations.txt";
	private Map<String, User> users;
	private Map<String, String> sessions;
	private List<Quizz> quizzes;
	private List<String> initial_codes;
	private List<String> used_codes;
	private List<String> tourLocations;
	private List<String> ranking;
	
	public ServerUtil(){
		this.sessions = new HashMap<>();
		this.users = new HashMap<>();
		List<User> usersList = getUsersFromDirectory();
		for(User u : usersList) {
			this.users.put(u.getUsername(), u);
		}
		this.quizzes = getQuizzesFromDirectory();
		this.initial_codes = getInitialCodesFromFile();
		this.used_codes = getCodesFromFile();
		this.tourLocations = getLocationsFromFile();
		ranking = null;
	}

	public User getUser(String username) {
		return this.users.get(username);
	}
	
	public void setUser(String username, User user) {
		this.users.put(username, user);
	}
	
	public List<Quizz> getQuizzes() {
		return quizzes;
	}

	public void setQuizzes(List<Quizz> quizzes) {
		this.quizzes = quizzes;
	}
	
	public List<String> getTourLocations() {
		return tourLocations;
	}
	
	public void addUsedCode(String code){
		this.used_codes.add(code);
	}

	public void setTourLocations(ArrayList<String> tourLocations) {
		this.tourLocations = tourLocations;
	}
	
	public boolean isPassword(String username, String password) {
		if(users.containsKey(username))
			return getUser(username).checkPassword(password);
		return false;
	}
	
	public void registerUser(String username, String password) {
		if(!users.containsKey(username)) {
			User user = new User(username, password);
			this.users.put(username, user);
			saveUser(username);
		}
		else
			System.out.println("User already registered: " + username);
	}
	
	private List<User> getUsersFromDirectory() {
		List<User> users = new ArrayList<>();

		File folder = new File(path_users);

		if(!folder.exists())
			folder.mkdir();

		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile() && file.length() > 0) {
		        try {
			    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
					User user = (User) ois.readObject();
					users.add(user);
					ois.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
		    }
		    else{
				System.out.println("User file is empty");
		    }
		}
		return users;
	}

	public boolean setSessionId(String username, String sessionId){
		if(users.containsKey(username)){
			sessions.put(sessionId, username);
			User user = users.get(username);
			user.setSessionId(sessionId);
			users.put(username, user);
			saveUser(user.getUsername());
			return true;
		}
		return false;
	}
	
	public String revokeSessionId(String sessionId){
		String username = sessions.get(sessionId);
		if(username!= null){
			User user = users.get(username);
			user.setSessionId(null);
			users.put(username, user);
			sessions.remove(sessionId);
			saveUser(user.getUsername());
			return user.getUsername();
		}
		return null;
	}
	
	public Boolean verifyUsername(String username){
		if(users.containsKey(username) && users.get(username)!=null)
			return false;
		return true;
	}
	
	private List<Quizz> getQuizzesFromDirectory() {
		List<Quizz> quizzes = new ArrayList<>();

		File folder = new File(path_quizzes);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        Quizz quizz = getQuizz(file.getName());
		        quizzes.add(quizz);
		    }
		}
		return quizzes;
	}
	
	public Quizz getQuizz(String name){
		List<String[]> questions = new ArrayList<String[]>();
		Quizz result = null;
		try {
			File cod = new File(path_quizzes + name);
			if(cod.length() == 0){
				System.out.println("Quizzes file is empty: " + name);
				return null;
			}
			
			FileReader fr = new FileReader(path_quizzes + name);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null) {
				String[] question = new String[6];
				for(int i = 0; i < 6; i++) {
					question[i]=line;
					line = br.readLine();
				}
				questions.add(question);
				line = br.readLine();
			}
			br.close();
			result = new Quizz(name, questions);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void saveUser(String username) {
		User user = users.get(username);
		try {
	    	ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(path_users + username + ".txt", false));
			ous.writeObject(user);
			ous.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getInitialCodes(){
		return this.initial_codes;
	}
	
	public List<String> getInitialCodesFromFile(){
		List<String> codes = new ArrayList<String>();
		try {
			File cod = new File(path_allcodes);
			if(cod.length() == 0){
				System.out.println("Initial codes file is empty");
				return codes;
			}
			FileReader fr = new FileReader(path_allcodes);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null) {
				codes.add(line);
				line = br.readLine();
			}
			br.close();
			fr.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return codes;
	}
	
	public List<String> getUsedCodes() {
		return this.used_codes;
	}
	
	public List<String> getCodesFromFile() {
		List<String> codes = new ArrayList<String>();
		try {
			File cod = new File(path_usedcodes);
			if(cod.length() == 0){
				System.out.println("Used codes file is empty");
				return codes;
			}
	    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path_usedcodes));
	    	Object o = ois.readObject();
	    	if (o instanceof ArrayList<?>){
	    		for(Object ob : (ArrayList<?>)o){
	    			if (ob instanceof String){
	    				codes.add((String) ob);
	    			}
	    		}
	    	}
			ois.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return codes;
	}
	
	public void saveCodes() {
		List<String> usedCodes = getUsedCodes();
		try {
	    	ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(path_usedcodes, false));
			ous.writeObject(usedCodes);
			ous.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public Boolean verifyCode(String code){
		List<String> all_codes = getInitialCodes();
		List<String> used_codes = getUsedCodes();
		Boolean isUsed = false;
		
		System.out.println(all_codes);
		if(used_codes != null) { // Check if code was already used: if not, register user
			if(used_codes.contains(code)){
				isUsed=true;
			}
		}
		if(all_codes != null){
			if(all_codes.contains(code) && !isUsed){
				return true;
			}
		}
		System.out.println("Code " + code + " does not exist or is already in use");
		return false;
	}
	
	public List<String> getLocationsFromFile(){
		FileReader fr;
		List<String> locations = new ArrayList<String>();
		try {
			File cod = new File(path_locations);
			if(cod.length() == 0){
				System.out.println("Locations file is empty");
				return null;
			}
			
			fr = new FileReader(path_locations);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null) {
				locations.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return locations;
	}
	
	public int checkAnswers(String ssid, Quizz quizz) {
		int result = 0;		//número de respostas corretas no quizz
		int numQuestion = 0;
		String username = sessions.get(ssid);
		User user = getUser(username);
		for(String answer : user.getAnswers(quizz.getName())) {
			if(answer.equals(quizz.getAnswer(numQuestion))) {
				result+=1;
			}
			numQuestion+=1;
		}
		user.setResult(quizz.getName(),result);
		this.users.put(user.getUsername(), user);
		saveUser(user.getUsername());
		System.out.println(user.getResult(quizz.getName()));
		return result;
	}
	
	public void setUserAnswers(String ssid, String quizzname, List<String> answers) {
		String username = sessions.get(ssid);
		User user = getUser(username);
		user.setAnswers(quizzname, answers);
		this.users.put(username, user);
		saveUser(username);
		System.out.println("Saving answers for user "+username);
	}

	public List<String> getRanking() {
		List<User> all_users = getUsersFromDirectory();
		List<Quizz> all_quizzes = getQuizzesFromDirectory();

		for(int i = 0; i < all_users.size(); i++){
			all_users.get(i).setNumQuenstionsCorrect(0);
			int num_correct_answers = 0;
			for(int j = 0; j < all_quizzes.size(); j++){
				Quizz quizz = all_quizzes.get(j);
				int result = 0;
				try {
					result = all_users.get(i).getResult(quizz.getName());
				} catch (NullPointerException e){
					result = 0;
				}
				num_correct_answers= num_correct_answers + result;
			}
			all_users.get(i).setNumQuenstionsCorrect(num_correct_answers);
		}
/*
		for(int i = 0; i < all_users.size(); i++){
			System.out.println("User: " + all_users.get(i).getUsername() + " Correct: " + all_users.get(i).getNumQuestionsCorrect());
		}
*/
		all_users.sort(Comparator.comparingInt(User::getNumQuestionsCorrect).reversed());   //ordena a lista
/*
		System.out.println("ORDERED:");
		for(int i = 0; i < all_users.size(); i++){
			System.out.println("User: " + all_users.get(i).getUsername() + " Correct: " + all_users.get(i).getNumQuestionsCorrect());
		}
*/
		ranking = composeRankingStrings(all_users); //all_users já está ordenado

		return ranking;
	}

	public List<String> composeRankingStrings(List<User> users){
        List<String> strings = new ArrayList<String>();;

        for(int i = 0; i < users.size(); i++){
            String s = (i+1) + " | " + users.get(i).getUsername() + " with " + users.get(i).getNumQuestionsCorrect() + " correct answers.";
            strings.add(i,s);
        }
/*
        for(int i = 0; i < strings.size(); i++){
            System.out.println(strings.get(i));
        }
*/
        return strings;
    }
}
