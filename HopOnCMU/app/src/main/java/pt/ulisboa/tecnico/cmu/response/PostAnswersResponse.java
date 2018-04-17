package pt.ulisboa.tecnico.cmu.response;

public class PostAnswersResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private boolean success;
    
    public PostAnswersResponse(boolean success){
    	this.success = success;
    }
    
    public boolean getSuccess() {
    	return this.success;
    }
}
