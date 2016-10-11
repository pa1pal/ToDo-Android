package pa1pal.todo.task.fragments;

import android.annotation.SuppressLint;
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

public class PendingFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener {
    private ApiManager apiManager;

    private TodoPojo mTodo;

    public List<Datum> mPendingTaskList;
    public List<Datum> tempList;


    public Datum newdata, d;

    TaskAdapter todoAdapter;

    RecyclerView todoList;

    SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    @BindView(R.id.pending_todo_list)
    RecyclerView pendingrecyclerView;

    public PendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionModeCallback = new ActionModeCallback();
        mTodo = new TodoPojo();
        d = new Datum();
        mPendingTaskList = new ArrayList<Datum>();
        tempList = new ArrayList<Datum>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_pending, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipelayout);
        ButterKnife.bind(this, rootview);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPendingTaskList.clear();
                loadTodo();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        pendingrecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.addtodofab);
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
        pendingrecyclerView = (RecyclerView) view.findViewById(R.id.pending_todo_list);
        pendingrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadTodo();
    }

    public void loadTodo() {
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

                if (response.isSuccessful()) {
                    mTodo = response.body();
                    Collection<Datum> data = mTodo.getData();
                    for (int i = 0; i < data.size(); i++) {
                        if (mTodo.getData().get(i).getState() == 0) {
                            mPendingTaskList.add(mTodo.getData().get(i));
                        }
                    }
                    todoAdapter = new TaskAdapter(getActivity(), mPendingTaskList);
                    todoAdapter.notifyDataSetChanged();
                    pendingrecyclerView.setAdapter(todoAdapter);
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
        d = new Datum();
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = input.getText().toString();
                if (title.length() == 0)
                    return;
                else {
                    d.setName(title);
                    if (!state.isChecked()) {
                        mPendingTaskList.add(d);
                        todoAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Added in the Pending list", Toast.LENGTH_LONG).show();
                    } else {

                         }
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    @Override
    public void onItemClick(View childView, final int position) {
        //final Datum tempD = new Datum();
        //String name;
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getResources().getString(R.string.delete));
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                d = mPendingTaskList.get(position);
                mPendingTaskList.remove(position);
                        //mPendingTaskList.add(d);
                Toast.makeText(getActivity(), "Removed from the list", Toast.LENGTH_SHORT).show();
                   // mPendingTaskList.add(d);
                    todoAdapter.notifyDataSetChanged();

                Snackbar.make(getView(), "Do you want the item back?", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPendingTaskList.add(d);
                        todoAdapter.notifyDataSetChanged();
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
        Toast.makeText(getActivity(), "Long Click on each item to add. Short click to delete only one item", Toast.LENGTH_SHORT).show();

        toggleSelection(position);
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
        todoAdapter.toggleSelection(position);
        int count = todoAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
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
                    List<Datum> removeTasks = new ArrayList<>();
                    for (Integer position : todoAdapter.getSelectedItems()) {
                        removeTasks.add(mPendingTaskList.get(position));
                    }
                    tempList = removeTasks;
                    mPendingTaskList.removeAll(removeTasks);
                    todoAdapter.notifyDataSetChanged();
                    mode.finish();

                    Snackbar.make(getView(), R.string.undo_delete, Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPendingTaskList.addAll(tempList);
                            todoAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), R.string.items_back, Toast.LENGTH_SHORT).show();
                        }
                    }).show();

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            todoAdapter.clearSelection();
            actionMode = null;
        }
    }

}
