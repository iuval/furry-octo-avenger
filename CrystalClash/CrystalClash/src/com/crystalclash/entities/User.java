package com.crystalclash.entities;

import com.crystalclash.networking.ServerDriver;

public class User {

	private String id;
	private String email;
	private String name;
	private int emblem = 0;
	private int victoryCount = 0;
	private int drawCount = 0;
	private int lostCount = 0;

	public User(String id, String email, String name, int emblem, int v, int d, int l) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.emblem = emblem;
		victoryCount = v;
		drawCount = d;
		lostCount = l;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public int getEmblem() {
		return emblem;
	}

	public void setEmblem(int emblem) {
		this.emblem = emblem;
	}

	public int getVictoryCount() {
		return victoryCount;
	}

	public int getDrawCount() {
		return drawCount;
	}

	public int getLostCount() {
		return lostCount;
	}

	public void update() {
		ServerDriver.sendUpdateUser(name, email, emblem);
	}

}
