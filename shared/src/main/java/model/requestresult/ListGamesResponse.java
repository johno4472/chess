package model.requestresult;

import model.SimpleGameData;

import java.util.Collection;

public record ListGamesResponse(Collection<SimpleGameData> games, String message) {
}
