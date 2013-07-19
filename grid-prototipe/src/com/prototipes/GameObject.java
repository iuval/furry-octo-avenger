package com.prototipes;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameObject extends Sprite {
	public GridPos gridPos;

	public GameObject(TextureRegion reg){
		super(reg);
	}
	
    public void Draw(SpriteBatch spriteBatch)
    {
        spriteBatch.draw(this, getX(), getY());
    }

    public void dispose(){
    	this.getTexture().dispose();
    }
}
