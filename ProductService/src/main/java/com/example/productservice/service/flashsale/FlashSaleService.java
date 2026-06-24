package com.example.productservice.service.flashsale;

import com.example.productservice.dto.flashsale.CreateFlashSaleOrderRequest;
import com.example.productservice.dto.flashsale.FlashSaleReservationResponse;
import com.example.productservice.dto.flashsale.ReserveFlashSaleRequest;
import com.example.productservice.dto.flashsale.request.FlashSaleCreateRequest;
import com.example.productservice.service.flashsale.command.FlashSaleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlashSaleService {

    private final FlashSaleCommandService commandService;

    public FlashSaleReservationResponse reserve(Long id, ReserveFlashSaleRequest request) {

        return commandService.reserve(id, request);
    }

    public void createFlashSaleOrder(String reservationKey, CreateFlashSaleOrderRequest request) {

        commandService.createFlashSaleOrder(reservationKey, request);
    }

    public void createFlashSale(Long productId, FlashSaleCreateRequest request) {

        commandService.createFlashSale(productId, request);

    }
}
