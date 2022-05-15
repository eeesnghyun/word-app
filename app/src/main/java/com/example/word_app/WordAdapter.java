package com.example.word_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    ArrayList<WordEntity> items = new ArrayList<WordEntity>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.data_main, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        WordEntity item = items.get(position);

        Glide.with(viewHolder.itemView.getContext())
                .load("https://eeesnghyun.github.io/word-app/images/" + item.getImage())
                .into(viewHolder.imageView);

        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(WordEntity item) {
        items.add(item);
    }

    public void setItems(ArrayList<WordEntity> items) {
        this.items = items;
    }

    public WordEntity getItem(int position) {
        return items.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView2;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textContent);
            textView2 = itemView.findViewById(R.id.textSpeaker);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void setItem(WordEntity word) {
            textView.setText(word.getContent());
            textView2.setText("- " + word.getSpeaker());
        }
    }
}