package com.example.orderservice.service.order;

import com.example.orderservice.dto.order.OrderDto;
import com.example.orderservice.dto.order.ProfileOrders;
import com.example.orderservice.dto.order.request.CreateOrderRequest;
import com.example.orderservice.dto.order.request.OrderPaymentInfoResponse;
import com.example.orderservice.service.order.command.OrderCommandService;
import com.example.orderservice.service.order.query.OrderQueryService;
import com.example.orderservice.service.order.query.RecentOrderService;
import lombok.RequiredArgsConstructor;
import org.example.events.flashsale.FlashSaleReservationAndCheckoutEvent;
import org.example.events.order.OrderAwaitingPaymentEvent;
import org.example.events.order.OrderCancelledEvent;
import org.example.events.order.OrderConfirmedEvent;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderCommandService commandService;
    private final OrderQueryService queryService;
    private final RecentOrderService recentOrderService;

    public OrderDto createOrder(CreateOrderRequest request) {
        return commandService.createOrder(request);
    }

    public OrderDto getOrderById(Long id) {
        return queryService.getOrderById(id);
    }

    public List<OrderDto> getCurrentUserOrders() {
        return queryService.getAllOrdersOfCurrentUser();
    }

    public ProfileOrders getRecentItems(Integer size) {
        return recentOrderService.getRecentItems(size);
    }


    public void awaitingPayment(
            OrderAwaitingPaymentEvent event
    ){
        commandService.awaitingPayment(event);
    }

    public void confirm(OrderConfirmedEvent event){
        commandService.confirm(event);
    }

    public OrderPaymentInfoResponse getPaymentInfo(Long orderId){
        return queryService.getPaymentInfo(orderId);
    }

    public void cancel(
            OrderCancelledEvent event
    ) {

        commandService.cancel(
                event
        );

    }

    public void createOrderByFlashSale(FlashSaleReservationAndCheckoutEvent event){
        commandService.createOrderByFlashSale(event);
    }

}
