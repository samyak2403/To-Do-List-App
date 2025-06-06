package com.samyak.to_dolistapp.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.samyak.to_dolistapp.model.Task;

public class TaskRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private final LiveData<List<Task>> activeTasks;
    private final LiveData<List<Task>> completedTasks;
    private final ExecutorService executorService;
    
    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        activeTasks = taskDao.getActiveTasks();
        completedTasks = taskDao.getCompletedTasks();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    // Room executes all queries on a separate thread
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
        return taskDao.getTaskById(id);
    }
    
    // You must call these on a non-UI thread
    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }
    
    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }
    
    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }
    
    public void deleteAllTasks() {
        executorService.execute(taskDao::deleteAllTasks);
    }
    
    public void deleteCompletedTasks() {
        executorService.execute(taskDao::deleteCompletedTasks);
    }
} 