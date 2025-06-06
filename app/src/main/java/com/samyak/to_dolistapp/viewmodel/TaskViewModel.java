package com.samyak.to_dolistapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

import com.samyak.to_dolistapp.data.TaskRepository;
import com.samyak.to_dolistapp.model.Task;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repository;
    private final LiveData<List<Task>> allTasks;
    private final LiveData<List<Task>> activeTasks;
    private final LiveData<List<Task>> completedTasks;
    
    public TaskViewModel(Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
        activeTasks = repository.getActiveTasks();
        completedTasks = repository.getCompletedTasks();
    }
    
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }
    
    public LiveData<List<Task>> getActiveTasks() {
        return activeTasks;
    }
    
    public LiveData<List<Task>> getCompletedTasks() {
        return completedTasks;
    }
    
    public LiveData<Task> getTaskById(int id) {
        return repository.getTaskById(id);
    }
    
    public void insert(Task task) {
        repository.insert(task);
    }
    
    public void update(Task task) {
        repository.update(task);
    }
    
    public void delete(Task task) {
        repository.delete(task);
    }
    
    public void deleteAllTasks() {
        repository.deleteAllTasks();
    }
    
    public void deleteCompletedTasks() {
        repository.deleteCompletedTasks();
    }
} 