package fr.feepin.justchat.ViewModels;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import fr.feepin.justchat.Fragments.SubjectsFragment;
import fr.feepin.justchat.Shared;

public class MainViewModel extends ViewModel {
    private MutableLiveData<Boolean> dataReceived;

    public MainViewModel(){
        dataReceived = new MutableLiveData<>();
    }

    public void requestDatas(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://discuss-chatapp.com/rooms", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Shared.datas = response;
                        dataReceived.setValue(true);
                        Log.i("LOGEU", "called");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dataReceived.setValue(false);
                    }
                }
        );

        Shared.requestQueue.add(jsonObjectRequest);
    }

    public MutableLiveData<Boolean> getDataReceived() {
        return dataReceived;
    }
}
