package pt.ulisboa.tecnico.cmu.crypto;

import java.io.Serializable;

import javax.xml.bind.DatatypeConverter;


public class CipheredMessage implements Serializable{

	private static final long serialVersionUID = 7621624716329836769L;

	private byte[] content;
    private byte[] IV;
    private byte[] integrityCheck;
    private byte[] key;

	public CipheredMessage(){
		
	}

    public CipheredMessage(byte[] content, byte[] IV, byte[] integrityCheck, byte[] key) {

        this.content = content;
        this.IV = IV;
        this.integrityCheck = integrityCheck;
        this.key = key;
        
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIntegrityCheck() {
        return integrityCheck;
    }

    public byte[] getContent() {
        return content;
    }

    public byte[] getIV() {
        return IV;
    }

    @Override
    public String toString() {
        return new String(content) + new String(IV) ;
    }
}

