package pt.ulisboa.tecnico.cmu.domain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable{
	private static final long serialVersionUID = 8766447384756338327L;
	private String username = null;
	private String sessionId = null;
	private byte[] busCode = null;
	private Map<String, List<String>> answers;
	private Map<String,Integer> results;
	private int num_questions_correct;
	private Map<String,Integer> time_taken;
	private int total_time;
	
	public User(String username, String busCode) {
		this.username = username;
		this.busCode = computeHash(busCode);
		this.answers = new HashMap<String, List<String>>();
		this.results = new HashMap<String, Integer>();
		num_questions_correct = 0;
		this.time_taken = new HashMap<String, Integer>();
		total_time = 0;
	}

	public int getNumQuestionsCorrect(){
		return num_questions_correct;
	}

	public void setNumQuenstionsCorrect(int v){
		num_questions_correct = v;
	}

	public int getTotalTime(){
		return total_time;
	}

	public void setTotalTime(int x){
		total_time = x;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public List<String> getAnswers(String quizzname){
		return this.answers.get(quizzname);
	}
	
	public void setAnswers(String quizzname, List<String> answers){
		for(String a : answers)
			System.out.println(a);
		this.answers.put(quizzname, answers);
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	public byte[] getBusCode() {
		return this.busCode;
	}
	
	public int getResult(String quizzname) {
		return this.results.get(quizzname);
	}
	
	public void setResult(String quizzname, int result) {
		this.results.put(quizzname, result);
	}

	public int getTime(String quizzname) {
		return time_taken.get(quizzname);
	}

	public void setTimeTaken(String quizzname, int time) {
		time_taken.put(quizzname, time);
	}
	
	public byte[] computeHash(String code) {
		MessageDigest digest;
		byte[] password = code.getBytes(StandardCharsets.UTF_8);
		byte[] result = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.update(password);
			result = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//to use just in case
	public byte[] pad32(byte[] password){
		byte[] padded = new byte[32];
		for(int i=0; i<password.length; i++){
			padded[i] = password[i];
		}
		return padded;
	}
	
	public boolean checkPassword(String password) {
		byte[] hashed = computeHash(password);
		if(Arrays.toString(hashed).equals(Arrays.toString(getBusCode()))){
			return true;
		}
		return false;
	}
}
