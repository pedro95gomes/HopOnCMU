package pt.ulisboa.tecnico.cmu.crypto;

import java.io.Serializable;

import javax.xml.bind.DatatypeConverter;


 
public class CipheredMessage implements Serializable{

	private static final long serialVersionUID = 7621624716329836769L;

	private String content;
    private String IV;
    private String integrityCheck;
    private String key;

	public CipheredMessage(){
		
	}

    public CipheredMessage(byte[] content, byte[] IV, byte[] integrityCheck, byte[] key) {
    	StringBuffer toContent = new StringBuffer();
        for (int i = 0; i < content.length; ++i) {
        	toContent.append(Integer.toHexString(0x0100 + (content[i] & 0x00FF)).substring(1));
        }
        StringBuffer toIV = new StringBuffer();
        for (int i = 0; i < IV.length; ++i) {
        	toIV.append(Integer.toHexString(0x0100 + (IV[i] & 0x00FF)).substring(1));
        }
        StringBuffer toIntegrityCheck = new StringBuffer();
        for (int i = 0; i < integrityCheck.length; ++i) {
        	toIntegrityCheck.append(Integer.toHexString(0x0100 + (integrityCheck[i] & 0x00FF)).substring(1));
        }
        StringBuffer toKey = new StringBuffer();
        for (int i = 0; i < key.length; ++i) {
        	toKey.append(Integer.toHexString(0x0100 + (key[i] & 0x00FF)).substring(1));
        }
        this.content = toContent.toString();
        this.IV = toIV.toString();
        this.integrityCheck = toIntegrityCheck.toString();
        this.key = toKey.toString();
        
    }

    public byte[] getKey() {
        return DatatypeConverter.parseHexBinary(key);
    }

    public byte[] getIntegrityCheck() {
        return DatatypeConverter.parseHexBinary(integrityCheck);
    }

    public byte[] getContent() {
        return DatatypeConverter.parseHexBinary(content);
    }

    public byte[] getIV() {
        return DatatypeConverter.parseHexBinary(IV);
    }
    
    public String getStringKey(){
    	return key;
    }
    public String getStringIV(){
    	return IV;
    }
    public String getStringContent(){
    	return content;
    }
    public String getStringIntegrityCheck(){
    	return integrityCheck;
    }

    @Override
    public String toString() {
        return new String(content) + new String(IV) ;
    }
}

