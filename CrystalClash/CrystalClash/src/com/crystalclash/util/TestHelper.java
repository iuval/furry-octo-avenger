package com.crystalclash.util;

public class TestHelper {

	public static String player1PlaceFT = "\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 2.0,\\\"y\\\": 1.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"earth_tank\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 0.0,\\\"y\\\": 2.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"darkness_mage\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 1.0,\\\"y\\\": 2.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"wind_slayer\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 2.0,\\\"y\\\": 3.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"fire_archer\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 1.0,\\\"y\\\": 4.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"fire_archer\\\"}";
	public static String player2PlaceFT = "\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 8.0,\\\"y\\\": 0.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"darkness_mage\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 6.0,\\\"y\\\": 1.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"fire_archer\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 7.0,\\\"y\\\": 2.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"earth_tank\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 6.0,\\\"y\\\": 3.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"fire_archer\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 7.0,\\\"y\\\": 3.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"wind_slayer\\\"},\\\"action\\\": {\\\"cell\\\": {\\\"x\\\": 8.0,\\\"y\\\": 4.0}, \\\"action\\\": \\\"place\\\", \\\"unit_name\\\": \\\"darkness_mage\\\"}";
	public static String game_id = "\"game_id\":\"52067fc1341d36eb68000002\"";
	public static String player_ = "\"player\":";

	public static String value_ok = "\"value\":\"ok\"";

	public static String getSecondTurnJson(int player) {
		return "{" + value_ok + ",  \"data\": \"{ " + game_id + ", " + player_
				+ player + ", \"data1\": {" + player1PlaceFT
				+ "}, \"data2\": {" + player2PlaceFT + "}}\"}";
	}

	public static String getFirstTurnJson(int player) {
		return "{" + value_ok + ", \"data\": { " + game_id + ", " + player_
				+ player + ",\"data\":\"none\"}}";
	}
}
