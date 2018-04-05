package pt.ulisboa.tecnico.cmov.response;

public interface ResponseHandler {
	public void handle(HelloResponse hr);
	public void handle(SignUpResponse sur);
}
