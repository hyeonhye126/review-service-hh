package delivery_system.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.persistence.*;
import lombok.Data;
import delivery_system.StoreserviceApplication;

@Entity
@Table(name = "Store_table")
@Data
//<<< DDD / Aggregate Root
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public static StoreRepository repository() {
        StoreRepository storeRepository = StoreserviceApplication.applicationContext.getBean(
            StoreRepository.class
        );
        return storeRepository;
    }
}
//>>> DDD / Aggregate Root
