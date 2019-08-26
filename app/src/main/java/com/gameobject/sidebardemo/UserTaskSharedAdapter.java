package com.gameobject.sidebardemo;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class UserTaskSharedAdapter extends RecyclerView.Adapter<UserTaskSharedAdapter.UserTaskViewHolder> {

    ArrayList<String> taskList;
    ArrayList<String> taskSubmittedByList;

    public UserTaskSharedAdapter(ArrayList<String> list1 ,ArrayList<String> list2, Context context) {
        this.taskList = list1;
        this.taskSubmittedByList = list2;
    }

    @Override
    public UserTaskSharedAdapter.UserTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_taskshareditem,parent,false);
        UserTaskViewHolder viewHolder=new UserTaskViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserTaskSharedAdapter.UserTaskViewHolder holder, int position) {
        holder.task.setText(taskList.get(position).toString());
        holder.byuser.setText(taskSubmittedByList.get(position).toString());


    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void removeItem(int position) {
        taskList.remove(position);
        taskSubmittedByList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(String item1,String item2, int position) {
        taskList.add(position, item1);
        taskSubmittedByList.add(position,item2);
        notifyItemInserted(position);
    }

    public static class UserTaskViewHolder extends RecyclerView.ViewHolder{

        protected TextView task;
        protected TextView byuser;

        public RelativeLayout viewBackground, viewForeground;

        public UserTaskViewHolder(View itemView) {
            super(itemView);
            task= (TextView) itemView.findViewById(R.id.tasksharedid);
            byuser= (TextView) itemView.findViewById(R.id.username);

            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }
}