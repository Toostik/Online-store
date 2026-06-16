package com.example.authservice.kafka;

import com.example.authservice.dao.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ProcessedEventRepository processedEventRepository;

//    @KafkaListener(topics = "users-registered", groupId = "users-consumers-group")
//    public void consume(UserDto user, Acknowledgment ack) {
//        try {
//            log.info("KAFKA_USER_REGISTERED_RECEIVED orderId={} eventId={}",
//                    user.getId(), user.getEventId());
//
//            if(processedEventRepository.existsById(user.getEventId())){
//                log.warn("KAFKA_EVENT_DUPLICATE eventId={}", user.getEventId());
//                ack.acknowledge();
//                return;
//            }
//
//            processedEventRepository.save(new ProcessedEvent(user.getEventId()));
//            ack.acknowledge();
//            log.info("NOTIFICATION_SENT orderId={}", user.getId());
//
//        }catch (Exception e) {
//            log.error("KAFKA_NOTIFICATION_ERROR orderId={} eventId={}",
//                    user.getId(), user.getEventId(), e);
//
//            throw e;
//        }
//    }

}