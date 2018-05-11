package frost.com.homelighting.webservice;

import retrofit2.Retrofit;

public class WebService {

    private Retrofit retrofit;

    public WebService() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://")
                .build();
    }
}
