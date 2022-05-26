package microservices.core.productcomposite;

import microservices.api.event.Event;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.io.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(topics = { EmbeddedKafkaApplicationTests.INPUT_TOPIC, EmbeddedKafkaApplicationTests.OUTPUT_TOPIC },
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class EmbeddedKafkaApplicationTests {

    public static final String INPUT_TOPIC = "testEmbeddedIn";
    public static final String OUTPUT_TOPIC = "testEmbeddedOut";
    private static final String GROUP_NAME = "embeddedKafkaApplicationTest";

    
    @Test
    void testSendReceive(@Autowired EmbeddedKafkaBroker embeddedKafka) throws IOException{
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
        senderProps.put("key.serializer", ByteArraySerializer.class);
        senderProps.put("value.serializer", ByteArraySerializer.class);
        
        DefaultKafkaProducerFactory<byte[], byte[]> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<byte[], byte[]> template = new KafkaTemplate<>(pf, true);
        template.setDefaultTopic(INPUT_TOPIC);
        Event<String, String> event = new Event<>(Event.Type.CREATE, "A", "AA");
        
//        byte[] eb = convertObjectToBytes(event);
        
        template.sendDefault("foo".getBytes());
//        template.sendDefault(eb);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put("key.deserializer", ByteArrayDeserializer.class);
        consumerProps.put("value.deserializer", ByteArrayDeserializer.class);
        DefaultKafkaConsumerFactory<byte[], byte[]> cf = new DefaultKafkaConsumerFactory<>(consumerProps);

        Consumer<byte[], byte[]> consumer = cf.createConsumer();
        consumer.assign(Collections.singleton(new TopicPartition(OUTPUT_TOPIC, 0)));
        ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofSeconds(10));
        consumer.commitSync();

//        assertThat(records.count()).isEqualTo(1);
//        assertThat(new String(records.iterator().next().value())).isEqualTo("foo");
    }

    private byte[] convertObjectToBytes(Object obj) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        }
    }

    private Object convertBytesToObject(byte[] bytes)
            throws IOException, ClassNotFoundException {
        InputStream is = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        }
    }

}
