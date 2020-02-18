package com.dcodestar.tasktimer;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,CursorRecyclerViewAdapter.OnTaskClickListener {
    private static final String TAG = "MainActivityFragment";

    public static final int LOADER_ID=0;
    private CursorRecyclerViewAdapter mAdapter;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);

        Activity activity=getActivity();
        if(! (activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener)){
            throw new ClassCastException(activity.getClass().getSimpleName()
                    +" must implement CursorRecyclerViewAdapter.OnTaskClickListener interface");
        }
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public void onEditClick(Task task) {
        Log.d(TAG, "onEditClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener=(CursorRecyclerViewAdapter.OnTaskClickListener)getActivity();
        if (listener!=null){
            listener.onEditClick(task);
        }
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "oDeleteClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener=(CursorRecyclerViewAdapter.OnTaskClickListener)getActivity();
        if (listener!=null){
            listener.onDeleteClick(task);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view= inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView=view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(mAdapter==null)
            mAdapter=new CursorRecyclerViewAdapter(null,this);
//        else{
//            mAdapter.setOnTaskClickListener((CursorRecyclerViewAdapter.OnTaskClickListener)getActivity());
//        }
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "onCreateView: returning");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.d(TAG, "onCreateLoader: starts with id "+i);
        String []projection={TasksContract.Columns._ID,TasksContract.Columns.TASKS_NAME,TasksContract.Columns.TASKS_DESCRIPTION,TasksContract.Columns.TASKS_SORTORDER};
        String sortOrder=TasksContract.Columns.TASKS_SORTORDER+","+TasksContract.Columns.TASKS_NAME;

        switch (i){
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TasksContract.CONTENT_URI,
                        projection,null,null,sortOrder);

            default:
                throw new InvalidParameterException(TAG+" .onCreateLoader called with invalid id "+i);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished: entering");
        mAdapter.swapCursor(cursor);
        int count=mAdapter.getItemCount();
        Log.d(TAG, "onLoadFinished: count is "+count);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: called");
        mAdapter.swapCursor(null);
    }
}
