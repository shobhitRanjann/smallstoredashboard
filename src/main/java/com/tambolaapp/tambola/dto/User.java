package com.tambolaapp.tambola.dto;

public class User {
    private String username;
    private String role;
    private String gameId;
    private String playerId;

    public User(String username, String role, String gameId, String playerId) {
        this.username = username;
        this.role = role;
        this.gameId = gameId;
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
