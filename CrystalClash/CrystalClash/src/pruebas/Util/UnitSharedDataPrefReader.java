package pruebas.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class UnitSharedDataPrefReader {
	public static int[] load(String internalPrefFile) {
		return load(Gdx.files.internal(internalPrefFile));
	}

	private static int[] load(FileHandle prefFile) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				prefFile.read()), 64);
		int[] values = new int[3];
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.trim().length() == 0)
					break;
				else {
					values[0] = Integer.parseInt(readValue(reader));
					values[1] = Integer.parseInt(readValue(reader));
					values[2] = Integer.parseInt(readValue(reader));
				}
			}
		} catch (Exception ex) {
			throw new GdxRuntimeException("Error reading pref file: "
					+ prefFile, ex);
		} finally {
			try {
				reader.close();
			} catch (IOException ignored) {
			}
		}
		return values;
	}

	/** Returns the number of tuple values read (2). */
	private static String readValue(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		int colon = line.indexOf(':');
		if (colon == -1)
			throw new GdxRuntimeException("Invalid line: " + line);
		return line.substring(colon + 1).trim();
	}
}
