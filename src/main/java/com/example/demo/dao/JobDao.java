package com.example.demo.dao;

import com.example.demo.model.Job;

public interface JobDao {
    
    int insertJob(Job job);

    int getStatus(int id);
}
