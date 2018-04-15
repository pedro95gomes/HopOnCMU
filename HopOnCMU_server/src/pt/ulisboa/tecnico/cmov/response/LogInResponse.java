package pt.ulisboa.tecnico.cmu.response;

public class LogInResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private String username, sessionId;
    private boolean login_success;

    public LogInResponse(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;

        //Implementar a logica aqui (?)
        /*
        if(username exists)
            register_success = true;
        else
            register_success = false;
        */

        login_success = false;   //só para testar. Depois apagar
    }

    public String getUsername() {
        return this.username;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public boolean getSuccess(){
        return this.login_success;
    }

}
