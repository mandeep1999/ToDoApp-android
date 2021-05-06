package co.mandeep_singh.todoapp.Adapter;
import co.mandeep_singh.todoapp.AddNewTask;
import co.mandeep_singh.todoapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import co.mandeep_singh.todoapp.MainActivity;
import co.mandeep_singh.todoapp.Modal.ToDoModel;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> toDoList;
    private MainActivity activity;
    private FirebaseFirestore firestore;

    public ToDoAdapter(MainActivity mainActivity, List<ToDoModel> toDoList){
        this.toDoList = toDoList;
        activity = mainActivity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.each_task,parent,false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    public void deleteTask(int position){
        ToDoModel toDoModel = toDoList.get(position);
        firestore.collection("task").document(toDoModel.TaskId).delete();
        toDoList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext(){
        return activity;
    }
    public void editTask(int position){
        ToDoModel toDoModel = toDoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("task",toDoModel.getTask());
        bundle.putString("id",toDoModel.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager(), addNewTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
         ToDoModel toDoModel = toDoList.get(position);
         holder.mCheckBox.setText(toDoModel.getTask());
         holder.mCheckBox.setChecked(toDoModel.getStatus()==1);
         holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked){
                     firestore.collection("task").document(toDoModel.TaskId).update("status",1);
                 }
                 else{
                     firestore.collection("task").document(toDoModel.TaskId).update("status",0);
                 }
             }
         });
    }



    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox mCheckBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
        }
    }
}
