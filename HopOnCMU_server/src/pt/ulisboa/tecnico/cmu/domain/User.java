package pt.ulisboa.tecnico.cmu.domain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class User implements Serializable{
	private static final long serialVersionUID = 8766447384756338327L;
	private String username = null;
	private String sessionId = null;
	private byte[] busCode = null;
	private Map<String, List<String>> answers;
	private Map<String,Integer> results = null;
	
	public User(String username, String busCode) {
		this.username = username;
		this.busCode = computeHash(busCode);
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public List<String> getAnswers(String quizzname){
		return this.answers.get(quizzname);
	}
	
	public List<String> setAnswers(String quizzname, List<String> answers){
		return this.answers.put(quizzname, answers);
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
