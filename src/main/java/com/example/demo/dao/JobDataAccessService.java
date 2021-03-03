package com.example.demo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.example.demo.model.Job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("realDao")
public class JobDataAccessService implements JobDao {

    @Autowired
    DataSource dataSource;
    
    @Autowired 
    JdbcTemplate jdbcTemplate;
    
    public JobDataAccessService() {
        // Load the jdbc driver for PostgreSQL
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load JDBC driver for PostgreSQL");
            e.printStackTrace();
        }
    }

    @Override
    public int insertJob(Job job) {
        int generatedId = -1;
        String insertQuery = "insert into jobs(name, status) values(" + job.getName() + ", " + job.getStatus() + ")";
        PreparedStatement statement = null;

        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Got a null connection!");
            return -1;
        }
        
        statement = getPreparedStatement(conn, insertQuery); 
        if (statement == null) {
            System.out.println("Got a null PreparedStatement!");
            return -1;
        }

        try {
            int affectedRows = statement.executeUpdate();
    
            if (affectedRows == 0) {
                throw new SQLException("Creating job failed, no rows affected.");
            }
    
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating job failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred attempting to execute a prepared statement, query = " + insertQuery);
            e.printStackTrace();
        }
        return generatedId;
    }

    private Connection getConnection() {
        Connection conn = null;
        DataSource ds = jdbcTemplate.getDataSource();
        if (ds == null) {
            System.out.println("Got a null DataSource!");
            return null;
        }
        
        try {
            conn = ds.getConnection();
        } catch(Exception e) {
            System.out.println("Error occurred trying to get a Connection");
            e.printStackTrace();
        }
        return conn;
    }

    private PreparedStatement getPreparedStatement(Connection conn, String query) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch(Exception e) {
            System.out.println("Error occurred trying to get a PreparedStatement, query = " + query);
            e.printStackTrace();
        }
        return statement;
    }

    @Override 
    public int getStatus(int id) {
        int status = -1;
        String query = "select status from jobs where id=" + id;
        status = jdbcTemplate.queryForObject(query, new Object[] { id }, Integer.class);
        return status;
    }
}
