package pt.ulisboa.tecnico.cmu.response;

import java.util.Map;

public class ListLocationsResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private Map<String,String> tourlocations;

    public ListLocationsResponse(Map<String,String> tourlocations){
        this.tourlocations = tourlocations;
    }

    public Map<String,String> getLocations(){
        return this.tourlocations;
    }
}
