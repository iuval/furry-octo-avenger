package com.prototipes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Cell extends GameObject {
	
	  public int[][] neigbours;
      public boolean Visible = true;
      public Vector2 Center;
      public Cell(TextureRegion reg){
  		super(reg);
      }

      public void Draw(SpriteBatch spriteBatch)
      {
          if (Visible)
          {
              super.Draw(spriteBatch);
          }
      }

      public boolean Equals(Object obj)
      {
          Cell o = (Cell)obj;
          return o.gridPos == gridPos;
      }
}
