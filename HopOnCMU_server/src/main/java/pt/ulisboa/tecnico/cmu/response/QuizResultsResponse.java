package pt.ulisboa.tecnico.cmu.response;

import java.util.Map;

public class QuizResultsResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private Map<String, Integer> results;
    private Map<String, Integer> numQuestions;
    
    public QuizResultsResponse(Map<String, Integer> results, Map<String, Integer> numQuestions){
    	this.results = results;
    	this.numQuestions = numQuestions;
    }
    
    public Map<String,Integer> getResults() {
    	return this.results;
    }
    
    public Map<String, Integer> getnumQuestions(){
    	return this.numQuestions;
    }
}
