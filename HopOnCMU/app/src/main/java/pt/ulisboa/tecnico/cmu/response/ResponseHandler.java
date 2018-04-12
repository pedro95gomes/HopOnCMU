package pt.ulisboa.tecnico.cmu.response;

public interface ResponseHandler {
	public void handle(HelloResponse hr);
	public void handle(SignUpResponse sur);
}
