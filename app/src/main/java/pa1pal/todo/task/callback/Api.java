package pa1pal.todo.task.callback;

import pa1pal.todo.task.pojo.TodoPojo;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by pa1pal on 10/10/16.
 */

public interface Api {
    @GET("pa1pal/39fd29a48bb3592006847286ee6541f4/raw/9729ae0b9018fcf6209a8b77c56375d296cc420f/tasks.json")
    Call<TodoPojo> getTodo();
}
