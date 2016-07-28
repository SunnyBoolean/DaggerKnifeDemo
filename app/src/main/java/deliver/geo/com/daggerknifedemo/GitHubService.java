package deliver.geo.com.daggerknifedemo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by 李伟 on 2016/7/28.
 */
public interface GitHubService {
    @GET("/users/{user}/repos")
    Call<List<String>> listRepos(@Path("user") String user);
}