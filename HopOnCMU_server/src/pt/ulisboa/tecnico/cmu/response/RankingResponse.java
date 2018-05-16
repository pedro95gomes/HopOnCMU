package pt.ulisboa.tecnico.cmu.response;

import java.util.List;

public class RankingResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private List<String> ranking_list;

    public RankingResponse(List<String> rank){
        ranking_list = rank;
    }

    public List<String> getRanking(){
        return ranking_list;
    }
}