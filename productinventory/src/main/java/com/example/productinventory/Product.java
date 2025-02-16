package com.example.productinventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class Product {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private BigDecimal price;

    @JsonProperty
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
