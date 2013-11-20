package com.crystalclash.renders;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.AudioManager.SOUND;
import com.crystalclash.entities.Unit;
import com.crystalclash.renders.helpers.UnitHelper;
import com.crystalclash.util.SuperAnimation;

public class UnitRender {
	public enum FACING {
		right, left
	}

	public enum STATE {
		idle, fighting, walking, dieing, dead, ghost
	}

	private Unit unit;
	private STATE state;

	public SuperAnimation idleAnim;
	public SuperAnimation fightAnim;
	public SuperAnimation walkAnim;
	public SuperAnimation dieAnim;
	public SuperAnimation shieldAnim;
	private SuperAnimation currnetAnim;
	private Texture hpBar;
	private float hpWidth;
	private FACING facing = FACING.right;
	
	private Sound playing;

	public UnitRender() {
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit u) {
		unit = u;
		if (unit.isEnemy()) {
			hpBar = UnitHelper.getEnemyHPBar();
		} else {
			hpBar = UnitHelper.getAllyHPBar();
		}
		updateHp();
	}

	public void setFacing(FACING at) {
		facing = at;
	}

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
		switch (state) {
		case idle: {
			currnetAnim = idleAnim;
		}
			break;
		case fighting: {
			currnetAnim = fightAnim;
			AudioManager.playUnitSFX(unit.getName(), SOUND.attack);
		}
			break;
		case walking: {
			currnetAnim = walkAnim;
		}
			break;
		case dieing: {
			currnetAnim = dieAnim;
		}
			break;
		case dead: {
			currnetAnim = null;
		}
			break;
		case ghost: {
			currnetAnim = walkAnim;

		}
			break;
		}
	}

	public void updateHp() {
		if (unit.isAlive() && unit.getTotalHP() != 0) {
			hpWidth = (unit.getHP() * UnitHelper.HP_BAR_WIDTH) / unit.getTotalHP();
		} else {
			hpWidth = 0;
		}
	}

	public void draw(SpriteBatch batch, float dt) {
		if (state != STATE.dead) {
			currnetAnim.update(dt, facing);

			if (state == STATE.ghost) {
				Color c = batch.getColor();
				batch.setColor(c.r, c.g, c.b, 0.4f);
				currnetAnim.draw(batch, unit.getX(), unit.getY());
				batch.setColor(c.r, c.g, c.b, 1f);
			} else {
				if(currnetAnim == fightAnim){
					currnetAnim.draw(batch, unit.getX(), unit.getY());
				}
				currnetAnim.draw(batch, unit.getX(), unit.getY());
				if (state != STATE.dieing) {
					batch.draw(UnitHelper.backHPBar,
							unit.getX() + UnitHelper.HP_BAR_BACK_X,
							unit.getY() + UnitHelper.HP_BAR_BACK_Y,
							UnitHelper.HP_BAR_BACK_WIDTH,
							UnitHelper.HP_BAR_BACK_HEIGHT);
					batch.draw(hpBar,
							unit.getX() + UnitHelper.HP_BAR_X,
							unit.getY() + UnitHelper.HP_BAR_Y,
							hpWidth,
							UnitHelper.HP_BAR_HEIGHT);

					if (unit.isInDefensePosition()) {
						shieldAnim.update(dt, facing);
						shieldAnim.draw(batch, unit.getX(), unit.getY());
					}
				}
				if (state == STATE.dieing && currnetAnim.isAnimationFinished()) {
					setState(STATE.dead);
				}
			}
		}
	}

	@Override
	public UnitRender clone() {
		UnitRender ren = new UnitRender();
		ren.idleAnim = idleAnim.clone();
		ren.fightAnim = fightAnim.clone();
		ren.walkAnim = walkAnim.clone();
		ren.dieAnim = dieAnim.clone();
		ren.setState(STATE.idle);
		return ren;
	}
	
	public void playSFX(SOUND sound){
		if(playing != null){
			playing.stop();
		}
		
		playing = AudioManager.playUnitSFX(unit.getName(), sound);
	}
}
