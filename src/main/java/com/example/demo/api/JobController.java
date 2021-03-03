package com.example.demo.api;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import com.example.demo.kafka.constants.IKafkaConstants;
import com.example.demo.kafka.consumer.ConsumerCreator;
import com.example.demo.kafka.producer.ProducerCreator;
import com.example.demo.model.Job;
import com.example.demo.service.JobService;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

@RequestMapping(value="api/v1/job", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RestController
public class JobController {
    Logger logger = LoggerFactory.getLogger(JobController.class);
    private final JobService jobService;
    private final Producer<Integer, String> kProducer = ProducerCreator.createProducer();
    private final Consumer<Integer, String> kConsumer = ConsumerCreator.createConsumer();
    static final int NEW = 0;
    static final int INPROGRESS = 1;
    static final int DONE = 2;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public int addJob(@RequestBody Job job) {
        int id = jobService.addJob(job);
        submitJob(job);
        consumeJob();
        return id;
    }

    @GetMapping
    public int getStatus(int id) {
        int status = jobService.getStatus(id);
        return status;
    }

    private void submitJob(Job job) {
        // Submit job to Kafka for later consumption
        for (int index = 0; index < IKafkaConstants.MESSAGE_COUNT; index++) {
            // Using constructor of the form ProducerRecord(String topic, Integer partition, K key, V value)
            ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(IKafkaConstants.TOPIC_NAME,
                index, (Integer)job.getId(), job.getName());
            try {
                RecordMetadata metadata = kProducer.send(record).get();
                logger.info("Record sent with key {} to partition {} with offset {}", 
                    index, metadata.partition(), metadata.offset());
            } catch (ExecutionException e) {
                logger.error("Error in sending record");
                logger.error(e.getMessage());
            } catch (InterruptedException e) {
                logger.error("Error in sending record");
                logger.error(e.getMessage());
            }
        } // for
    }

    // I am not familiar with Kafka
    // Most of the code in this function 
    // came from https://dzone.com/articles/kafka-producer-and-consumer-example
    private void consumeJob() {
        // TODO: consume job from Kafka
        ConsumerRecords<Integer, String> records = kConsumer.poll(Duration.ofMillis(100));
         for (ConsumerRecord<Integer, String> record : records) {
            logger.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
            Job job = jobService.getJob(record.key());
            job.setStatus(INPROGRESS);
            
            // Sleep for a random interval between 1 to 5 seconds
            // to mimic work.
            long timeLength = (long) Math.floor(5*Math.random()) + 1;
            try {
                Thread.sleep(timeLength*1000);
            } catch(InterruptedException ie) {
                // Do nothing
            }
        } // for
        // Update 
    }
}
