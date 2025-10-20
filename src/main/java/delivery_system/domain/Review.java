package delivery_system.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.persistence.*;
import lombok.Data;
import delivery_system.ReviewserviceApplication;

@Entity
@Table(name = "Review_table")
@Data
//<<< DDD / Aggregate Root
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public static ReviewRepository repository() {
        ReviewRepository reviewRepository = ReviewserviceApplication.applicationContext.getBean(
            ReviewRepository.class
        );
        return reviewRepository;
    }
}
//>>> DDD / Aggregate Root
