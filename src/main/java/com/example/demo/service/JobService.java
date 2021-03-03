package com.example.demo.service;

import com.example.demo.dao.JobDao;
import com.example.demo.model.Job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class JobService {
    
    private final JobDao jobDao;

    @Autowired
    public JobService(@Qualifier("realDao") JobDao jobDao) {
        this.jobDao = jobDao;
    }

    public int addJob(Job job) {
        return jobDao.insertJob(job);
    }

    public int getStatus(int id) {
        return jobDao.getStatus(id);
    }
}
