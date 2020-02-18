package com.dcodestar.tasktimer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    private enum FragmentEditMode{EDIT,ADD}
    private FragmentEditMode mMode;

    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mSaveButton;
    private OnSaveClicked msaveListener =null;

    interface OnSaveClicked{
        void onSaveClicked();
    }


        public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);
        Activity activity=getActivity();
        if(! (activity instanceof OnSaveClicked)){
            throw new ClassCastException(activity.getClass().getSimpleName()
            +" must implement AddEditActivityFragment.OnsaveClicked interface");
        }
        msaveListener=(OnSaveClicked)activity;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        msaveListener=null;
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view= inflater.inflate(R.layout.fragment_add_edit, container, false);
        mNameTextView=view.findViewById(R.id.addedit_name);
        mDescriptionTextView=view.findViewById(R.id.addedit_description);
        mSortOrderTextView=view.findViewById(R.id.addedit_sortorder);
        mSaveButton=view.findViewById(R.id.addedit_save);


//        Bundle arguments=getActivity().getIntent().getExtras();//This is line we will change later
        Bundle arguments=getArguments();

        final Task task;
        if(arguments!=null){
            Log.d(TAG, "onCreateView: retrieving task details");

            task=(Task)arguments.getSerializable(Task.class.getSimpleName());
            if(task!=null){
                Log.d(TAG, "onCreateView: task details found, editting");
                mNameTextView.setText(task.getmName());
                mDescriptionTextView.setText(task.getmDescription());
                mSortOrderTextView.setText(Integer.toString(task.getmSortOrder()));
                mMode=FragmentEditMode.EDIT;
            }else{
                mMode=FragmentEditMode.ADD;
            }
        }else{
            task=null;
            Log.d(TAG, "onCreateView: No arguments, adding new record");
            mMode=FragmentEditMode.ADD;
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int so;// to save repeated conversions to int
                if(mSortOrderTextView.length()>0){
                    so=Integer.parseInt(mSortOrderTextView.getText().toString());
                }else{
                    so=0;
                }

                ContentResolver contentResolver=getActivity().getContentResolver();
                ContentValues values=new ContentValues();

                switch (mMode){
                    case EDIT:
                        if(task==null){
                            break;
                        }
                        if(!mNameTextView.getText().toString().equals(task.getmName())){
                            values.put(TasksContract.Columns.TASKS_NAME,mNameTextView.getText().toString());
                        }
                        if(!mDescriptionTextView.getText().toString().equals(task.getmDescription())){
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION,mDescriptionTextView.getText().toString());
                        }
                        if(so!=task.getmSortOrder()){
                            values.put(TasksContract.Columns.TASKS_SORTORDER,so);
                         }
                        if(values.size()!=0){
                            Log.d(TAG, "onClick: updating task");
                            contentResolver.update(TasksContract.buildTaskUri(task.getId()),values,null,null);
                        }
                        break;

                    case ADD:
                        if(mNameTextView.length()>0){
                            Log.d(TAG, "onClick: adding new task");
                            values.put(TasksContract.Columns.TASKS_NAME,mNameTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION,mDescriptionTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_SORTORDER,so);
                            contentResolver.insert(TasksContract.CONTENT_URI,values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: done editing");

                if(msaveListener!=null){
                    msaveListener.onSaveClicked();
                }
            }
        });
        Log.d(TAG, "onCreateView: exiting......");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    boolean canClose(){
        return false;
    }
}
