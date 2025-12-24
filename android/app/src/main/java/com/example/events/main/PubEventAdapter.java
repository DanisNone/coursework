package com.example.events.main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.events.R;
import com.example.events.model.Event;
import com.example.events.model.PublicEvent;

import java.util.List;

public class PubEventAdapter extends RecyclerView.Adapter<PubEventAdapter.EventViewHolder> {

    private List<PublicEvent> pubEventList;

    public PubEventAdapter(List<PublicEvent> pubEventList) {
        this.pubEventList = pubEventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        PublicEvent event = pubEventList.get(position);

        holder.tvName.setText(event.getName());
        holder.tvOwnerInfo.setText(event.getOwnerName() + " " + event.getOwnerSurname());
        holder.tvTime.setText(event.getStartTime() + " - " + event.getEndTime());
        holder.tvLocation.setText(event.getFull_location() + ", " + event.getCity());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("name", event.getName());
            intent.putExtra("ownerInfo", event.getOwnerName() + " " + event.getOwnerSurname());
            intent.putExtra("time", event.getStartTime() + " - " + event.getEndTime());
            intent.putExtra("location", event.getFull_location());
            intent.putExtra("description", event.getDescription());
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return pubEventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvOwnerInfo, tvTime, tvLocation;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvOwnerInfo = itemView.findViewById(R.id.tvCreatorInfo);
            tvTime = itemView.findViewById(R.id.tvEventTime);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
        }
    }
}
