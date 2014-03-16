package pruebas.CrystalClash;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.crystalclash.CrystalClash;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = CrystalClash.TITLE;
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 530;
		cfg.resizable = false;
		
		new LwjglApplication(new CrystalClash(), cfg);
	}
}
