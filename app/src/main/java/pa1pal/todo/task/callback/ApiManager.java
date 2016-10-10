package pa1pal.todo.task.callback;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pa1pal on 30/4/16.
 */
public class ApiManager {
    private Api todoService;

    public Api getApi(){
        if (todoService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.110:8088/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            todoService = retrofit.create(Api.class);
        }
        return todoService;
    }

}