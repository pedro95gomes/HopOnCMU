package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class LogInCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
    private String password;
    private String username;

    public LogInCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }
}
