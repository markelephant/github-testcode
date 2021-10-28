package io.netty.example.study.mq.kafkamq;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerClass extends Thread{

    KafkaConsumer<Integer,String> kafkaConsumer;
    String topic;

    public KafkaConsumerClass(String topic){
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.1.111:9092");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG,"my-consumer");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"my-gid2");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"30000");
        kafkaConsumer = new KafkaConsumer<>(properties);
        this.topic = topic;
    }

    @Override
    public void run() {
        kafkaConsumer.subscribe(Collections.singleton(this.topic));
        while(true){
            ConsumerRecords<Integer, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));
            consumerRecords.forEach(record-> {
                System.out.println("key: "+record.key()+" value: "+record.value()+" offset: "+record.offset());
            });
        }
    }

    public static void main(String[] args) {
        new KafkaConsumerClass("practice").start();



    }

}
