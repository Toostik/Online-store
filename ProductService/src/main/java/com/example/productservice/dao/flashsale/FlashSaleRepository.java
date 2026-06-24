package com.example.productservice.dao.flashsale;

import com.example.productservice.entity.flashsale.FlashSale;
import com.example.productservice.entity.flashsale.FlashSaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    List<FlashSale> findByStatus(FlashSaleStatus flashSaleStatus);

    List<FlashSale> findByStatusAndStartsAtBefore(FlashSaleStatus flashSaleStatus, Instant now);

    List<FlashSale> findByStatusAndEndsAtBefore(FlashSaleStatus flashSaleStatus, Instant now);
}
