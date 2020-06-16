package fr.feepin.justchat;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.eightbitlab.supportrenderscriptblur.SupportRenderScriptBlur;

import eightbitlab.com.blurview.BlurView;

public class MessageDialog extends AlertDialog {
    private BlurView blurView;
    private ViewGroup root;
    private Context context;

    public MessageDialog(Context context, String message) {
        super(context);
        this.context = context;

        setCancelable(true);
        root = getWindow().getDecorView().findViewById(android.R.id.content);
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = layoutInflater.inflate(R.layout.message_dialog, root, false);

        TextView messageView = dialogView.findViewById(R.id.message);
        messageView.setText(message);

        Button button = dialogView.findViewById(R.id.okButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurView.setVisibility(View.INVISIBLE);
                dismiss();
            }
        });

        //Removing default background overlay
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setView(dialogView);
    }

    @Override
    public void show() {
        super.show();
        blurView = ((FragmentActivity)context).findViewById(R.id.blur);
        blurView.setupWith(root)
                .setBlurAlgorithm(new SupportRenderScriptBlur(getContext()))
                .setBlurRadius(3);
    }
}
