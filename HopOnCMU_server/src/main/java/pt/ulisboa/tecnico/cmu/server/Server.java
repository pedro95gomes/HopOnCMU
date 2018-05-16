package pt.ulisboa.tecnico.cmu.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
		CryptoManager cryptoManager = CryptoManager.getInstance(chi.getPublicKey(), chi.getPrivateKey());

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

				Message messageResponse = new Message(cryptoManager.getPublicKey(), decipheredMessage.getSender(), rsp);
				CipheredMessage cipheredResponse = cryptoManager.makeCipheredMessage(messageResponse, decipheredMessage.getSender());

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
