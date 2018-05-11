package frost.com.homelighting.webservice;

import frost.com.homelighting.db.entity.DeviceEntity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NodeMCUAPI {

    @POST("{ipAddress}/play")
    void playPreset(@Path("ipAddress") String ipAddress, @Body String command);

    @GET("{ipAddress}/mcu_info")
    Call<String> getDeviceInfo(@Path("ipAddress") String ipAddress);

    @POST("{ipAddress}/mcu_config")
    void updateDevice(@Path("ipAddress") String ipAddress, @Body DeviceEntity deviceEntity);

    @GET("{ipAddress}/mcu_json")
    DeviceEntity getDeviceConfiguration(@Path("ipAddress") String ipAddress);
}
