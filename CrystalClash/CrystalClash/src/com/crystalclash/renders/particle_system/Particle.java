package com.crystalclash.renders.particle_system;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Particle {
	protected float timeToLive;
	protected float x;
	protected float y;

	public Particle(float x, float y, float ttl) {
		this.x = x;
		this.y = y;
		this.timeToLive = ttl;
	}

	public float getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(float timeToLive) {
		this.timeToLive = timeToLive;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void updateParticle(float dt) {
		update(dt);
		timeToLive -= dt;
	}

	public abstract void update(float dt);

	public abstract void draw(SpriteBatch batch);

}
