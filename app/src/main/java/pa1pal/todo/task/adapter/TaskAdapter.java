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

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

    private List<Datum> mTaskList;
    private Context mContext;
    private TodoPojo todoData;
    private String[] items;
    private Multiselector multiselector;
    private OnItemClickListener listener;



    public TaskAdapter(Activity context, List<Datum> results, Multiselector multiselector, OnItemClickListener listener){
        mTaskList = results;
        mContext = context;
        this.multiselector = multiselector;
        this.listener = listener;
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
        else{
            holder.todoDescription.setText("Reviews not available");
        }
        holder.itemView.setActivated(multiselector.isChecked(position));
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{
        @BindView(R.id.tododesc) TextView todoDescription;
        public int id, state;

        public MyViewHolder(View view) {
            super(view);
//            ButterKnife.bind(this, view);
            todoDescription = (TextView) view.findViewById(R.id.tododesc);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onItemLongPress(view, getAdapterPosition());
            return true;
        }
    }

}
