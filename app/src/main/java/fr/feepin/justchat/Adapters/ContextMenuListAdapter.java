package fr.feepin.justchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import fr.feepin.justchat.R;
import fr.feepin.justchat.Shared;

public class ContextMenuListAdapter extends BaseAdapter {
    private OnItemClicked onItemClicked;
    private Context context;
    private String [] strings;
    private LayoutInflater layoutInflater;

    public ContextMenuListAdapter(Context context, OnItemClicked onItemClicked){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.onItemClicked = onItemClicked;

        if (Shared.hostname.equals(Shared.userName)){
            strings = new String[]{
                    context.getResources().getString(R.string.kick),
            };
        }else{
            strings = new String[]{

            };
        }

    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public String getItem(int position) {
        return strings[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.context_menu_item, null);
        final TextView textView = convertView.findViewById(R.id.text);
        textView.setText(strings[position]);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.clicked(v, textView.getText().toString());
            }
        });

        return convertView;
    }


    //Intefaces
    public interface OnItemClicked{
        void clicked(View v, String itemText);
    }

}
