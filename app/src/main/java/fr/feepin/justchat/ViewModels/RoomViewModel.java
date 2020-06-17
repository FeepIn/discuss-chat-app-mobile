package fr.feepin.justchat.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import fr.feepin.justchat.R;
import fr.feepin.justchat.Shared;
import fr.feepin.justchat.models.User;

public class RoomViewModel extends AndroidViewModel {
    private Socket socket;
    private MutableLiveData<Integer>userCount;
    private MutableLiveData<User> message;
    private MutableLiveData<String> newHost;
    private MutableLiveData<Boolean> hasBeenKicked;

    public RoomViewModel(@NonNull Application application) {
        super(application);

        //Inits
        userCount = new MutableLiveData<>();
        newHost = new MutableLiveData<>();
        message = new MutableLiveData<>();
        hasBeenKicked = new MutableLiveData<>();
        socket = Shared.socket;

        newHost.setValue(Shared.hostname);

    }

    public void removeListeners(){
        socket.off("message", messageListener);
        socket.off("newUser", newUserListener);
        socket.off("userLeft", userLeftListener);
        socket.off("userKicked", userKickedListener);
        socket.off("newHost", newHostListener);
        socket.off("kicked", kickedListener);
    }

    public void setupSocketListeners(){
        socket.on("message", messageListener)
                .on("newUser", newUserListener)
                .on("userLeft", userLeftListener)
                .on("userKicked", userKickedListener)
                .on("newHost", newHostListener)
                .on("kicked", kickedListener)
                .on("roomLeft", kickedListener);
    };

    //Listeners
    private Emitter.Listener userKickedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject)args[0];
            try {
                String userName = jsonObject.getString("name");
                String colorHex = jsonObject.getString("color");
                User user = new User("System", false, getApplication().getResources().getString(R.string.user_kicked, userName), colorHex);
                user.setUserJoined(userName);
                getMessage().postValue(user);
                getUserCount().postValue(jsonObject.getInt("userCount"));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    private Emitter.Listener kickedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            hasBeenKicked.postValue(true);
        }
    };

    private Emitter.Listener newHostListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject)args[0];
            try {
                String userName = jsonObject.getString("name");
                getNewHost().postValue(userName);
                String colorHex = jsonObject.getString("color");
                final User user = new User("System", false, getApplication().getResources().getString(R.string.user_new_host, userName), colorHex);
                user.setUserJoined(userName);
                getMessage().postValue(user);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    private Emitter.Listener userLeftListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject)args[0];
            try {
                String userName = jsonObject.getString("name");
                String colorHex = jsonObject.getString("color");
                final User user = new User("System", false, getApplication().getString(R.string.user_has_left, userName), colorHex);
                user.setUserJoined(userName);
                userCount.postValue(jsonObject.getInt("userCount"));
                getMessage().postValue(user);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    private Emitter.Listener newUserListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject)args[0];
            try {
                int userCount = jsonObject.getInt("userCount");
                String userName = jsonObject.getString("name");
                String colorHex = jsonObject.getString("color");
                final User user = new User("System", false, getApplication().getString(R.string.user_has_joined, userName), colorHex);
                user.setUserJoined(userName);

                getUserCount().postValue(userCount);
                getMessage().postValue(user);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    private Emitter.Listener messageListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject datas = (JSONObject)args[0];
            try {

                String message = datas.getString("message");
                String userName = datas.getString("userName");

                getMessage().postValue(new User(userName, getNewHost().getValue().equals(userName), message.trim(), datas.getString("color")));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    //Getters

    public MutableLiveData<Boolean> getHasBeenKicked() {
        return hasBeenKicked;
    }

    public MutableLiveData<String> getNewHost() {
        return newHost;
    }

    public MutableLiveData<User> getMessage() {
        return message;
    }

    public MutableLiveData<Integer> getUserCount() {
        return userCount;
    }
}
