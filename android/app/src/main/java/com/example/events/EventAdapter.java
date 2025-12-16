package com.example.events;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
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
        Event event = eventList.get(position);

        holder.tvName.setText(event.name);
        holder.tvTime.setText(event.startTime + " - " + event.endTime);
        holder.tvLocation.setText(event.full_location + ", " + event.city);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("name", event.name);
            intent.putExtra("time", event.startTime + " - " + event.endTime);
            intent.putExtra("location", event.full_location + ", " + event.city);
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvLocation;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvTime = itemView.findViewById(R.id.tvEventTime);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
        }
    }
}
