package pa1pal.todo.task.pojo;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pa1pal.todo.task.pojo.Datum;

public class TodoPojo {

    @SerializedName("data")
    @Expose
    private List<Datum> data = new ArrayList<Datum>();


    /**
     *
     * @return
     * The data
     */
    public List<Datum> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<Datum> data) {
        this.data = data;
    }
}

