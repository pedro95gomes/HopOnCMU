package pt.ulisboa.tecnico.cmu.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import pt.ulisboa.tecnico.cmu.command.Command;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.response.Response;

public class Server {

	private static final int PORT = 9090;

	public static void main(String[] args) throws Exception {
		CommandHandlerImpl chi = new CommandHandlerImpl();
		final ServerSocket socket = new ServerSocket(PORT);
		Socket client = null;
		CryptoManager cryptoManager = new CryptoManager(chi.getPublicKey(), chi.getPrivateKey());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Server now closed.");
				try { socket.close(); }
				catch (Exception e) { }
			}
		});

		System.out.println("Server is accepting connections at " + PORT);

		while (true) {
			try {
			
				client = socket.accept();

				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				CipheredMessage cipheredMessage = (CipheredMessage) ois.readObject();
				Message decipheredMessage = cryptoManager.decipherCipheredMessage(cipheredMessage);
				Command cmd =  decipheredMessage.getCommand();

				Response rsp = cmd.handle(chi);
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(decipheredMessage.getSender().getBytes()));
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	            PublicKey pubKey = keyFactory.generatePublic(keySpec);
	            

				Message messageResponse = new Message(Base64.getEncoder().encodeToString(chi.getPublicKey().getEncoded()), Base64.getEncoder().encodeToString(pubKey.getEncoded()), rsp);
				CipheredMessage cipheredResponse = cryptoManager.makeCipheredMessage(messageResponse, pubKey);
				System.out.println("QQQQQQQQQQQQQQ"+cipheredMessage.getKey().length);
				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				oos.writeObject(cipheredResponse);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (client != null) {
					try { client.close(); }
					catch (Exception e) {}
				}
			}
		}
	}
}
