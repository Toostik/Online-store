package com.example.productservice.controller.flashsale.command;

import com.example.productservice.dto.flashsale.CreateFlashSaleOrderRequest;
import com.example.productservice.dto.flashsale.FlashSaleReservationResponse;
import com.example.productservice.dto.flashsale.ReserveFlashSaleRequest;
import com.example.productservice.dto.flashsale.request.FlashSaleCreateRequest;
import com.example.productservice.entity.flashsale.FlashSaleReservation;
import com.example.productservice.service.flashsale.FlashSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/flash-sales")
public class FlashSaleCommandController {

    private final FlashSaleService flashSaleService;

    @PostMapping("/{id}/reserve")
    public ResponseEntity<FlashSaleReservationResponse> reserve(
            @PathVariable("id") Long flashSaleId,
            @RequestBody ReserveFlashSaleRequest request
    ) {

        return ResponseEntity.ok(flashSaleService.reserve(flashSaleId,request));
    }

    @PostMapping("/{reservationKey}/checkout")
    public ResponseEntity<Void> createFlashSaleOrder(
            @PathVariable String reservationKey,
            @RequestBody CreateFlashSaleOrderRequest request
    ) {

        flashSaleService.createFlashSaleOrder(reservationKey, request);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> createFlashSale(@PathVariable("id") Long productId, @RequestBody FlashSaleCreateRequest request){

        flashSaleService.createFlashSale(productId, request);

        return ResponseEntity.ok().build();
    }


}
