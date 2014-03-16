package com.crystalclash.renders.particle_system;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class ParticleSystem {
	private Array<Particle> particles;

	public ParticleSystem() {
		particles = new Array<Particle>();
	}

	public void addParticle(Particle p) {
		particles.add(p);
	}

	public void update(float dt, SpriteBatch batch) {
		Particle p;
		for (int i = particles.size - 1; i >= 0; i--) {
			p = particles.get(i);
			p.updateParticle(dt);
			p.draw(batch);
			if (p.timeToLive <= 0)
				particles.removeIndex(i);
		}
	}
}
