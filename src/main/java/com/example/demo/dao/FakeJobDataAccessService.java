package com.example.demo.dao;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Job;

import org.springframework.stereotype.Repository;

@Repository("fakeDao")
public class FakeJobDataAccessService implements JobDao {

    private static List<Job> DB = new ArrayList<>();
    
    @Override
    public int insertJob(Job job) {
        DB.add(new Job(job.getName(), job.getStatus()));
        return 0;
    }
    
    @Override
    public int getStatus(int id) {
        for (int i = 0; i < DB.size(); i++) {
            Job job = DB.get(i);
            if (job.getId() == id) {
                return job.getStatus();
            }
        }
        return -1;
        
    }
}
