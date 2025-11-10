package delivery_system.common;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import delivery_system.review.domain.repository.ReviewRepositoryV1;

import java.util.function.Consumer;

@Service
@Transactional
@Configuration
public class PolicyHandler {

    private final ReviewRepositoryV1 reviewRepository;

    public PolicyHandler(ReviewRepositoryV1 reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // ✅ Kafka 메시지를 수신하는 함수형 Consumer
    @Bean
    public Consumer<Message<String>> handleMessage() {
        return message -> {
            String eventString = message.getPayload();
            System.out.println("📥 Received event: " + eventString);

            // TODO: 여기에 실제 로직 작성 (예: DB 업데이트 등)
        };
    }
}
