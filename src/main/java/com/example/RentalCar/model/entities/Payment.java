package com.example.RentalCar.model.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentID;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId; // Transaction ID from Square

    @Column(name = "amount", nullable = false)
    private Integer amount; // Stored in cents for precision

    @Column(name = "currency", length = 3, nullable = false)
    private String currency; // Currency code (e.g., USD)

    @Column(name = "status", length = 20, nullable = false)
    private String status; // Status of the payment (e.g., COMPLETED, FAILED)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Timestamp when the payment was created

    @Column(name = "square_payment_id", unique = true, nullable = false)
    private String squarePaymentId; // Payment ID returned by Square

    public Payment() {
    }

    public Payment(Integer paymentID, User user, String transactionId, Integer amount, String currency, String status, LocalDateTime createdAt, String squarePaymentId) {
        this.paymentID = paymentID;
        this.user = user;
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
        this.squarePaymentId = squarePaymentId;
    }
    // Getters and Setters

    public Integer getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Integer paymentID) {
        this.paymentID = paymentID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSquarePaymentId() {
        return squarePaymentId;
    }

    public void setSquarePaymentId(String squarePaymentId) {
        this.squarePaymentId = squarePaymentId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID=" + paymentID +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", squarePaymentId='" + squarePaymentId + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}