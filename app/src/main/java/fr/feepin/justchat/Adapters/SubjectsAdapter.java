package fr.feepin.justchat.Adapters;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Interpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fr.feepin.justchat.R;
import fr.feepin.justchat.Shared;
import fr.feepin.justchat.models.Subject;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<Subject> subjects, subjectsFull;
    private OnSubjectClick onSubjectClick;
    public SubjectsAdapter(Context context, ArrayList<Subject> subjects, OnSubjectClick onSubjectClick){
        this.context = context;
        this.subjects = subjects;
        this.subjectsFull = new ArrayList<>();
        this.subjectsFull.addAll(subjects);
        this.onSubjectClick = onSubjectClick;
        adjustPositioning();
    }

    private void adjustPositioning(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.subject_list_item, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Subject subject = subjects.get(position);
        String subjectName;
        switch (subject.getName()){
            case "mangaAnime":
                subjectName = context.getResources().getString(R.string.mangaAnime);
                break;
            case "love":
                subjectName = context.getResources().getString(R.string.love);
                break;
            case "videoGames":
                subjectName = context.getResources().getString(R.string.videoGames);
                break;
            case "space":
                subjectName = context.getResources().getString(R.string.space);
                break;
            case "culture":
                subjectName = context.getResources().getString(R.string.culture);
                break;
            case "music":
                subjectName = context.getResources().getString(R.string.music);
                break;
            default:
                subjectName = "null";
                break;
        }
        holder.backgroundImage.setImageDrawable(subject.getBackgroundImg());
        holder.userCount.setText(subject.getUserCount()+"");
        holder.subject.setText(subjectName);
        holder.heartIcon.setImageDrawable(subject.isLiked() ? context.getResources().getDrawable(R.drawable.liked_heart_icon) : context.getResources().getDrawable(R.drawable.unliked_heart_icon));

        holder.heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subject.isLiked()){

                    subject.setLiked(false);
                    holder.heartIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.unliked_heart_icon));
                    Shared.database.deleteSubject(subject.getName());
                }else{
                    holder.heartIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.like_heart_animation));
                    subject.setLiked(true);
                    holder.heartIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.liked_heart_icon));
                    Shared.database.insertSubject(subject.getName(), true);

                }

            }
        });

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubjectClick.clicked(subject.getName());
            }
        });

    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Subject> filteredSubjects = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredSubjects.addAll(subjectsFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Subject subject : subjectsFull){
                    if (subject.getName().toLowerCase().startsWith(filterPattern)){
                        filteredSubjects.add(subject);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSubjects;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            subjects.clear();
            subjects.addAll((ArrayList<Subject>)results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder{
        private View root;
        private ImageView backgroundImage;
        public ImageView heartIcon;
        private TextView subject;
        private TextView userCount;
        public ViewHolder(View root){
            super(root);
            this.root = root;
            backgroundImage = root.findViewById(R.id.subjectBackgroundImg);
            heartIcon = root.findViewById(R.id.heart);
            subject = root.findViewById(R.id.subject);
            userCount = root.findViewById(R.id.userCount);

        }
    }

    public interface OnSubjectClick{
        public void clicked(String subjectName);
    }
}
