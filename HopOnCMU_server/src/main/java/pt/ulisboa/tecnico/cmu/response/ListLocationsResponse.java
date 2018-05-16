package pt.ulisboa.tecnico.cmu.response;

import java.util.List;

public class ListLocationsResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private List<String> tourlocations;

    public ListLocationsResponse(List<String> tourlocations){
        this.tourlocations = tourlocations;
    }

    public List<String> getLocations(){
        return this.tourlocations;
    }
}
