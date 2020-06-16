package fr.feepin.justchat.Fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;


import fr.feepin.justchat.Adapters.RoomListAdapter;
import fr.feepin.justchat.MainActivity;
import fr.feepin.justchat.MyDialog;
import fr.feepin.justchat.R;
import fr.feepin.justchat.Shared;
import fr.feepin.justchat.ViewModels.RoomsViewModel;
import fr.feepin.justchat.models.RoomDetails;

public class RoomsFragment extends Fragment {
    Socket socket;
    JSONObject datas;
    RequestQueue requestQueue;
    RoomDetails[] roomDetails;
    RecyclerView recyclerView;
    AppCompatActivity appCompatActivity;
    RoomsViewModel roomsViewModel;
    public RoomsFragment(){
        setRetainInstance(true);

        //Setting back button
        this.appCompatActivity = Shared.appCompatActivity;
        ((MainActivity)appCompatActivity).setOnBackButtonPressed(new MainActivity.OnBackButtonPressed() {
            @Override
            public void pressed() {
                exitFragment();
            }
        });

        //Getting shared objects
        requestQueue = Shared.requestQueue;
        socket = Shared.socket;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomsViewModel = new ViewModelProvider(this).get(RoomsViewModel.class);
        roomsViewModel.getDatas().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                if (jsonObject != null){
                    datas = jsonObject;
                    setupRoomList();
                }
            }
        });

        roomsViewModel.getUserCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Shared.userCount = integer;
            }
        });



        roomsViewModel.getHostName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Shared.hostname = s;

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.frameLayout, new RoomFragment());
                fragmentTransaction.commit();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roomsViewModel.requestDatas();
        return inflater.inflate(R.layout.rooms_fragment, container, false);
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView backButton = view.findViewById(R.id.backBtn);
        backButton.setOnClickListener(onBackButtonClicked);
        recyclerView = view.findViewById(R.id.roomsList);





    }



    private void exitFragment(){
        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.replace(R.id.frameLayout, new SubjectsFragment());
        fragmentTransaction.commit();

    }



    private View.OnClickListener onBackButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            exitFragment();
        }
    };



    private void setupRoomList(){
        try {
            roomDetails = new RoomDetails[datas.getJSONObject(Shared.theme).getJSONArray("rooms").length()];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            for (int i = 0; i < datas.getJSONObject(Shared.theme).getJSONArray("rooms").length(); i++){
                roomDetails[i] = new RoomDetails(datas.getJSONObject(Shared.theme).getJSONArray("rooms").getJSONObject(i).getString("roomName"), datas.getJSONObject(Shared.theme).getJSONArray("rooms").getJSONObject(i).getInt("userCount"), getResources().getIntArray(R.array.colors_array)[Math.round((float)Math.random()*(getResources().getIntArray(R.array.colors_array).length-1))]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        int colCount;
        if (width>height){
            colCount = 3;
        }else{
            colCount = 2;
        }


        RoomListAdapter roomListAdapter = new RoomListAdapter(getContext(), roomDetails, onRoomClick, onCreateClick);
        recyclerView.setAdapter(roomListAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), colCount));


    }
    int width, height;
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        int colCount;
        if (width>height){
            colCount = 3;
        }else{
            colCount = 2;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), colCount));
    }

    private RoomListAdapter.OnCreateClick onCreateClick = new RoomListAdapter.OnCreateClick() {
        @Override
        public void clicked() {
            final MyDialog myDialog = new MyDialog(appCompatActivity,
                    getResources().getString(R.string.room_name),
                    getResources().getString(R.string.cancel),
                    getResources().getString(R.string.done),
                    getResources().getString(R.string.enter_room_name));

            myDialog.setOnSubmitListener(new MyDialog.OnSubmitListener() {
                @Override
                public void dismiss(String input) {

                }

                @Override
                public void cancel(String input) {
                    myDialog.dismiss();
                }

                @Override
                public void done(String input) {
                    if (input.trim().equals("")){
                        myDialog.setErrorMessage(getResources().getString(R.string.please_enter_name));
                        return;
                    }
                    JSONObject datas = new JSONObject();
                    try {
                        datas.put("roomTheme", Shared.theme);
                        datas.put("roomName", input);
                        socket.emit("createRoom", datas);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    myDialog.dismiss();
                }
            });

            myDialog.show();
        }
    };

    private RoomListAdapter.OnRoomClick onRoomClick = new RoomListAdapter.OnRoomClick() {
        @Override
        public void clicked(String roomName) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomTheme", Shared.theme);
                jsonObject.put("roomName", roomName);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            socket.emit("joinRoom", jsonObject);
        }
    };


}
