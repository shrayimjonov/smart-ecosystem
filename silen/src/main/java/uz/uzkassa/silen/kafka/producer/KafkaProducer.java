package uz.uzkassa.silen.kafka.producer;

public interface KafkaProducer<T> {
  void sendMessage(String topic, T message);
}
