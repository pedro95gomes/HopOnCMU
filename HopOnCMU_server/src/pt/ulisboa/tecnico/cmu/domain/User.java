package pt.ulisboa.tecnico.cmu.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class User implements Serializable{
	private static final long serialVersionUID = 8766447384756338327L;
	private String username = null;
	private String sessionId = null;
	private byte[] busCode = null;
	
	public User(String username, String busCode) {
		this.username = username;
		this.busCode = computeHash(busCode);
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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
	
	public byte[] computeHash(String code) {
		MessageDigest digest;
		byte[] password = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			password = digest.digest(code.getBytes("UTF_8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return password;
	}
	
	public boolean checkPassword(String password) {
		byte[] hashed = computeHash(password);
		if(Arrays.equals(busCode, hashed)) 
			return true;
		return false;
	}
}
