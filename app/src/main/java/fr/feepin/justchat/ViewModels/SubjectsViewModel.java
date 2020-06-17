package fr.feepin.justchat.ViewModels;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import fr.feepin.justchat.R;
import fr.feepin.justchat.Shared;

public class SubjectsViewModel extends ViewModel {
    private Socket socket;
    private MutableLiveData<Boolean> nameTaken, connected;

    public SubjectsViewModel(){
        super();
        this.socket = Shared.socket;
        connected = new MutableLiveData<>();
        nameTaken = new MutableLiveData<>();
        setupListeners();
    }


    private void setupListeners(){
        //Socket listeners
        socket.on("nameTaken", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                nameTaken.postValue(true);
            }
        }).on("connected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                connected.postValue(true);
            }
        });
    }

    public MutableLiveData<Boolean> getConnected() {
        return connected;
    }

    public MutableLiveData<Boolean> getNameTaken() {
        return nameTaken;
    }
}
