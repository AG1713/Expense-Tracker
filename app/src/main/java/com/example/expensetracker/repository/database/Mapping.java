package com.example.expensetracker.repository.database;

public class Mapping {
    private long id;
    private Long party_id;
    private Double amount;
    private long category_id;

    public Mapping(long id, Long party_id, Double amount, long category_id) {
        this.id = id;
        this.party_id = party_id;
        this.amount = amount;
        this.category_id = category_id;
    }

    public Mapping(Long party_id, Double amount, long category_id) {
        this.party_id = party_id;
        this.amount = amount;
        this.category_id = category_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParty_id() {
        return party_id;
    }

    public void setParty_id(Long party_id) {
        this.party_id = party_id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }
}
