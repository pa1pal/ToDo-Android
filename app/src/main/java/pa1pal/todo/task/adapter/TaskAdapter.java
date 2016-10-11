package pa1pal.todo.task.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pa1pal.todo.task.R;
import pa1pal.todo.task.pojo.Datum;
import pa1pal.todo.task.pojo.TodoPojo;

/**
 * Created by pa1pal on 10/10/16.
 */

public class TaskAdapter extends SelectableAdapter<TaskAdapter.MyViewHolder>{

    private List<Datum> mTaskList;
    private Context mContext;
    private TodoPojo todoData;


    public TaskAdapter(Activity context, List<Datum> results){
        mTaskList = results;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.todo_view, null, false);
        //return new ImageViewHolder(mLayoutInflater.inflate(R.layout.recyclerview_item, parent, false));
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.MyViewHolder holder, int position) {
        if (mTaskList.get(position).getName() != null) {
            holder.todoDescription.setText(mTaskList.get(position).getName());
        }
        else
            holder.todoDescription.setText("Reviews not available");

    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tododesc) TextView todoDescription;
        public int id, state;

        public MyViewHolder(View view) {
            super(view);
//            ButterKnife.bind(this, view);
            todoDescription = (TextView) view.findViewById(R.id.tododesc);
        }
    }

}
