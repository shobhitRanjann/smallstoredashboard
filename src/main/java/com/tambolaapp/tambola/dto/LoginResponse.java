package com.tambolaapp.tambola.dto;

public class LoginResponse {
    private String username;
    private String role;
    private String gameId;
    private String playerId;

    public LoginResponse(String username, String role, String gameId, String playerId) {
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

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
