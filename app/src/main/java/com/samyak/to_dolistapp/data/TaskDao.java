package com.samyak.to_dolistapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.samyak.to_dolistapp.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);
    
    @Update
    void update(Task task);
    
    @Delete
    void delete(Task task);
    
    @Query("DELETE FROM tasks")
    void deleteAllTasks();
    
    @Query("DELETE FROM tasks WHERE completed = 1")
    void deleteCompletedTasks();
    
    @Query("SELECT * FROM tasks ORDER BY priority, dueDate ASC")
    LiveData<List<Task>> getAllTasks();
    
    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY priority, dueDate ASC")
    LiveData<List<Task>> getActiveTasks();
    
    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY priority, dueDate ASC")
    LiveData<List<Task>> getCompletedTasks();
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<Task> getTaskById(int id);
} 