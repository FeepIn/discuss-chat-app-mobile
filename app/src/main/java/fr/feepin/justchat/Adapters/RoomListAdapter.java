package fr.feepin.justchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.feepin.justchat.R;
import fr.feepin.justchat.models.RoomDetails;

public class RoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RoomDetails[] rooms;
    private Context context;
    private OnRoomClick onRoomClick;
    private OnCreateClick onCreateClick;

    public RoomListAdapter(Context context, RoomDetails[] rooms, OnRoomClick onRoomClick, OnCreateClick onCreateClick){
        this.onRoomClick = onRoomClick;
        this.rooms = rooms;
        this.context = context;
        this.onCreateClick = onCreateClick;
    }

    private final int ADDING_COMPONENT = 8;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ADDING_COMPONENT){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.room_create_item, parent, false);
            return new CreateViewHolder(view);
        }

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.room_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder)holder;
            viewHolder.roomName.setText(rooms[position].getName());
            viewHolder.roomUserCount.setText(rooms[position].getUserCount()+"");
            viewHolder.rootView.setBackgroundColor(rooms[position].getColorId());
            viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRoomClick.clicked(rooms[position].getName());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == rooms.length ? ADDING_COMPONENT : 1;
    }

    @Override
    public int getItemCount() {
        return rooms.length+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView roomName;
        public TextView roomUserCount;
        public View rootView;

        public ViewHolder(View rootView){
            super(rootView);
            this.rootView = rootView;
            this.roomName = rootView.findViewById(R.id.roomName);
            this.roomUserCount = rootView.findViewById(R.id.roomUserCount);
        }
    }

    public class CreateViewHolder extends RecyclerView.ViewHolder{
        private View root;

        public CreateViewHolder(@NonNull View root) {
            super(root);
            this.root = root;
            this.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCreateClick.clicked();
                }
            });
        }
    }

    //Interfaces

    public interface OnRoomClick{
        void clicked(String roomName);
    }

    public interface OnCreateClick{
        void clicked();
    }

}
