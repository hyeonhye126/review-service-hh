package delivery_system.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

//<<< Clean Arch / Outbound Adaptor
@Component
public abstract class AbstractEvent {

    // Getter/Setter
    @Getter
    @Setter
    private String eventType;
    @Getter
    @Setter
    private Long timestamp;

    @Autowired
    private transient StreamBridge streamBridge;

    public AbstractEvent(Object aggregate) {
        this();
        BeanUtils.copyProperties(aggregate, this);
    }

    public AbstractEvent() {
        this.eventType = this.getClass().getSimpleName();
        this.timestamp = System.currentTimeMillis();
    }

    // ✅ 기본 토픽 이름 지정 (application.yml의 destination과 일치해야 함)
    private static final String DEFAULT_TOPIC = "untitled";

    // 메시지 발행
    public void publish() {
        publish(DEFAULT_TOPIC, this);
    }

    public void publish(String topic, Object payload) {
        streamBridge.send(topic, payload);
        System.out.println("📤 Published to topic: " + topic + " payload: " + payload);
    }

    // 트랜잭션 커밋 이후 발행
    public void publishAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        AbstractEvent.this.publish();
                    }
                }
        );
    }

    public boolean validate() {
        return getEventType().equals(getClass().getSimpleName());
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }
    }
}
//>>> Clean Arch / Outbound Adaptor
