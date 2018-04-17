package pt.ulisboa.tecnico.cmu.response;

public class QuizResultsResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private double result;
    
    public QuizResultsResponse(double result){
    	this.result = result;
    }
    
    public double getResult() {
    	return this.result;
    }
}
