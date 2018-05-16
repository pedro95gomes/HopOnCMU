package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class SignUpCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
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