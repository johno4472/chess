package service.requestresult;

import model.GameData;

import java.util.HashMap;

public record ListGamesResult(HashMap<Integer, GameData> gamesList) {
}
