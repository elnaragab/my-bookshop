package com.example.mybookshopapp.dto.payment.yookassa.request;

import com.example.mybookshopapp.dto.payment.yookassa.Amount;
import lombok.Data;

@Data
public class RootRequest {
    private Amount amount;
    private boolean capture;
    private Confirmation confirmation;
    private String description;
}
