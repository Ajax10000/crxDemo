package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Job {

    private final int id;
    private final String name;
    private final int status;

    public Job() {
    }
    

    public Job(@JsonProperty("id") int id, 
        @JsonProperty("name") String name, 
        @JsonProperty("status") int status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Job(@JsonProperty("name") String name, 
        @JsonProperty("status") int status) {
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }
}