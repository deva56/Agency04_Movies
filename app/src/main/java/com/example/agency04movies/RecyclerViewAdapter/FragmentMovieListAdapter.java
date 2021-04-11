/*
Recyclerview adapter used in fragment.
 */

package com.example.agency04movies.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agency04movies.Models.MoviesItem;
import com.example.agency04movies.R;

import java.util.List;

import static com.example.agency04movies.Constants.image_base_url;

public class FragmentMovieListAdapter extends RecyclerView.Adapter<FragmentMovieListAdapter.FragmentMovieListHolder> {

    private final List<MoviesItem> records;
    private final Context context;

    private OnItemClickListener listener;

    public FragmentMovieListAdapter(List<MoviesItem> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public FragmentMovieListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_fragment_item, parent, false);
        return new FragmentMovieListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentMovieListHolder holder, int position) {
        MoviesItem currentRecord = records.get(position);
        holder.headerText.setText(currentRecord.getTitle());
        Glide.with(context).load(image_base_url + currentRecord.getPoster_path()).placeholder(R.drawable.ic_no_image_available).centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (records != null) {
            return records.size();
        } else {
            return 0;
        }
    }

    class FragmentMovieListHolder extends RecyclerView.ViewHolder {
        private TextView headerText;
        private ImageView imageView;

        public FragmentMovieListHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.MovieListFragmentItemTitle);
            imageView = itemView.findViewById(R.id.MovieListFragmentItemImageView);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(records.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MoviesItem NewsItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }
}
