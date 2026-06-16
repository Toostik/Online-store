package com.example.orderservice.entity.order.mapper;

import com.example.orderservice.dto.order.OrderDto;
import com.example.orderservice.entity.order.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto toDto(Order product);

    List<OrderDto> toDtoList(List<Order> orders);

}
