package pt.ulisboa.tecnico.cmu.crypto;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.Cipher;

public class KeystoreManager {

	private KeyStore ks;

	public KeyStore getKeyStore(){

		return this.ks;
	}

	public KeystoreManager(String name, String pass) throws Exception{
		
		ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(getClass().getResourceAsStream(name), pass.toCharArray());
	}

	public KeyPair getKeyPair(String name, String pass) throws Exception{

		Key key = getKeyStore().getKey(name.toLowerCase(), pass.toCharArray());
	    if (key instanceof PrivateKey) {
	      // Get certificate of public key
	      Certificate cert = getKeyStore().getCertificate(name);

	      // Get public key
	      PublicKey publicKey = cert.getPublicKey();

	      // Return a key pair
	      return new KeyPair(publicKey, (PrivateKey) key);
	      
	    }

	    return null;
	}
	
	public PublicKey getPublicKeyByName(String name) throws Exception{

		Certificate certificate = getKeyStore().getCertificate(name);
		PublicKey publicKey = certificate.getPublicKey();
	    return publicKey;
	}
}
