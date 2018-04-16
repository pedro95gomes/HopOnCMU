package pt.ulisboa.tecnico.cmu.response;

import java.util.List;

public class DownloadQuestionsResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    List<String[]> questions;
    
   public DownloadQuestionsResponse(List<String[]> questions){
	   this.questions = questions;
   }
   
   public List<String[]> getQuestions(){
	   return this.questions;
   }
}
