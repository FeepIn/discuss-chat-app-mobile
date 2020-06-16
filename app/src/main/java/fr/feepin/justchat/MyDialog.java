package fr.feepin.justchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class MyDialog extends AlertDialog {
    EditText editText;
    TextView titleView, cancelView, doneView;
    Activity activity;
    TextInputLayout textInputLayout;

    public MyDialog(@NonNull Context context, String title, String cancelLable, String doneLabel, String hint) {
        super(context);
        setupDialog(context, title, cancelLable, doneLabel, hint);

    }

    private void setupDialog(Context context, String title, String cancelLable, String doneLabel, String hint){
        activity = (Activity)context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog, (ViewGroup)activity.findViewById(R.id.fragment), false);

        //Getting views
        titleView = view.findViewById(R.id.dialogTitle);
        cancelView = view.findViewById(R.id.dialogCancelButton);
        doneView = view.findViewById(R.id.dialogCaDoneButton);
        editText = view.findViewById(R.id.dialogEditText);
        textInputLayout= view.findViewById(R.id.textInputLayout2);

        //Setting views
        textInputLayout.setHint(hint);
        titleView.setText(title);
        cancelView.setText(cancelLable);
        doneView.setText(doneLabel);

        //Removing default dialog background overlay
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //Setting new dialog background
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        setView(view);

    }

    public void setErrorMessage(String errorMessage){
        textInputLayout.setError(errorMessage.trim());
    }

    private void blurEffect(){
        final BlurView blurView = activity.findViewById(R.id.blur);
        blurView.setupWith((ViewGroup)activity.findViewById(R.id.fragment))
                .setBlurRadius(3)
                .setBlurAlgorithm(new RenderScriptBlur(getContext()));
        blurView.setEnabled(true);
        blurView.setVisibility(View.VISIBLE);

        //Remove blur on dismiss
        setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                blurView.setVisibility(View.INVISIBLE);
                blurView.setEnabled(false);
            }
        });


    }

    //Listeners
    public void setOnSubmitListener(final OnSubmitListener onSubmitListener){
        doneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitListener.done(editText.getText().toString());
            }
        });

        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitListener.cancel(editText.getText().toString());
            }
        });

    }

    @Override
    public void show() {
        super.show();
        blurEffect();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        final BlurView blurView = activity.findViewById(R.id.blur);
        blurView.setVisibility(View.INVISIBLE);
        blurView.setEnabled(false);
    }


    //Interface
    public interface OnSubmitListener{
        public void dismiss(String input);
        public void cancel(String input);
        public void done(String input);
    }



}
