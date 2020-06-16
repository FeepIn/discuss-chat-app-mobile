package fr.feepin.justchat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.feepin.justchat.R;
import fr.feepin.justchat.Shared;
import fr.feepin.justchat.models.User;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    public ArrayList<User> users;
    private Context context;
    private ViewGroup parent;
    private View contextMenuView;
    private ViewGroup container;
    private int width, height;
    private OnContextMenuOpen onContextMenuOpen;
    private View selectedView;

    public ChatRoomAdapter(Context context, ArrayList<User> users, View contextMenuView, ViewGroup container, OnContextMenuOpen onContextMenuOpen){
        this.context = context;
        this.users = users;
        this.contextMenuView = contextMenuView;
        this.onContextMenuOpen = onContextMenuOpen;
        this.container = container;

        //Get viewport size
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    public View getSelectedView() {
        return selectedView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        if (user.getName().equals("System")){
            holder.nameView.setTextColor(context.getResources().getColor(R.color.black));

            SpannableString spannableString = new SpannableString(user.getMessage());
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor(user.getColor()));

            spannableString.setSpan(foregroundColorSpan, 0, user.getUserJoined().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.nameView.setText(spannableString);
            ((ViewGroup)holder.root).removeView(holder.messageView);

            return;
        }

        holder.nameView.setText(user.getName());
        holder.nameView.setTextColor(Color.parseColor(user.getColor()));
        holder.messageView.setText(user.getMessage());

        if (user.isHost()){
            holder.nameView.setCompoundDrawablesWithIntrinsicBounds(null,null, context.getResources().getDrawable(R.drawable.crown), null);
        }
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener,
            GestureDetector.OnGestureListener {
        public View root;
        public TextView nameView;
        public TextView messageView;
        private GestureDetector gestureDetector;

        public ViewHolder(@NonNull View root) {
            super(root);
            this.root = root;
            this.nameView = root.findViewById(R.id.userName);
            this.messageView = root.findViewById(R.id.message);
            this.root.setOnTouchListener(this);

            gestureDetector = new GestureDetector(context, this);
        }


        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            container.removeView(contextMenuView);

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }


        @Override
        public void onLongPress(MotionEvent e) {
            if (!Shared.hostname.equals(Shared.userName)){
                return;
            }
            container.removeView(contextMenuView);
            container.addView(contextMenuView);
            selectedView = root;
            selectedView.setBackgroundColor(context.getResources().getColor(R.color.grey));
            ScaleAnimation scaleAnimation;
            if (e.getRawX() >= width-contextMenuView.getWidth() && e.getRawY()<=contextMenuView.getHeight()) {
                contextMenuView.setX(e.getRawX() - contextMenuView.getWidth());
                contextMenuView.setY(e.getRawY());
                scaleAnimation = new ScaleAnimation(0, 1, 0, 1, contextMenuView.getX() + contextMenuView.getWidth(), contextMenuView.getY() );
            }else if (e.getRawX() >= width-contextMenuView.getWidth() && e.getRawY()>=height-contextMenuView.getHeight()){
                contextMenuView.setX(e.getRawX()-contextMenuView.getWidth());
                contextMenuView.setY(e.getRawY()-contextMenuView.getHeight());
                scaleAnimation = new ScaleAnimation(0, 1, 0, 1, contextMenuView.getX()+contextMenuView.getWidth(), contextMenuView.getY()+contextMenuView.getHeight());

            }else if (e.getRawY()>=height-contextMenuView.getHeight()){
                contextMenuView.setX(e.getRawX());
                contextMenuView.setY(e.getRawY()-contextMenuView.getHeight());
                scaleAnimation = new ScaleAnimation(0, 1, 0, 1, contextMenuView.getX(), contextMenuView.getY()+contextMenuView.getHeight());
            }else if (e.getRawY()<=contextMenuView.getHeight()){
                contextMenuView.setX(e.getRawX());
                contextMenuView.setY(e.getRawY());
                scaleAnimation = new ScaleAnimation(0, 1, 0, 1, contextMenuView.getX(), contextMenuView.getY());
            }else if (e.getRawX() >= width-contextMenuView.getWidth()){
                contextMenuView.setX(e.getRawX()-contextMenuView.getWidth());
                contextMenuView.setY(e.getRawY()-contextMenuView.getHeight());
                scaleAnimation = new ScaleAnimation(0, 1, 0, 1, contextMenuView.getX()+contextMenuView.getWidth(), contextMenuView.getY()+contextMenuView.getHeight());


            }else{
                contextMenuView.setX(e.getRawX());
                contextMenuView.setY(e.getRawY()-contextMenuView.getHeight());
                scaleAnimation = new ScaleAnimation(0, 1, 0, 1, contextMenuView.getX(), contextMenuView.getY()+contextMenuView.getHeight());
            }


            scaleAnimation.setDuration(200);
            contextMenuView.setAnimation(scaleAnimation);

            onContextMenuOpen.onOpen(nameView, root);

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN && selectedView !=null){
                container.removeView(contextMenuView);
                selectedView.setBackgroundColor(context.getResources().getColor(R.color.belgium));

            }

            gestureDetector.onTouchEvent(event);

            return true;
        }
    }

    //Interfaces
    public interface OnContextMenuOpen{
        void onOpen(TextView nameView, View root);
    }
}
