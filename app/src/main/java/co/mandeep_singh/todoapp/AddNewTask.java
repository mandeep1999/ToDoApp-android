package co.mandeep_singh.todoapp;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewTask";
    private EditText mEditText;
    private Button mSaveButton;
    private FirebaseFirestore firestore;
    private Context context;
    private String id;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditText = view.findViewById(R.id.task_edittext);
        mSaveButton = view.findViewById(R.id.saveButton);

        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle !=null){
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");

            mEditText.setText(task);
            if(task.length() > 0){
                mSaveButton.setEnabled(false);
                mSaveButton.setBackgroundColor(Color.GRAY);
            }

        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if(s.toString().equals("")){
                   mSaveButton.setEnabled(false);
                   mSaveButton.setBackgroundColor(Color.GRAY);
               }
               else{
                   mSaveButton.setEnabled(true);
                   mSaveButton.setBackgroundColor(getResources().getColor(R.color.green_blue));
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String task = mEditText.getText().toString();
               if(finalIsUpdate){
                 firestore.collection("task").document(id).update("task",task);
                 Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
               }
               else{
               if(task.isEmpty()){
                   Toast.makeText(context, "Empty task not allowed", Toast.LENGTH_SHORT).show();
               }
               else {
                   Map<String, Object> taskMap = new HashMap<>();
                   taskMap.put("task" ,task);
                   taskMap.put("status", 0);
                   taskMap.put("time", FieldValue.serverTimestamp());
                   firestore.collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                       @Override
                       public void onComplete(@NonNull Task<DocumentReference> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(context,"Task Saved",Toast.LENGTH_SHORT).show();
                           }
                           else {
                               Toast.makeText(context, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                           }
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
               }
               }
               dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
    }
}
