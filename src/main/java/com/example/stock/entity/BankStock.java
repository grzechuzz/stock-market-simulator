package com.example.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankStock {

    @Id
    private String name;

    @Column(nullable = false)
    private int quantity;

    public BankStock(String name, int quantity) {
        validateName(name);
        validateQuantity(quantity);

        this.name = name;
        this.quantity = quantity;
    }

    public void decreaseQuantity() {
        if (quantity == 0) {
            throw new IllegalStateException("Stock quantity cannot be negative");
        }
        quantity--;
    }

    public void increaseQuantity() {
        quantity++;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Stock name must not be blank");
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity must not be negative");
        }
    }
}
