package com.example.mybookshopapp.dto.google.api.books;

import lombok.Data;

@Data
public class RetailPrice {

    private int amount;
    private String currencyCode;
    private long amountInMicros;


}
