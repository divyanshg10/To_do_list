package com.dcodestar.tasktimer;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;

    interface OnTaskClickListener{
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }

    OnTaskClickListener onTaskClickListener;
    public CursorRecyclerViewAdapter(Cursor mCursor,OnTaskClickListener onTaskClickListener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called");
        this.onTaskClickListener=onTaskClickListener;
        this.mCursor = mCursor;
    }

//    public void setOnTaskClickListener(OnTaskClickListener onTaskClickListener) {
//        this.onTaskClickListener = onTaskClickListener;
//    }

    @NonNull
    @Override
    public CursorRecyclerViewAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_list_items,viewGroup,false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursorRecyclerViewAdapter.TaskViewHolder taskViewHolder, int i) {
//        Log.d(TAG, "onBindViewHolder: starts");
        if(mCursor==null||mCursor.getCount()==0){
            Log.d(TAG, "onBindViewHolder: providing instructions");
            taskViewHolder.name.setText(R.string.instructions_heading);
            taskViewHolder.description.setText(R.string.instructions);
            taskViewHolder.editButton.setVisibility(View.GONE);
            taskViewHolder.deleteButton.setVisibility(View.GONE);
        }else{
            if(!mCursor.moveToPosition(i)){
                throw new IllegalStateException("Couldn't move cursor to position "+i);
            }else{
                final Task task=new Task(mCursor.getLong(mCursor.getColumnIndex(TasksContract.Columns._ID)),
                        mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                        mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)),
                        mCursor.getInt(mCursor.getColumnIndex(TasksContract.Columns.TASKS_SORTORDER)));

                taskViewHolder.name.setText(task.getmName());
                taskViewHolder.description.setText(task.getmDescription());
                taskViewHolder.editButton.setVisibility(View.VISIBLE);
                taskViewHolder.deleteButton.setVisibility(View.VISIBLE);

                View.OnClickListener onClickListener=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.d(TAG, "onClick: starts");
                        switch (v.getId()){
                            case R.id.tli_edit:
                                if(onTaskClickListener!=null) {
                                    onTaskClickListener.onEditClick(task);
                                }
                                break;

                            case R.id.tli_delete:
                                if(onTaskClickListener!=null) {
                                    onTaskClickListener.onDeleteClick(task);
                                }
                                break;

                            default:
                                Log.d(TAG, "onClick: found unexpected button id");
                        }

//                        Log.d(TAG, "onClick: button with id "+v.getId()+" get clicked");
//                        Log.d(TAG, "onClick: task name is "+task.getmName());
                    }
                };
                taskViewHolder.editButton.setOnClickListener(onClickListener);
                taskViewHolder.deleteButton.setOnClickListener(onClickListener);
            }
        }
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: starts");
        if(mCursor==null||mCursor.getCount()==0){
            return 1;
        }else{
            return mCursor.getCount();
        }
    }

    Cursor swapCursor(Cursor newCursor){
        if(newCursor==mCursor){
            return null;
        }
        final Cursor oldCursor=mCursor;
        mCursor=newCursor;
        if(newCursor!=null){
            notifyDataSetChanged();
        }else{
            notifyItemRangeRemoved(0,getItemCount());
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "TaskViewHolder";
        TextView name=null;
        TextView description=null;
        ImageButton editButton=null;
        ImageButton deleteButton=null;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
//            Log.d(TAG, "TaskViewHolder: starts");
            name=itemView.findViewById(R.id.tli_name);
            description=itemView.findViewById(R.id.tli_description);
            editButton=itemView.findViewById(R.id.tli_edit);
            deleteButton=itemView.findViewById(R.id.tli_delete);
        }
    }
}
