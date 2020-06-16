package fr.feepin.justchat.Fragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fr.feepin.justchat.Adapters.SubjectsAdapter;
import fr.feepin.justchat.Database;
import fr.feepin.justchat.MainActivity;
import fr.feepin.justchat.MyDialog;
import fr.feepin.justchat.R;
import fr.feepin.justchat.Adapters.RoomListAdapter;
import fr.feepin.justchat.Shared;
import fr.feepin.justchat.models.Subject;

public class SubjectsFragment extends Fragment implements SubjectsAdapter.OnSubjectClick {
    private Drawable[]backgroundImgs;
    private HashMap<String, Boolean> preference;
    private JSONObject datas;
    private ArrayList<Subject> subjects;
    private Database database;
    private RecyclerView subjectRecycler;
    private Socket socket;
    private SearchView searchView;
    private SubjectsAdapter subjectsAdapter;
    private TextView title;
    private AppCompatActivity appCompatActivity;
    private MyDialog myDialog;
    public SubjectsFragment() {
        setRetainInstance(false);
        this.appCompatActivity = Shared.appCompatActivity;
        ((MainActivity)appCompatActivity).setOnBackButtonPressed(new MainActivity.OnBackButtonPressed() {
            @Override
            public void pressed() {
                appCompatActivity.finish();
            }
        });
        subjects = new ArrayList<>();
        this.datas = Shared.datas;
        this.database = Shared.database;
        try {
            this.socket = IO.socket("https://discuss-chatapp.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.socket.connect();
        Shared.socket = this.socket;

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.subjects_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initSubjectList(view);



        if(Shared.theme == null && Shared.userName == null && myDialog == null){
            askForName(false);
        }

        title = view.findViewById(R.id.subjects);

        setupEditText();


    }

    @Override
    public void onStart() {
        super.onStart();
        socket.on("nameTaken", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myDialog.setErrorMessage(getResources().getString(R.string.name_taken_error));
                    }
                });

            }
        }).on("connected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myDialog.dismiss();
                    }
                });
            }
        });
    }

    private void setupEditText(){
        searchView = (SearchView)getView().findViewById(R.id.textInputLayout);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                title.setVisibility(View.VISIBLE);
                return false;
            }

        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setVisibility(View.INVISIBLE);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                subjectsAdapter.getFilter().filter(newText);
                return true;
            }

        });
    }

    private void askForName(final boolean changeName){
        myDialog = new MyDialog(getContext(), getResources().getString(R.string.who_are_you), getResources().getString(R.string.cancel), getResources().getString(R.string.done), getResources().getString(R.string.enter_name));
        myDialog.setOnSubmitListener(new MyDialog.OnSubmitListener() {
            @Override
            public void dismiss(String input) {
                Shared.userName = "Anonymous";
                socket.emit(changeName ? "changeName" :"name", "Anonymous");

            }

            @Override
            public void cancel(String input) {
                Shared.userName = "Anonymous";
                socket.emit(changeName ? "changeName" :"name", "Anonymous");
            }

            @Override
            public void done(final String input) {
                if (input.trim().equals("")){
                    myDialog.setErrorMessage(getResources().getString(R.string.please_enter_name));
                }else{
                    Shared.userName = input;
                    socket.emit(changeName ? "changeName" :"name", input);
                }
            }
        });
        myDialog.getWindow().getAttributes().windowAnimations = R.style.EnterDialogAnimation;
        myDialog.show();

    }

    private void initSubjectList(View view){
        this.backgroundImgs = new Drawable[]{
                getResources().getDrawable(R.drawable.manga_anime),
                getResources().getDrawable(R.drawable.love),
                getResources().getDrawable(R.drawable.video_games),
                getResources().getDrawable(R.drawable.space),
                getResources().getDrawable(R.drawable.culture),
                getResources().getDrawable(R.drawable.music)
        };
        preference = database.getFavoriteSubjects();
        setupSubjects();

        subjectRecycler = view.findViewById(R.id.subjectsRecycler);
        subjectsAdapter = new SubjectsAdapter(getContext(), subjects, this);
        subjectRecycler.setAdapter(subjectsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        subjectRecycler.setLayoutManager(linearLayoutManager);
    }



    private void setupSubjects(){
        int index = 0;

        for (Iterator<String> it = datas.keys(); it.hasNext(); ) {

            String subject = it.next();


            try {

                subjects.add(new Subject(subject, preference.containsKey(subject), datas.getJSONObject(subject).getInt("userCount"), backgroundImgs[index], 0, 0));
            } catch (JSONException e) {

            }

            index++;
        }

        sortSubjects();
    }

    private void sortSubjects(){
        ArrayList<Subject>sortedList = new ArrayList<>();
        HashMap<String, Boolean> preference = database.getFavoriteSubjects();

        for (Subject subject : subjects){
            if (preference.containsKey(subject.getName())) {
                subject.setLiked(true);
                sortedList.add(0, subject);
            }else{
                sortedList.add(subject);
            }
        }
        this.subjects = sortedList;
    }


    @Override
    public void clicked(String subjectName) {
        Shared.theme = subjectName;

        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.frameLayout, new RoomsFragment());
        fragmentTransaction.commit();


    }
}
