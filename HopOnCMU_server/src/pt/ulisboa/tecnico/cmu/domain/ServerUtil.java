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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerUtil {
	private static final String path_quizzes = "quizzes/";
	private static final String path_users = "users/";
	private static final String path_codes = "codes/codes.txt";
	private static final String path_locations ="tour/locations.txt";
	private Map<String, User> users;
	private List<Quizz> quizzes;
	private List<String> codes;
	private ArrayList<String> tourLocations;
	
	public ServerUtil(){
		this.users = new HashMap<>();
		List<User> usersList = getUsersFromDirectory();
		for(User u : usersList) {
			this.users.put(u.getUsername(), u);
		}
		this.quizzes = getQuizzesFromDirectory();
		this.codes = getCodesFromFile();
		this.tourLocations = getLocationsFromFile();
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
	
	public ArrayList<String> getTourLocations() {
		return tourLocations;
	}

	public void setTourLocations(ArrayList<String> tourLocations) {
		this.tourLocations = tourLocations;
	}
	
	public boolean isPassword(String username, String password) {
		return getUser(username).checkPassword(password);
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
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        try {
			    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
					User user = (User) ois.readObject();
					users.add(user);
					ois.close();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		return users;
	}
	
	private List<Quizz> getQuizzesFromDirectory() {
		List<Quizz> quizzes = new ArrayList<>();

		File folder = new File(path_users);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        Quizz quizz = getQuizz(file.getName());
		        quizzes.add(quizz);
		    }
		}
		return quizzes;
	}

	public boolean setSessionId(String username, String sessionId){
		if(users.containsKey(username)){
			User user = users.get(username);
			user.setSessionId(sessionId);
			saveUser(user.getUsername());
			users.put(username, user);
			return true;
		}
		return false;
	}
	
	public boolean revokeSessionId(String username, String sessionId){
		if(users.containsKey(username) && users.get(username).getSessionId().equals(sessionId)){
			User user = users.get(username);
			user.setSessionId(null);
			saveUser(user.getUsername());
			users.put(username, user);
			return true;
		}
		return false;
	}
	
	public Quizz getQuizz(String name){
		String[] question = new String[4];
		List<String[]> questions = new ArrayList<String[]>();
		Quizz result = null;
		try {
			FileReader fr = new FileReader(path_quizzes + name);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null) {
				for(int i = 0; i < 5; i++) {
					question[i]=line;
					line = br.readLine();
				}
				questions.add(question);
			}
			br.close();
			result = new Quizz(name, questions);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> getCodes() {
		return this.codes;
	}
	
	public List<String> getCodesFromFile() {
		List<String> codes = null;
		try {
	    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path_codes));
	    	codes = (List<String>) ois.readObject();
			ois.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return codes;
	}
	
	public void saveCodes() {
		try {
	    	ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(path_codes, false));
			ous.writeObject(getCodes());
			ous.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getLocationsFromFile(){
		FileReader fr;
		ArrayList<String> locations = null;
		try {
			fr = new FileReader(path_locations);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			locations = new ArrayList<>();
			while(line != null) {
				line = br.readLine();
				locations.add(line);
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
}
