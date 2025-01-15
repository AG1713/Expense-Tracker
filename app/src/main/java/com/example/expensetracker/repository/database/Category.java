package com.example.expensetracker.repository.database;

import androidx.annotation.NonNull;

public class Category {
    private long id;

    @NonNull
    private String name;

    private Long parent_id;

    public Category(@NonNull String name, Long parent_id) {
        this.name = name;
        this.parent_id = parent_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }
}
