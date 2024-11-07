package service.requestresult;

import model.GameData;
import model.SimpleGameData;

import java.util.Collection;
import java.util.HashMap;

public record ListGamesResult(Collection<SimpleGameData> games, String message) {
}
