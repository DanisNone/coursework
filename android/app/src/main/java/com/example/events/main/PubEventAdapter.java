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
        String name = event.getName();
        String ownerInfo = event.getPublicUser().getName() + " " + event.getPublicUser().getSurname();
        String timeStr = event.getStartTime() + " - " + event.getEndTime();
        String location = event.getFull_location() + ", " + event.getCity();
        String description = event.getDescription();

        holder.tvName.setText(name);
        holder.tvOwnerInfo.setText(ownerInfo);
        holder.tvTime.setText(timeStr);
        holder.tvLocation.setText(location);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("ownerInfo", ownerInfo);
            intent.putExtra("time", timeStr);
            intent.putExtra("location", location);
            intent.putExtra("description", description);
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
