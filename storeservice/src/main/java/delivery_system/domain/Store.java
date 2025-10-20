package delivery_system.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;

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
    private UUID store_id;

    //owner_id: 점주 식별자
    //store_name: 가게명
    //delivery_fee: 배달비
    //store_address: 주소 원문
    //store_geom: 주소 좌표
    //store_rating_avg: 평균 평점
    //store_review_count: 리뷰 개수
    //created_by: 가게 생성시각
    //updated_by: 가게 수정자
    //deleted_at: 가게 삭제시각
    //deleted_by: 가게 삭제자


    public static StoreRepository repository() {
        StoreRepository storeRepository = StoreserviceApplication.applicationContext.getBean(
            StoreRepository.class
        );
        return storeRepository;
    }
}
//>>> DDD / Aggregate Root
