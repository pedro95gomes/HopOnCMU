package pt.ulisboa.tecnico.cmu.crypto;

import java.io.Serializable;

public class IntegrityCheck implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5539635077805923444L;
	private byte[] digitalSignature;
    private long nonce;
    private long timestamp;

    public IntegrityCheck(byte[] digitalSignature, long nonce, long timestamp) {
        this.digitalSignature = digitalSignature;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }

    public byte[] getDigitalSignature() {
        return digitalSignature;
    }

    public long getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
