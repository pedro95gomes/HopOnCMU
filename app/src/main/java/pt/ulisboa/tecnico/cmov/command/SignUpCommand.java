package pt.ulisboa.tecnico.cmov.command;

import pt.ulisboa.tecnico.cmov.response.Response;

public class SignUpCommand implements Command {

    private String busCode;
    private String username;

    public SignUpCommand(String username, String busCode) {
        this.username = username;
        this.busCode = busCode;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getUsername() {
        return this.username;
    }

    public String getBusCode(){
        return this.busCode;
    }
}