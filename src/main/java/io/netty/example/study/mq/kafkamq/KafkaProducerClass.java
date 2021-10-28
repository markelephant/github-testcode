package io.netty.example.study.mq.kafkamq;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class KafkaProducerClass extends Thread{

    KafkaProducer<Integer,String> kafkaProducer;
    String topic;

    public KafkaProducerClass(String topic){

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.1.111:9092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG,"my-producer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducer =  new KafkaProducer<>(properties);
        this.topic = topic;
    }

    @Override
    public void run() {
        int num =0;
        while (num <= 20){
            String msg ="my kafka test msg num: "+num;
            try {
                RecordMetadata recordMetadata = kafkaProducer.send(new ProducerRecord<>(topic, msg)).get();
                System.out.println("topic: "+recordMetadata.topic()+" partition: "+recordMetadata.partition()+" offset: "+recordMetadata.offset());
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            num++;
        }
    }

    public static void main(String[] args) {
        KafkaProducerClass kafkaProducerClass = new KafkaProducerClass("practice");
        kafkaProducerClass.start();

    }
}
