package com.samyak.to_dolistapp.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.samyak.to_dolistapp.R;
import com.samyak.to_dolistapp.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {
    private OnItemClickListener listener;
    private OnCheckBoxClickListener checkBoxListener;
    private int lastPosition = -1;

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.isCompleted() == newItem.isCompleted() &&
                    oldItem.getPriority() == newItem.getPriority() &&
                    oldItem.getDueDate() == newItem.getDueDate();
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = getItem(position);
        holder.bind(currentTask);
        
        // Apply animation only when item is newly added
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), 
                    android.R.anim.fade_in);
            animation.setDuration(300);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull TaskViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public Task getTaskAt(int position) {
        return getItem(position);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBoxCompleted;
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final TextView textViewDueDate;
        private final View priorityIndicator;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewDueDate = itemView.findViewById(R.id.text_view_due_date);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);

            // Set click listener with ripple effect
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });

            // Set checkbox click listener
            checkBoxCompleted.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (checkBoxListener != null && position != RecyclerView.NO_POSITION) {
                    boolean isChecked = checkBoxCompleted.isChecked();
                    checkBoxListener.onCheckBoxClick(getItem(position), isChecked);
                    
                    // Apply completion animation
                    if (isChecked) {
                        textViewTitle.animate()
                                .alpha(0.5f)
                                .setDuration(300)
                                .start();
                    } else {
                        textViewTitle.animate()
                                .alpha(1.0f)
                                .setDuration(300)
                                .start();
                    }
                }
            });
        }

        public void bind(Task task) {
            textViewTitle.setText(task.getTitle());
            textViewDescription.setText(task.getDescription());
            
            // Format and set the due date
            if (task.getDueDate() > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(new Date(task.getDueDate()));
                textViewDueDate.setText("Due: " + formattedDate);
                textViewDueDate.setVisibility(View.VISIBLE);
            } else {
                textViewDueDate.setVisibility(View.GONE);
            }
            
            // Set priority indicator color
            int priorityColor;
            switch (task.getPriority()) {
                case 1: // High
                    priorityColor = R.color.priority_high;
                    break;
                case 2: // Medium
                    priorityColor = R.color.priority_medium;
                    break;
                case 3: // Low
                    priorityColor = R.color.priority_low;
                    break;
                default:
                    priorityColor = R.color.priority_medium;
            }
            priorityIndicator.setBackgroundResource(priorityColor);
            
            // Set completed state
            checkBoxCompleted.setChecked(task.isCompleted());
            if (task.isCompleted()) {
                textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textViewTitle.setAlpha(0.5f);
                textViewDescription.setAlpha(0.5f);
            } else {
                textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                textViewTitle.setAlpha(1.0f);
                textViewDescription.setAlpha(1.0f);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClick(Task task, boolean isChecked);
    }

    public void setOnCheckBoxClickListener(OnCheckBoxClickListener listener) {
        this.checkBoxListener = listener;
    }
} 