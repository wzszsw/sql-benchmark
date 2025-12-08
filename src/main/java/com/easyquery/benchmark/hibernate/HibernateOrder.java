package com.easyquery.benchmark.hibernate;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Hibernate entity for Order table
 */
@Entity
@Table(name = "t_order")
public class HibernateOrder {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "order_no")
    private String orderNo;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "remark")
    private String remark;

    public HibernateOrder() {
    }

    public HibernateOrder(String id, String userId, String orderNo, BigDecimal amount, Integer status, String remark) {
        this.id = id;
        this.userId = userId;
        this.orderNo = orderNo;
        this.amount = amount;
        this.status = status;
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}



