package pa1pal.todo.task.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pa1pal.todo.task.R;
import pa1pal.todo.task.adapter.TaskAdapter;
import pa1pal.todo.task.callback.ApiManager;
import pa1pal.todo.task.pojo.Datum;
import pa1pal.todo.task.pojo.TodoPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingFragment extends Fragment {
    private ApiManager apiManager;
    private TodoPojo mTodo;
    private List<Datum> mPendingTaskList;
    TaskAdapter todoAdapter;
    RecyclerView todoList;
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.pending_todo_list)
    RecyclerView pendingrecyclerView;

    public PendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTodo = new TodoPojo();
        mPendingTaskList = new ArrayList<Datum>();
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

        pendingrecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
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

    public void loadTodo(){
        apiManager = new ApiManager();
        Call<TodoPojo> reviewsCall = apiManager.getApi().getTodo();
        reviewsCall.enqueue(new Callback<TodoPojo>() {
            @Override
            public void onResponse(Call<TodoPojo> call, Response<TodoPojo> response) {
                if(response.isSuccessful()){
                    mTodo = response.body();
                    Collection<Datum> data = mTodo.getData();
                    for (int i=0; i<data.size(); i++){
                        if (mTodo.getData().get(i).getState() == 0){
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

            }

        });
    }

    private void displayCreateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getResources().getString(R.string.title_dialog_new_list));

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_input, null);
        final EditText input = (EditText) view.findViewById(R.id.text);
        alert.setView(view);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                    String title = input.getText().toString();
                    if (title.length() == 0)
                        return;
                else{
                        Datum d = new Datum();
                        d.setName(title);
                        mPendingTaskList.add(d);
                        todoAdapter.notifyDataSetChanged();
                    }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

}
