package com.example.expensetracker.repository.displayEntities;

public class Filter {
    private Long party_id;
    private Long account_id;
    private Long category_id;
    private String start_date;
    private String end_date;
    private String start_time;
    private String end_time;

    public Filter() {
        party_id = null;
        account_id = null;
        category_id = null;
        start_date = null;
        end_date = null;
        start_time = null;
        end_time = null;
    }

    public Filter(Long party_id, Long account_id, Long category_id, String start_date, String end_date, String start_time, String end_time) {
        this.party_id = party_id;
        this.account_id = account_id;
        this.category_id = category_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public Long getParty_id() {
        return party_id;
    }

    public void setParty_id(Long party_id) {
        this.party_id = party_id;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
