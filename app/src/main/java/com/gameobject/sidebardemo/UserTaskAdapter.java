package com.gameobject.sidebardemo;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class UserTaskAdapter extends RecyclerView.Adapter<UserTaskAdapter.UserTaskViewHolder> {

    ArrayList<String> taskList;

    public UserTaskAdapter(ArrayList<String> planetList, Context context) {
        this.taskList = planetList;
    }

    @Override
    public UserTaskAdapter.UserTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        UserTaskViewHolder viewHolder=new UserTaskViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserTaskAdapter.UserTaskViewHolder holder, int position) {
        holder.text.setText(taskList.get(position).toString());


    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void removeItem(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(String item, int position) {
        taskList.add(position, item);
        notifyItemInserted(position);
    }

    public static class UserTaskViewHolder extends RecyclerView.ViewHolder{

        protected TextView text;

        public RelativeLayout viewBackground, viewForeground;

        public UserTaskViewHolder(View itemView) {
            super(itemView);
            text= (TextView) itemView.findViewById(R.id.text_id);

            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }
}