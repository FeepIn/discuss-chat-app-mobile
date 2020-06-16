package fr.feepin.justchat;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

//View that constraint ratio to be 1:1
public class GridViewRoomItem extends ConstraintLayout {

    public GridViewRoomItem(Context context) {
        super(context);
    }

    public GridViewRoomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewRoomItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

    }
}
