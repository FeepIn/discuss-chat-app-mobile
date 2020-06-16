package fr.feepin.justchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import fr.feepin.justchat.Fragments.RoomsFragment;
import fr.feepin.justchat.Fragments.SubjectsFragment;
import fr.feepin.justchat.ViewModels.MainViewModel;


public class MainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    RoomsFragment roomsFragment;
    SubjectsFragment subjectsFragment;
    Database database;
    RequestQueue requestQueue;
    ProgressBar progressBar;
    ImageView appIconView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestQueue = Volley.newRequestQueue(this);
        database = new Database(this);
        Shared.requestQueue = requestQueue;
        final MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getDataReceived().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    setFragment(new SubjectsFragment());
                }else{
                    connectionFailed(new OnRetryButtonClick() {
                        @Override
                        public void clicked() {
                            mainViewModel.requestDatas();
                        }
                    });
                }

            }
        });

        Shared.wasOn = "";
        Shared.appCompatActivity = this;
        frameLayout  = findViewById(R.id.frameLayout);
        super.onCreate(savedInstanceState);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this)
                .setReplaceAll(true);
        EmojiCompat.init(config);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frameLayout);

        Shared.database = database;

        progressBar = findViewById(R.id.progress);



        appIconView = findViewById(R.id.discussIcon);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fading_in_from_top);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBar.setVisibility(View.VISIBLE);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setDuration(500);
                progressBar.startAnimation(alphaAnimation);
                mainViewModel.requestDatas();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        appIconView.startAnimation(animation);


    }


    private OnBackButtonPressed onBackButtonPressed;

    @Override
    public void onBackPressed() {
        if (onBackButtonPressed != null){
            onBackButtonPressed.pressed();
        }
    }

    public void setOnBackButtonPressed(OnBackButtonPressed onBackButtonPressed) {
        this.onBackButtonPressed = onBackButtonPressed;
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }, 1000);
    }

    public void connectionFailed(final OnRetryButtonClick onRetryButtonClick){
        Log.i("SOCKET", "failed");
        View root = getLayoutInflater().inflate(R.layout.dialog_conneciton_failed, (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content), false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(root).create();

        Button retryButton = root.findViewById(R.id.retryBtn);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onRetryButtonClick.clicked();
            }
        });

        if (retryButton != null) {
            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        dialog.show();

    }


    public interface OnRetryButtonClick{
        void clicked();
    }

    public interface OnBackButtonPressed{
        void pressed();
    }

}
