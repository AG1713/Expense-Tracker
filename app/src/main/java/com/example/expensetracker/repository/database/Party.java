package com.example.expensetracker.repository.database;

public class Party {
    private long id;
    private String name;
    private String nickname;

    public Party(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
