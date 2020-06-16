package fr.feepin.justchat.ViewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import fr.feepin.justchat.MainActivity;
import fr.feepin.justchat.Shared;


public class RoomsViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject>datas;
    private MutableLiveData<String> hostName;
    private MutableLiveData<Integer>userCount;

    public RoomsViewModel(Application application){
        super(application);

        //Inits
        datas = new MutableLiveData<>();
        hostName = new MutableLiveData<>();
        userCount = new MutableLiveData<>();
        setupListener();
        requestDatas();
    }

    public void requestDatas(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://discuss-chatapp.com/rooms", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                datas.setValue(response);
                Shared.datas = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getDatas().postValue(null);
                ((MainActivity)getApplication().getApplicationContext()).connectionFailed(new MainActivity.OnRetryButtonClick() {
                    @Override
                    public void clicked() {
                        requestDatas();
                    }
                });
            }
        });

        Shared.requestQueue.add(jsonObjectRequest);
    }

    private void setupListener(){
        Shared.socket.on("roomJoined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject datas = (JSONObject)args[0];
                try {
                    //String roomName = datas.getString("roomName");
                    userCount.postValue(datas.getInt("userCount"));
                    hostName.postValue(datas.getString("hostName"));


                } catch (JSONException e) {
                    Toast.makeText(getApplication(), "Connection failed", Toast.LENGTH_LONG);
                }
            }
        });
    }
    public MutableLiveData<JSONObject> getDatas() {
        return datas;
    }
    public MutableLiveData<Integer> getUserCount() {
        return userCount;
    }
    public MutableLiveData<String> getHostName() {
        return hostName;
    }
}
