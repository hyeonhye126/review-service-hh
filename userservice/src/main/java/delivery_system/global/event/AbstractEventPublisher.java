package delivery_system.global.event;


import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
public class AbstractEventPublisher {

    private final StreamBridge streamBridge;

    public AbstractEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publish(String bindingName, Object payload, String type) {
        System.out.println("📤 Publishing event to " + bindingName + " with type: " + type);
        System.out.println("📤 Event payload: " + payload.toString());

        boolean result = streamBridge.send(bindingName, MessageBuilder
                .withPayload(payload)
                .setHeader("type", type)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());

        System.out.println("📤 Event publish result: " + result);
    }
}
