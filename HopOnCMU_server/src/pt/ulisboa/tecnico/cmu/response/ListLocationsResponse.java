package pt.ulisboa.tecnico.cmu.response;

import java.util.ArrayList;

public class ListLocationsResponse implements Response{
    private static final long serialVersionUID = 734457624276534179L;
    private ArrayList<String> tourlocations = new ArrayList<String>();

    public ListLocationsResponse(ArrayList<String> tourlocations){
        this.tourlocations = tourlocations;
    }

    public ArrayList<String> getLocations(){
        return this.tourlocations;
    }
}
