package com.example.MyBookShopApp.dto.google.api.books;

import lombok.Data;

@Data
public class ListPrice {
    private int amount;
    private String currencyCode;
    private long amountInMicros;

}
