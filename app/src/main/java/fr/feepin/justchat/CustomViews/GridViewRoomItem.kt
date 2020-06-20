package fr.feepin.justchat.CustomViews

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout

class GridViewRoomItem(context: Context) : ConstraintLayout(context){

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}