package delivery_system.store.domain.entity;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

import delivery_system.store.domain.repository.StoreRepository;
import jakarta.persistence.*;
import lombok.Data;
import delivery_system.StoreserviceApplication;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "Store_table")
@Data
public class StoreEntity {

    @Id
    @Column(name = "store_id", columnDefinition = "uuid", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID store_id; //가게 식별자

    @Column(name = "owner_id", nullable = false)
    private String owner_id; //점주식별자

    @Column(name = "store_name", nullable = false)
    private String storeName; //가게명

    @Column(name = "description")
    private String description; //가게 소개

    @Column(name = "delivery_fee")
    private Integer deliveryFee; //배달비

    @Column(name = "store_address", nullable = false)
    private String storeAddress; //주소 원문

    @Column(name = "store_geom")
    private String storeGeom; //주소 좌표

    @Column(name = "store_rating_avg", nullable = false)
    private DecimalFormat storeRatingAvg; //평균 평점

    @Column(name = "store_review_count", nullable = false)
    private Integer storeReviewCount; //리뷰 갯수

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;
}
