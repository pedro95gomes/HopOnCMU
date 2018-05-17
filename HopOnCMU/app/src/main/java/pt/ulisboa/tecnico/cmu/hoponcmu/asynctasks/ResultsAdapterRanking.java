package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.List;
import java.util.Map;

public class ResultsAdapterRanking extends BaseAdapter {

    private Context context;
    private List<String> ranking_list;

    public ResultsAdapterRanking(Context context, List<String> l){
        this.context = context;
        ranking_list=l;
    }

    @Override
    public int getCount() {
        return ranking_list.size();
    }

    @Override
    public Object getItem(int i) {
        return ranking_list.get(i);
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

        String[] parts = ranking_list.get(i).split("\\|");
        String position = parts[0];
        String name = parts[1];
        String correct_answers = parts[2];
        String time = parts[3];

        view1.setText(position + " - " + name);
        view2.setText(correct_answers + " correct answers " + "| Total time: " + time + " milliseconds");

        return twoLineListItem;
    }
}