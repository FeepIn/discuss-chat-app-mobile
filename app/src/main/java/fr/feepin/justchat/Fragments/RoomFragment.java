package fr.feepin.justchat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.emoji.widget.EmojiEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.ArrayList;

import fr.feepin.justchat.Adapters.ChatRoomAdapter;
import fr.feepin.justchat.Adapters.ContextMenuListAdapter;
import fr.feepin.justchat.Adapters.RoomListAdapter;
import fr.feepin.justchat.MainActivity;
import fr.feepin.justchat.MessageDialog;
import fr.feepin.justchat.R;
import fr.feepin.justchat.Shared;
import fr.feepin.justchat.ViewModels.RoomViewModel;
import fr.feepin.justchat.models.User;

public class RoomFragment extends Fragment implements ContextMenuListAdapter.OnItemClicked, ChatRoomAdapter.OnContextMenuOpen {
    private String hostName;
    private Socket socket;
    private ArrayList<User> users;
    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    private AppCompatActivity appCompatActivity;
    private EmojiEditText emojiEditText;
    private ImageView sendButtonView;
    private ImageView settingIconView;
    private View contextMenuView;
    private ListView contextMenuListView;
    private TextView userCountView;
    
    public RoomFragment(){
        setRetainInstance(true);

        Shared.wasOn ="room";

        this.appCompatActivity = Shared.appCompatActivity;
        ((MainActivity)appCompatActivity).setOnBackButtonPressed(new MainActivity.OnBackButtonPressed() {
            @Override
            public void pressed() {
                exitFragment();
            }
        });

        this.socket = Shared.socket;
        this.users = new ArrayList<>();
        this.hostName = Shared.hostname;

    }

    public RoomViewModel roomViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        roomViewModel.setupSocketListeners();
        roomViewModel.getNewHost().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Shared.hostname = s;
                hostName = s;
            }
        });

        roomViewModel.getMessage().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                user.setHost(user.getName().equals(hostName));
                chatRoomAdapter.users.add(user);
                chatRoomAdapter.notifyItemInserted(chatRoomAdapter.users.size()-1);

                if (!recyclerView.isPressed()) recyclerView.scrollToPosition(chatRoomAdapter.users.size()-1);
            }
        });

        roomViewModel.getHasBeenKicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                MessageDialog messageDialog = new MessageDialog(getActivity(), getResources().getString(R.string.got_kicked));
                messageDialog.show();
                exitFragment();
            }
        });

        roomViewModel.getUserCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                userCountView.setText(String.valueOf(integer));
            }
        });

    }

    ViewGroup container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.room_fragment, container, false);
        this.container = container;
        contextMenuView = inflater.inflate(R.layout.context_menu, container, false);
        contextMenuListView = contextMenuView.findViewById(R.id.contextList);
        contextMenuListView.setAdapter(new ContextMenuListAdapter(getContext(), this));


        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.chatRecycler);
        chatRoomAdapter = new ChatRoomAdapter(getContext(), users, contextMenuView, container, this);
        recyclerView.setAdapter(chatRoomAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        //Back Button listener
        ImageView imageView = view.findViewById(R.id.backBtn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                exitFragment();

            }
        });




        emojiEditText = view.findViewById(R.id.chatEditText);
        sendButtonView = view.findViewById(R.id.sendButton);
        userCountView = view.findViewById(R.id.userCount);

        userCountView.setText(String.valueOf(Shared.userCount));

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                container.removeView(contextMenuView);
                if (chatRoomAdapter.getSelectedView() != null) chatRoomAdapter.getSelectedView().setBackgroundColor(getResources().getColor(R.color.belgium));
                return false;
            }
        });

        emojiEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                container.removeView(contextMenuView);
                if (chatRoomAdapter.getSelectedView() != null) chatRoomAdapter.getSelectedView().setBackgroundColor(getResources().getColor(R.color.belgium));
                return false;
            }
        });




        final ConstraintLayout constraintLayout = view.findViewById(R.id.root);

        sendButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeView(contextMenuView);
                if (!emojiEditText.getText().toString().trim().equals("")){
                    socket.emit("message", emojiEditText.getText().toString());
                }

                if (!chatRoomAdapter.users.isEmpty())
                recyclerView.smoothScrollToPosition(chatRoomAdapter.users.size()-1);
                emojiEditText.setText("");


            }
        });

    }

    private void exitFragment(){
        if (emojiEditText.isFocused())hideKeyboard(getActivity());

        roomViewModel.removeListeners();
        socket.emit("userLeft", new JSONObject());
        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.replace(R.id.frameLayout, new RoomsFragment());

        fragmentTransaction.commit();
    }

    Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    public static void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null && v != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();

    }

    String selectedUserName;


    @Override
    public void clicked(View v, String itemText) {
        container.removeView(contextMenuView);
        if (itemText.equals(getResources().getString(R.string.kick)) && Shared.hostname.equals(Shared.userName)){
                socket.emit("kickUser", selectedUserName);
        }
    }

    @Override
    public void onOpen(TextView nameView, View root) {
        selectedUserName = nameView.getText().toString();
    }
}
