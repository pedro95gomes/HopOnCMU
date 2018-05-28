package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.Map;

public class ResultsAdapter extends BaseAdapter {

    private Context context;
    private String[] files;
    private Map<String, Integer> results;
    Map<String, Integer> numQ;

    public ResultsAdapter(Context context, String[] files, Map<String, Integer> results, Map<String, Integer> numQ){
        this.context = context;
        this.files = files;
        this.results = results;
        this.numQ = numQ;
    }

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int i) {
        return files[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TwoLineListItem twoLineListItem;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) view;
        }

        TextView view1 = twoLineListItem.getText1();
        TextView view2 = twoLineListItem.getText2();

        if(results.get(files[i])!=null) {
            view1.setText(files[i]);
            view2.setText("Result: " + results.get(files[i]) + " correct answer in " + numQ.get(files[i]) + " questions");
        } else{
            view1.setText(files[i]);
            view2.setText("Unanswered");
        }

        return twoLineListItem;
    }
}