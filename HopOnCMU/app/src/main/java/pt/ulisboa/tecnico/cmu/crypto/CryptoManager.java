package pt.ulisboa.tecnico.cmu.crypto;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;


public class CryptoManager {

    private PublicKey pubKey;
    private PrivateKey privKey;
    private static CryptoManager cryptoManager;

    public CryptoManager(PublicKey publicKey, PrivateKey privateKey){
    	this.pubKey=publicKey;
    	this.privKey=privateKey;
    }
    
    public static CryptoManager getInstance(PublicKey publicKey, PrivateKey privateKey){
    	if(cryptoManager==null){
    		cryptoManager = new CryptoManager(publicKey, privateKey);
    	}
    	return cryptoManager;
    }
    
    public CipheredMessage makeCipheredMessage(Message message, PublicKey receiverPubKey){
        CipheredMessage cipheredMessage = null;
        try {
            //Required params
            long nonce = new SecureRandom().nextLong();
            byte[] IV = generateIV();

            //AES ciphering of Message
            SecretKey aesKey = generateAESKey();
            byte[] cipheredContent = cipherContent(message, IV, aesKey);

            //Signature generation
            byte[] concatParams = concatHashParams(message, nonce, IV);
            byte[] digitalSig = CryptoUtil.makeDigitalSignature(concatParams, privKey);
            IntegrityCheck integrityCheck = new IntegrityCheck(digitalSig, nonce, nonce);
            byte[] integrityCheckBytes = toBytes(integrityCheck);

            //AES ciphering of Signature and params
            byte[] cipheredIntegrityCheck = CryptoUtil.symCipher(integrityCheckBytes, IV, aesKey);

            //RSA ciphering of AES key
            byte[] keyBytes = toBytes(aesKey);
            if(receiverPubKey==null)
            	System.out.println("Receiver key is null");
            byte[] cipheredKey = CryptoUtil.asymCipher(keyBytes, receiverPubKey);

            cipheredMessage = new CipheredMessage(cipheredContent, IV, cipheredIntegrityCheck, cipheredKey);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            System.out.println("Cipher error1");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cipher error2");
        }
        return cipheredMessage;
    }

    /**
     * Deciphers a {@link CipheredMessage} object to obtain the original message, and verifies
     * digital signature to ensure integrity and non-repudiation
     * @param cipheredMessage The received ciphered message
     * @return {@link Message} object that was encapsulated
     * @throws Exception 
     */

    @TargetApi(Build.VERSION_CODES.O)
    public Message decipherCipheredMessage(CipheredMessage cipheredMessage){
        Message deciphMsg = null;
        try {
            SecretKey key = (SecretKey) fromBytes(CryptoUtil.asymDecipher(cipheredMessage.getKey(), privKey));
            byte[] decipheredContent = CryptoUtil.symDecipher(cipheredMessage.getContent(), cipheredMessage.getIV(), key);
            deciphMsg = (Message) fromBytes(decipheredContent);


            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(deciphMsg.getSender().getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            byte[] decipheredIntegrityBytes = CryptoUtil.symDecipher(cipheredMessage.getIntegrityCheck(), cipheredMessage.getIV(), key);
            IntegrityCheck check = (IntegrityCheck) fromBytes(decipheredIntegrityBytes);
            if(verifyIntegrity(deciphMsg, cipheredMessage.getIV(), check,pubKey )) return deciphMsg;
            else throw new IllegalStateException("Invalid Signature");
        } catch (ClassNotFoundException | IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            System.out.println("Decipher error...");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return deciphMsg;
    }

    /**
     * Calculates digital sign on the receiver end and compares with the one received
     * @param msg The decrypted {@link Message}
     * @param IV Initialization Vector contained in the received message
     * @param check Contains the rest of the parameters used in the signature
     * @param key The public Key of the origin
     * @return true if signature matches, false otherwise
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvalidAlgorithmParameterException
     */
    private boolean verifyIntegrity(Message msg, byte[] IV, IntegrityCheck check, PublicKey key) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, ClassNotFoundException, InvalidAlgorithmParameterException {
        byte[] concatParams = concatHashParams(msg, check.getNonce(), IV);
       return CryptoUtil.verifyDigitalSignature(check.getDigitalSignature(), concatParams, key);
    }

    /**
     * Ciphers a {@link Message} object with AES
     * @param message msg to be ciphered
     * @param IV Initialization Vector
     * @param skey AES key
     * @return byte array containing the ciphered contents
     */
    private byte[] cipherContent(Message message, byte[] IV, SecretKey skey){
        byte[] cipheredMessage = new byte[0];
        try {
            cipheredMessage = CryptoUtil.symCipher(toBytes(message), IV, skey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return cipheredMessage;
    }

    /**
     * Converts a generic argument to a byte array
     * @param toConvert What we want to convert
     * @param <T> Could by any object
     * @return byte array representation of the original object
     * @throws IOException
     */
    private <T> byte[] toBytes(T toConvert) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(byteStream);
        os.writeObject(toConvert);
        os.flush();
        return byteStream.toByteArray();
    }

    /**
     * Converts a byte array to a Java object.
     * It's not possible to know which object it is so it needs to be casted when storing in a variable
     * Use only when you know what type of object it is
     * @param toConvert byte array that should be converted
     * @return Object obtained from byte array
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Object fromBytes(byte[] toConvert) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(toConvert);
        ObjectInputStream is = new ObjectInputStream(byteInStream);
        return is.readObject();
    }

    /**
     * Concatenates parameters that go into the hash and turns it into a byte array
     * @param message Message that should be sent
     * @param timestamp timestamp of the message
     * @param IV Init Vector
     * @return byte array of msg||IV||t
     * @throws IOException
     */
    private byte[] concatHashParams(Message message, long timestamp, byte[] IV) throws IOException {
        byte[] msgBytes = toBytes(message);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.SIZE/8);
        byteBuffer.putLong(timestamp);
        byte[] timestampBytes = byteBuffer.array();
        byte[] concatedParams = new byte[msgBytes.length + timestampBytes.length + IV.length];
        System.arraycopy(msgBytes, 0, concatedParams, 0, msgBytes.length);
        System.arraycopy(timestampBytes, 0, concatedParams, msgBytes.length, timestampBytes.length);
        System.arraycopy(IV, 0, concatedParams, msgBytes.length + timestampBytes.length, IV.length);
        return concatedParams;
    }

    /**
     * Generates an init vector for AES ciphering
     * @return
     */
    private byte[] generateIV(){
        SecureRandom random = new SecureRandom();
        byte[] initializationVector = new byte[128/8];
        random.nextBytes(initializationVector);
        return initializationVector;
    }


    /**
     * Generates an AES Key for ciphering
     * @return AES key
     * @throws NoSuchAlgorithmException
     */
    private SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecretKey skey = keygen.generateKey();
        return skey;
    }
    
    public PublicKey getPublicKey() {
    	return pubKey;
    }
    public PrivateKey getPrivateKey() {
    	return privKey;
    }

}

