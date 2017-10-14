package com.example.parktaeim.cameraforblind.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parktaeim.cameraforblind.Model.SettingsItem;
import com.example.parktaeim.cameraforblind.R;

import java.util.ArrayList;

/**
 * Created by parktaeim on 2017. 10. 14..
 */

public class SettingsRecyclerViewAdapter extends RecyclerView.Adapter<SettingsRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SettingsItem> settingsItems;

    public SettingsRecyclerViewAdapter(ArrayList<SettingsItem> items, Context context){
        this.settingsItems = items;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_settings,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SettingsItem item =  settingsItems.get(position);
        holder.title.setText(item.getTitle());
        holder.icon.setImageResource(item.getIcon());
        holder.itemView.setTag(item);
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView title;
        public ImageView icon;
        public View.OnClickListener onClickListener;

        public ViewHolder(View view){
            super(view);
            icon = (ImageView) view.findViewById(R.id.itemIcon);
            title = (TextView) view.findViewById(R.id.itemTextView);
        }
    }

    @Override
    public int getItemCount() {
        return settingsItems.size();
    }
}
