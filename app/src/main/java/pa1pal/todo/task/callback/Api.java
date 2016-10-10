package pa1pal.todo.task.callback;

import pa1pal.todo.task.pojo.TodoPojo;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by pa1pal on 10/10/16.
 */

public interface Api {
    @GET("tasks.json")
    Call<TodoPojo> getTodo();
}
