package com.dcodestar.tasktimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,AddEditActivityFragment.OnSaveClicked,AppDialog.DialogEvents{
    private static final String TAG = "MainActivity";

    private boolean mtwoPane=false;

    public static final int DIALOG_ID_DELETE =1;
    public static final int DIALOG_ID_CANCEL_EDIT  =2;
    private static final int DIALOG_ID_CANCEL_EDIT_UP=3;
    private AlertDialog mDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mtwoPane=(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE);
        Log.d(TAG, "onCreate: twoPane is "+mtwoPane);
        
        FragmentManager fragmentManager=getSupportFragmentManager();
        //If the addeditactivity fragment exists, We are editing
        Boolean editing=fragmentManager.findFragmentById(R.id.task_details_container)!=null;
        Log.d(TAG, "onCreate: editing is "+editing);
        
        View addEditLayout=findViewById(R.id.task_details_container);
        View mainFragment=findViewById(R.id.fragment);
        
        if(mtwoPane){
            Log.d(TAG, "onCreate: twoPane mode");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.VISIBLE);
        }else if(editing){
            Log.d(TAG, "onCreate: single pane, editing");
            mainFragment.setVisibility(View.GONE);
        }else{
            Log.d(TAG, "onCreate: single pane,not editing");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager=getSupportFragmentManager();
        Fragment fragment=fragmentManager.findFragmentById(R.id.task_details_container);
        if(fragment!=null){
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        View addEditLayout=findViewById(R.id.task_details_container);
        View mainFragment=findViewById(R.id.fragment);
        if(!mtwoPane){
            addEditLayout.setVisibility(View.GONE);
            mainFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: on home button pressed");
                AddEditActivityFragment fragment=(AddEditActivityFragment)getSupportFragmentManager().findFragmentById(R.id.task_details_container);
                if(fragment.canClose()){
                    return super.onOptionsItemSelected(item);
                }else{
                    showConfirmationDialog(DIALOG_ID_CANCEL_EDIT_UP);
                    return true;
                }

            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;

            case R.id.menumain_showDurations:
                break;

            case R.id.menumain_settings:
                break;

            case R.id.menumain_showAbout:
                showAboutDialog();
                break;

            case R.id.menumain_generate:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAboutDialog(){
        View messageView=getLayoutInflater().inflate(R.layout.about,null,false);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(messageView);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.d(TAG, "onClick: entering messageView.onClick, showing ="+mDialog.isShowing());
                if(mDialog!=null&&mDialog.isShowing()){
                    mDialog.dismiss();
                }
            }
        });

        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher_round);
        mDialog=builder.create();
        mDialog.setCanceledOnTouchOutside(true);


        TextView tv=messageView.findViewById(R.id.about_version);
        tv.setText("v"+BuildConfig.VERSION_NAME);

        mDialog.show();
    }


    @Override
    public void onEditClick(Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: starts");

        AppDialog dialog=new AppDialog();
        Bundle args=new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE,getString(R.string.deleting_message,task.getId(),task.getmName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID,R.string.deldiag_positive_caption);
        args.putLong("TaskID",task.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);


    }

    private void taskEditRequest(Task task){
        Log.d(TAG, "taskEditRequest: starts");
        Log.d(TAG, "taskEditRequest: in two-pane mode");
        AddEditActivityFragment fragment=new AddEditActivityFragment();

        Bundle arguments=new Bundle();
        arguments.putSerializable(Task.class.getSimpleName(),task);
        fragment.setArguments(arguments);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();


        fragmentTransaction.replace(R.id.task_details_container,fragment);
        fragmentTransaction.commit();
           
        
        if(!mtwoPane){
            Log.d(TAG, "taskEditRequest: in single-pane mode");
            View mainFragment=findViewById(R.id.fragment);
            View addEditLayout=findViewById(R.id.task_details_container);
            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "taskEditRequest: exiting taskeditrequest");
    }


    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPosititeDialogResult: called");
        switch (dialogId) {
            case DIALOG_ID_DELETE: {
                Long taskId = args.getLong("TaskID");
                if (BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Task ID is zero");
                getContentResolver().delete(TasksContract.buildTaskUri(taskId), null, null);
                break;
            }
            case DIALOG_ID_CANCEL_EDIT:{
                //no action required
                break;
            }
            case DIALOG_ID_CANCEL_EDIT_UP:
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
                //no action required
                break;

            case DIALOG_ID_CANCEL_EDIT_UP:
            case DIALOG_ID_CANCEL_EDIT:
                FragmentManager fragmentManager=getSupportFragmentManager();
                Fragment fragment=fragmentManager.findFragmentById(R.id.task_details_container);
                if(fragment!=null){
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment).commit();
                    if(mtwoPane){
                        if(dialogId==DIALOG_ID_CANCEL_EDIT) {
                            finish();
                        }
                    }else{
                        View addEditLayout=findViewById(R.id.task_details_container);
                        View mainFragment=findViewById(R.id.fragment);
                        addEditLayout.setVisibility(View.GONE);
                        mainFragment.setVisibility(View.VISIBLE);
                    }
                }else{
                    finish();
                }
                break;
        }
    }
    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager=getSupportFragmentManager();
        AddEditActivityFragment fragment=(AddEditActivityFragment)fragmentManager.findFragmentById(R.id.task_details_container);
        if(fragment==null||fragment.canClose()){
            super.onBackPressed();
        }else{
            //show dialog
            showConfirmationDialog(DIALOG_ID_CANCEL_EDIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    private void showConfirmationDialog(int dialogId){
        //show dialog
        AppDialog dialog=new AppDialog();
        Bundle args=new Bundle();
        args.putInt(AppDialog.DIALOG_ID, dialogId);
        args.putString(AppDialog.DIALOG_MESSAGE,getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID,R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID,R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);
    }
}
