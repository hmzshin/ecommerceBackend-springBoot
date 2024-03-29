package com.workintech.ecommerce.util;

import java.time.LocalDateTime;

import com.workintech.ecommerce.dto.*;
import org.springframework.stereotype.Component;

import com.workintech.ecommerce.entity.Address;
import com.workintech.ecommerce.entity.Order;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.service.AddressService;
import com.workintech.ecommerce.service.ProductService;
import com.workintech.ecommerce.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class OrderFactory {

    private ProductService productService;
    private AddressService addressService;
    private UserService userService;
    private ProductFactory productFactory;
    private AddressFactory addressFactory;

    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setStatus(orderRequest.getStatus());
        order.setOrderDate(LocalDateTime.now().toString());
        order.setPaymentInformation(orderRequest.getPaymentInformation());

        for (ProductInOrder p : orderRequest.getProducts()) {
            Product product = productService.findById(p.getProductId());
            for (int i = 0; i < p.getNumberOfProduct(); i++) {
                order.addProduct(product);
            }
        }
        User u = userService.findByIdUser(orderRequest.getUserId());

        Address sA = addressService.findById(orderRequest.getShippingAddressId());

        Address bA = addressService.findById(orderRequest.getBillingAddressId());

        order.setUser(u);
        order.setBillingAddress(bA);
        order.setShippingAddress(sA);

        return order;
    }

    public OrderResponse createOrderResponse(Order order) {
        OrderResponse oR = new OrderResponse();
        oR.setStatus("ORDER_MADE");
        AddressDto billingDto = addressFactory.createAddressDto(order.getBillingAddress());
        oR.setBillingAddress(billingDto);
        AddressDto shippingDto = addressFactory.createAddressDto(order.getShippingAddress());
        oR.setShippingAddress(shippingDto);
        oR.setPaymentInformation(order.getPaymentInformation());

        for (Product p : order.getProducts()) {
            ProductResponse pR = productFactory.createProductResponse(p, p.getImages());
            oR.addProductResponse(pR);
        }
        return oR;
    }

}
