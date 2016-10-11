package pa1pal.todo.task.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pa1pal.todo.task.MainActivity;
import pa1pal.todo.task.R;
import pa1pal.todo.task.adapter.TaskAdapter;
import pa1pal.todo.task.callback.ApiManager;
import pa1pal.todo.task.pojo.Datum;
import pa1pal.todo.task.pojo.TodoPojo;
import pa1pal.todo.task.utils.RecyclerItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoneFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener{

    private ApiManager apiManager;
    private TodoPojo mTodo;
    public List<Datum> mDoneTaskList, tempList;
    TaskAdapter donetodoAdapter;
    RecyclerView todoList;
    SwipeRefreshLayout swipeRefreshLayout;
    Datum d;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    @BindView(R.id.done_todo_list)
    RecyclerView donerecyclerView;

    public DoneFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionModeCallback = new ActionModeCallback();
        d = new Datum();
        mTodo = new TodoPojo();
        mDoneTaskList = new ArrayList<Datum>();
        tempList = new ArrayList<Datum>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_done, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.doneswipelayout);
        ButterKnife.bind(this, rootview);
        donerecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DoneFragment();
                mDoneTaskList.clear();
                loadTodo();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.addtodofab_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCreateDialog();
            }
        });

        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        donerecyclerView = (RecyclerView) view.findViewById(R.id.done_todo_list);
        donerecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadTodo();
    }

    public void loadTodo(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        apiManager = new ApiManager();
        Call<TodoPojo> reviewsCall = apiManager.getApi().getTodo();
        reviewsCall.enqueue(new Callback<TodoPojo>() {
            @Override
            public void onResponse(Call<TodoPojo> call, Response<TodoPojo> response) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if(response.isSuccessful()){
                    mTodo = response.body();
                    Collection<Datum> data = mTodo.getData();
                    for (int i=0; i<data.size(); i++){
                        if (mTodo.getData().get(i).getState() == 1){
                            mDoneTaskList.add(mTodo.getData().get(i));
                        }
                    }
                    donetodoAdapter = new TaskAdapter(getActivity(), mDoneTaskList);
                    donetodoAdapter.notifyDataSetChanged();
                    donerecyclerView.setAdapter(donetodoAdapter);
                }
            }

            @Override
            public void onFailure(Call<TodoPojo> call, Throwable t) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }

        });
    }

    private void displayCreateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getResources().getString(R.string.title_dialog_new_list));

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_input, null);
        final EditText input = (EditText) view.findViewById(R.id.text);
        final CheckBox state = (CheckBox) view.findViewById(R.id.stateCheckbox);
        alert.setView(view);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = input.getText().toString();
                if (title.length() == 1)
                    return;
                else{
                    d.setName(title);
                    if (state.isChecked()){
                        mDoneTaskList.add(d);
                        Toast.makeText(getActivity(), "Added in the done list", Toast.LENGTH_LONG).show();
                    }else {
                        //TODO
                    }
                    donetodoAdapter.notifyDataSetChanged();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }



    /**
     * Toggle the selection state of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        donetodoAdapter.toggleSelection(position);
        int count = donetodoAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onItemClick(View childView, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getResources().getString(R.string.delete));
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                d = mDoneTaskList.get(position);
                mDoneTaskList.remove(position);
                //mDoneTaskList.add(d);
                Toast.makeText(getActivity(), "Removed from the list", Toast.LENGTH_SHORT).show();
                // mDoneTaskList.add(d);
                donetodoAdapter.notifyDataSetChanged();

                Snackbar.make(getView(), "Do you want the item back?", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDoneTaskList.add(d);
                        donetodoAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Operation Undo, Item added back", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();

    }

    @Override
    public void onItemLongPress(View childView, int position) {
        if (actionMode == null) {
            actionMode = ((MainActivity) getActivity()).startSupportActionMode
                    (actionModeCallback);
        }
        toggleSelection(position);
    }


    /**
     * This ActionModeCallBack Class handling the User Event after the Selection of Clients. Like
     * Click of Menu Sync Button and finish the ActionMode
     */
    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String LOG_TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    for (Integer position : donetodoAdapter.getSelectedItems()) {
                        tempList.add(mDoneTaskList.get(position));
                        mDoneTaskList.remove(mDoneTaskList.get(position));
                        donetodoAdapter.notifyDataSetChanged();
                    }
                    mode.finish();

                    Snackbar.make(getView(), R.string.undo_delete, Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                mDoneTaskList.addAll(tempList);
                                donetodoAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), R.string.items_back, Toast.LENGTH_SHORT).show();
                        }
                    }).show();
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            donetodoAdapter.clearSelection();
            actionMode = null;
        }
    }

}
