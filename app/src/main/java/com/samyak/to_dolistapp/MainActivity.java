package com.samyak.to_dolistapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.samyak.to_dolistapp.adapter.TaskAdapter;
import com.samyak.to_dolistapp.model.Task;
import com.samyak.to_dolistapp.viewmodel.TaskViewModel;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private ConstraintLayout emptyStateContainer;
    private int currentFilterMode = 0; // 0: All, 1: Active, 2: Completed
    private long selectedDueDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        
        // Set up adapter
        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);
        
        // Set up empty view
        emptyStateContainer = findViewById(R.id.empty_state_container);
        
        // Set up ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        
        // Observe tasks
        observeTasks();
        
        // Set up FAB with animation
        FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);
        Animation fabAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fabAnimation.setDuration(500);
        fabAddTask.startAnimation(fabAnimation);
        
        fabAddTask.setOnClickListener(v -> {
            // Add a small scale animation when clicked
            v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                showAddEditTaskDialog(null);
            });
        });
        
        // Set up item click listener
        adapter.setOnItemClickListener(task -> showAddEditTaskDialog(task));
        
        // Set up checkbox click listener
        adapter.setOnCheckBoxClickListener((task, isChecked) -> {
            task.setCompleted(isChecked);
            taskViewModel.update(task);
            
            // Show snackbar on task completion
            if (isChecked) {
                Snackbar.make(findViewById(R.id.main), "Task completed!", Snackbar.LENGTH_SHORT).show();
            }
        });
        
        // Set up swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Task deletedTask = adapter.getTaskAt(viewHolder.getAdapterPosition());
                taskViewModel.delete(deletedTask);
                
                Snackbar.make(findViewById(R.id.main), "Task deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> taskViewModel.insert(deletedTask))
                        .setActionTextColor(getResources().getColor(R.color.colorAccent))
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }
    
    private void observeTasks() {
        // Remove all observers first
        if (taskViewModel.getAllTasks().hasActiveObservers()) {
            taskViewModel.getAllTasks().removeObservers(this);
        }
        if (taskViewModel.getActiveTasks().hasActiveObservers()) {
            taskViewModel.getActiveTasks().removeObservers(this);
        }
        if (taskViewModel.getCompletedTasks().hasActiveObservers()) {
            taskViewModel.getCompletedTasks().removeObservers(this);
        }
        
        // Observe based on filter mode
        switch (currentFilterMode) {
            case 0: // All tasks
                taskViewModel.getAllTasks().observe(this, tasks -> {
                    adapter.submitList(tasks);
                    updateEmptyState(tasks.isEmpty());
                });
                break;
            case 1: // Active tasks
                taskViewModel.getActiveTasks().observe(this, tasks -> {
                    adapter.submitList(tasks);
                    updateEmptyState(tasks.isEmpty());
                });
                break;
            case 2: // Completed tasks
                taskViewModel.getCompletedTasks().observe(this, tasks -> {
                    adapter.submitList(tasks);
                    updateEmptyState(tasks.isEmpty());
                });
                break;
        }
    }
    
    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            emptyStateContainer.setVisibility(View.VISIBLE);
            emptyStateContainer.setAlpha(0f);
            emptyStateContainer.animate().alpha(1f).setDuration(300);
        } else {
            if (emptyStateContainer.getVisibility() == View.VISIBLE) {
                emptyStateContainer.animate().alpha(0f).setDuration(300)
                        .withEndAction(() -> emptyStateContainer.setVisibility(View.GONE));
            }
        }
    }
    
    private void showAddEditTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_edit_task, null);
        builder.setView(view);
        
        // Get references to views
        TextView textViewTitle = view.findViewById(R.id.textView_dialog_title);
        EditText editTextTitle = view.findViewById(R.id.edit_text_title);
        EditText editTextDescription = view.findViewById(R.id.edit_text_description);
        RadioGroup radioGroupPriority = view.findViewById(R.id.radio_group_priority);
        RadioButton radioButtonHigh = view.findViewById(R.id.radio_button_high);
        RadioButton radioButtonMedium = view.findViewById(R.id.radio_button_medium);
        RadioButton radioButtonLow = view.findViewById(R.id.radio_button_low);
        Button buttonSetDueDate = view.findViewById(R.id.button_set_due_date);
        Button buttonCancel = view.findViewById(R.id.button_cancel);
        Button buttonSave = view.findViewById(R.id.button_save);
        
        // Set initial values if editing task
        if (task != null) {
            textViewTitle.setText("Edit Task");
            editTextTitle.setText(task.getTitle());
            editTextDescription.setText(task.getDescription());
            
            // Set priority
            switch (task.getPriority()) {
                case 1: // High
                    radioButtonHigh.setChecked(true);
                    break;
                case 2: // Medium
                    radioButtonMedium.setChecked(true);
                    break;
                case 3: // Low
                    radioButtonLow.setChecked(true);
                    break;
            }
            
            // Set due date
            selectedDueDate = task.getDueDate();
            if (selectedDueDate > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selectedDueDate);
                buttonSetDueDate.setText(String.format("%d/%d/%d", 
                        calendar.get(Calendar.MONTH) + 1, 
                        calendar.get(Calendar.DAY_OF_MONTH), 
                        calendar.get(Calendar.YEAR)));
            }
        } else {
            // Reset due date for new task
            selectedDueDate = 0;
        }
        
        // Set due date button click listener
        buttonSetDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (selectedDueDate > 0) {
                calendar.setTimeInMillis(selectedDueDate);
            }
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);
                        selectedDueDate = selectedCalendar.getTimeInMillis();
                        buttonSetDueDate.setText(String.format("%d/%d/%d", month + 1, dayOfMonth, year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        // Create dialog
        AlertDialog dialog = builder.create();
        
        // Set button click listeners
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        
        buttonSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            
            // Validate input
            if (title.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get priority
            int priority;
            int checkedRadioButtonId = radioGroupPriority.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.radio_button_high) {
                priority = 1;
            } else if (checkedRadioButtonId == R.id.radio_button_medium) {
                priority = 2;
            } else {
                priority = 3;
            }
            
            // Save task
            if (task != null) {
                // Update existing task
                task.setTitle(title);
                task.setDescription(description);
                task.setPriority(priority);
                task.setDueDate(selectedDueDate);
                taskViewModel.update(task);
                Toast.makeText(MainActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
            } else {
                // Create new task
                Task newTask = new Task(title, description, priority, selectedDueDate);
                taskViewModel.insert(newTask);
                Toast.makeText(MainActivity.this, "Task added", Toast.LENGTH_SHORT).show();
            }
            
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        // Handle filter options
        if (id == R.id.action_filter_all) {
            currentFilterMode = 0;
            item.setChecked(true);
            observeTasks();
            return true;
        } else if (id == R.id.action_filter_active) {
            currentFilterMode = 1;
            item.setChecked(true);
            observeTasks();
            return true;
        } else if (id == R.id.action_filter_completed) {
            currentFilterMode = 2;
            item.setChecked(true);
            observeTasks();
            return true;
        }
        
        // Handle delete options
        else if (id == R.id.action_delete_all_completed) {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Delete Completed Tasks")
                    .setMessage("Are you sure you want to delete all completed tasks?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        taskViewModel.deleteCompletedTasks();
                        Toast.makeText(MainActivity.this, "Completed tasks deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        } else if (id == R.id.action_delete_all) {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Delete All Tasks")
                    .setMessage("Are you sure you want to delete all tasks?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        taskViewModel.deleteAllTasks();
                        Toast.makeText(MainActivity.this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}