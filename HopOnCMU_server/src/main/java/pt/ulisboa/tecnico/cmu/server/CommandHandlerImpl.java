package pt.ulisboa.tecnico.cmu.server;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pt.ulisboa.tecnico.cmu.command.*;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.domain.Quizz;
import pt.ulisboa.tecnico.cmu.domain.ServerUtil;
import pt.ulisboa.tecnico.cmu.response.*;

public class CommandHandlerImpl implements CommandHandler {

	ServerUtil sv = new ServerUtil();
	private PrivateKey privkey = null;
	private PublicKey pubkey = null;
	private static String certificateFilePath = "";
	private static String keystoreFilePath = "";

	public CommandHandlerImpl(){
		char[] keyPassword = "securepwd".toCharArray(); //FIXME?
		char[] keyStorePassword = keyPassword;
		File f = new File(System.getProperty("user.dir"));
		certificateFilePath = f.getPath()+"/keys/server/server.cer";
		keystoreFilePath = f.getPath()+"/keys/server/server.jks";

		setKeys(certificateFilePath, keystoreFilePath, keyStorePassword, keyPassword);
	}

	public PublicKey getPublicKey() {
		return this.pubkey;
	}

	public PrivateKey getPrivateKey() {
		return this.privkey;
	}

	/********************************************/
	/*               HANDLERS                   */
	/********************************************/
	@Override
	public Response handle(HelloCommand hc) {
		System.out.println("Received: " + hc.getMessage());
		return new HelloResponse("Hi from Server!");
	}

	@Override
	public Response handle(SignUpCommand suc){
		System.out.println("Username:" + suc.getUsername() + " | Code: " + suc.getBusCode());

		String code = suc.getBusCode();
		System.out.println(code);

		String username = suc.getUsername();
		if (sv.verifyCode(code) && sv.verifyUsername(username)){
			sv.registerUser(suc.getUsername(), code);
			sv.addUsedCode(code);
			sv.saveCodes();
			return new SignUpResponse(suc.getUsername(),code);
		}

		return new SignUpResponse(suc.getUsername(),null);
	}

	@Override
	public Response handle(LogInCommand lginc){
		System.out.println("Username:" + lginc.getUsername() + " | Password: " + lginc.getPassword());
		String sessionId = null;
		// Check if password/busCode for user is correct
		if(sv.isPassword(lginc.getUsername(), lginc.getPassword())) {
			UUID uuid = UUID.randomUUID();
			sessionId = uuid.toString();
			System.out.println(sessionId);
			sv.setSessionId(lginc.getUsername(), sessionId); // Generate and set user session Id
			
		}
		LogInResponse logedIn =  new LogInResponse(lginc.getUsername(), sessionId);
		return logedIn;
	}

	@Override
	public Response handle(ListLocationsCommand llc){
		System.out.println("Getting tour locations...");
		// Obter localização dos spots da tour
		Map<String,String> locations = sv.getTourLocations();
		ListLocationsResponse listLocations = new ListLocationsResponse(locations);
		return listLocations;
	}

	@Override
	public Response handle(LogOutCommand lgoutc){
		System.out.println("Logging out... sessionID "+ lgoutc.getSessionId());

		// Removes sessionID associated with user X
		String logoutuser = sv.revokeSessionId(lgoutc.getSessionId());
		LogOutResponse loggedOut = new LogOutResponse(lgoutc.getSessionId(), logoutuser);

		return loggedOut;
	}

	@Override
	public Response handle(DownloadQuestionsCommand dqc){
		System.out.println("Getting quizz questions...");

		List<String[]> questions = null;
		// Gets questions from quizz Y
		List<Quizz> quizzes = sv.getQuizzes();
		for(Quizz quizz : quizzes) {
			if(quizz.getName().equals(dqc.getName())) {
				questions = quizz.getQuestions();
			}
		}
		DownloadQuestionsResponse response = new DownloadQuestionsResponse(questions);

		return response;
	}

	@Override
	public Response handle(PostAnswersCommand pac){
		System.out.println("Submiting tourist answers for " + pac.getQuizzName());

		// Calculates results for User X in quizz Y
		String name = pac.getQuizzName() + ".txt";
		List<Quizz> quizzes = sv.getQuizzes();
		boolean success = false;
		for(Quizz quizz : quizzes) {
			if(quizz.getName().equals(name)) {
				sv.setUserAnswers(pac.getSessionId(), quizz.getName(), pac.getAnswers(), pac.getTime_taken());
				success = true;
			}
		}
		PostAnswersResponse postResponse = new PostAnswersResponse(success);

		return postResponse;
	}

	@Override
	public Response handle(QuizResultsCommand qrc){
		System.out.println("Getting quizz results...");

		// Gets results for user X in quiz Y
		List<Quizz> quizzes = sv.getQuizzes();
		Map<String, Integer> results = new HashMap<String, Integer>();
		Map<String, Integer> numQuestions = new HashMap<String,Integer>();
		String[] answered_quizes = qrc.getQuizzName();
		for(Quizz quizz : quizzes) {
			for(String name : answered_quizes){
				if(quizz.getName().equals(name)){
					int result = sv.checkAnswers(qrc.getUserSSID(), quizz);
					if(result!=-1) {
						results.put(name, result);
						numQuestions.put(name, quizz.getNumQuestions());
					}
				}
        		/*else{
        			results.put(quizz.getName(), 0);
        			numQuestions.put(quizz.getName(), quizz.getNumQuestions());
        		}*/
			}
		}
		QuizResultsResponse response = new QuizResultsResponse(results, numQuestions);

		return response;
	}

	@Override
	public Response handle(RankingCommand rank){
		System.out.println("Getting ranking...");
		List<String> ranking = sv.getRanking();	//Método que vai devolver o ranking ordenado
		RankingResponse rank_response = new RankingResponse(ranking);
		return rank_response;
	}

	/********************************************/
	/*               AUX                        */
	/********************************************/
	private void setKeys(String certificateFilePath, String keyStoreFilePath, char[] keyStorePassword, char[] keyPassword){
		Certificate certificate;
		try {
			certificate = CryptoUtil.getX509CertificateFromFile(certificateFilePath);
			PrivateKey privKey = CryptoUtil.getPrivateKeyFromKeyStoreFile(keyStoreFilePath, keyStorePassword, "server", keyPassword);
			PublicKey pubKey = certificate.getPublicKey();
			this.privkey = privKey;
			this.pubkey = pubKey;
		} catch (CertificateException | UnrecoverableKeyException | KeyStoreException | IOException e) {
			e.printStackTrace();
			return;
		}
	}

}
