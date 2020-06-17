package fr.feepin.justchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
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

import fr.feepin.justchat.Fragments.SubjectsFragment;
import fr.feepin.justchat.ViewModels.MainViewModel;


public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    Database database;
    RequestQueue requestQueue;
    ProgressBar progressBar;
    ImageView appIconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Inits
        requestQueue = Volley.newRequestQueue(this);
        database = new Database(this);
        //Init emojicompat
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this)
                .setReplaceAll(true);
        EmojiCompat.init(config);

        //Setting shared objects
        Shared.database = database;
        Shared.requestQueue = requestQueue;
        Shared.wasOn = "";
        Shared.appCompatActivity = this;

        //ViewModels
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

        setContentView(R.layout.activity_main);
        //Getting views
        frameLayout  = findViewById(R.id.frameLayout);
        progressBar = findViewById(R.id.progress);
        appIconView = findViewById(R.id.discussIcon);

        //App Icon animation setting
        final AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fading_in_from_top);

        //Anim listeners
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.startAnimation(alphaAnimation);
                mainViewModel.requestDatas();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        appIconView.startAnimation(animation);


    }

    @Override
    protected void onStart() {
        if (Shared.userName != null) {
            Shared.socket.emit("name", Shared.userName);
        }
        super.onStart();
    }

    private OnBackButtonPressed onBackButtonPressed;

    public void setOnBackButtonPressed(OnBackButtonPressed onBackButtonPressed) {
        this.onBackButtonPressed = onBackButtonPressed;
    }

    @Override
    public void onBackPressed() {
        if (onBackButtonPressed != null){
            onBackButtonPressed.pressed();
        }
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
        View root = getLayoutInflater().inflate(R.layout.dialog_conneciton_failed,
                (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content),
                false);

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

    //Intefaces
    public interface OnRetryButtonClick{
        void clicked();
    }

    public interface OnBackButtonPressed{
        void pressed();
    }

}
