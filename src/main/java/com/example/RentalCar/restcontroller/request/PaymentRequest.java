package com.example.RentalCar.restcontroller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class PaymentRequest {
    private String currency;
    private String sourceId; // from frontend (card nonce)
    private String idempotencyKey;
    private Long userId;
    private Integer reservationID;



    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public Integer getReservationID() {
        return reservationID;
    }

    public void setReservationID(Integer reservationID) {
        this.reservationID = reservationID;
    }
}
