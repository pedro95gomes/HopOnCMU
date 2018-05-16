package pt.ulisboa.tecnico.cmu.domain;

import java.util.ArrayList;
import java.util.List;

public class Quizz {

	String name;
	List<String[]> questions;
	List<Integer> results;
	
	public Quizz(String name, List<String[]> questions) {
		this.name = name;
		this.questions = questions;
		results = new ArrayList<Integer>(questions.size());
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<String[]> getQuestions(){
		return this.questions;
	}
	
	public List<Integer> getResults(){
		return this.results;
	}
	
	public void setResult(String question, int result) {
		results.set(getIndexQuestion(question), result);
	}
	
	public void setResults(List<Integer> results) {
		this.results = results;
	}
	
	public String getAnswer(String question){
		for(String[] q : this.questions) {
			if(question.equals(q[0])) {
				return q[5];
			}
		}
		return "";
	}
	
	public String getAnswer(int numQuest) {
		return this.questions.get(numQuest)[5];
	}
	
	public int getIndexQuestion(String question) {
		for(int i=0; i < this.questions.size(); i++) {
			if(question.equals(this.questions.get(i)[0])) {
				return i;
			}
		}
		return -1;
	}
	
	public int getNumQuestions(){
		return this.questions.size();
	}
}
