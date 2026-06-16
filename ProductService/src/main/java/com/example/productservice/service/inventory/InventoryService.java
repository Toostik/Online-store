package com.example.productservice.service.inventory;

import com.example.productservice.dto.product.request.CheckProductRequest;
import com.example.productservice.dto.product.request.CheckProductResponse;
import com.example.productservice.service.inventory.command.InventoryCommandService;
import com.example.productservice.service.inventory.query.InventoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.InventoryCommitRequestedEvent;
import org.example.events.inventory.InventoryReleaseRequestedEvent;
import org.example.events.inventory.InventoryReserveRequestedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryCommandService inventoryCommandService;
    private final InventoryQueryService inventoryQueryService;


    public Boolean isProductExists(List<Long> ids) {
        return inventoryQueryService.isProductExists(ids);
    }

    public CheckProductResponse getProductAvailability(CheckProductRequest request) {
        return inventoryQueryService.getProductAvailability(request);
    }


    public void reserve(
            InventoryReserveRequestedEvent event
    ){
        inventoryCommandService.reserve(event);
    }

    public void releaseInventory(InventoryReleaseRequestedEvent event){
        inventoryCommandService.releaseInventory(event);
    }

    public void commitInventory(
            InventoryCommitRequestedEvent event
    ){

        inventoryCommandService.commitInventory(event);

    }
}
