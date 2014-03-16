package com.crystalclash.renders.particle_system.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.particle_system.Particle;

public class JumpingNumber extends Particle {
	private BitmapFont font;
	private String text;
	private float velX;
	private float velY;

	public JumpingNumber(String txt, float x, float y, float vx, float vy, float ttl) {
		super(x, y, ttl);
		text = txt;
		font = ResourceHelper.getDamageFont();
		velX = vx;
		velY = vy;
	}

	@Override
	public void update(float dt) {
		x += velX;
		y += velY;
		velY -= 0.2;
	}

	@Override
	public void draw(SpriteBatch batch) {
		if (timeToLive < 0.3f) {
			Color c = font.getColor();
			font.setColor(c.r, c.g, c.b, timeToLive / 0.3f);
			font.draw(batch, text, x, y);
			font.setColor(c);
		} else {
			font.draw(batch, text, x, y);
		}
	}
}
