package pruebas.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class UnitAnimPrefReader {

	/**
	 * Loads the specified pref file using {@link FileType#Internal}, using the
	 * parent directory of the pref file.
	 */
	public static UnitPrefReaderData load(String internalPrefFile) {
		return load(Gdx.files.internal(internalPrefFile));
	}

	public static UnitPrefReaderData load(FileHandle prefFile) {
		return new UnitPrefReaderData(prefFile);
	}

	public static class UnitPrefReaderData {
		static final String[] tuple = new String[2];
		public int cols;
		public int rows;
		public int handle_x;
		public int handle_y;
		public float[] image_times;
		public int image_count;

		public UnitPrefReaderData(FileHandle prefFile) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					prefFile.read()), 64);
			try {
				image_count = 0;
				while (true) {
					String line = reader.readLine();
					if (line == null)
						break;
					if (line.trim().length() == 0)
						break;
					if (image_count > 0) {
						image_times = new float[image_count];

						for (int i = 0; i < image_count; i++) {
							image_times[i] = Float
									.parseFloat(readValue(reader));
						}
					} else {
						readTuple(reader);
						rows = Integer.parseInt(tuple[0]);
						cols = Integer.parseInt(tuple[1]);

						readTuple(reader);
						handle_x = Integer.parseInt(tuple[0]);
						handle_y = Integer.parseInt(tuple[1]);

						float total_time = Float.parseFloat(readValue(reader));

						image_count = Integer.parseInt(readValue(reader));
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
		}

		/** Returns the number of tuple values read (2). */
		static void readTuple(BufferedReader reader) throws IOException {
			String line = reader.readLine();
			int colon = line.indexOf(':');
			if (colon == -1)
				throw new GdxRuntimeException("Invalid line: " + line);
			int i = 0, lastMatch = colon + 1;
			for (i = 0; i < 2; i++) {
				int comma = line.indexOf(',', lastMatch);
				if (comma == -1) {
					if (i == 0)
						throw new GdxRuntimeException("Invalid line: " + line);
					break;
				}
				tuple[i] = line.substring(lastMatch, comma).trim();
				lastMatch = comma + 1;
			}
			tuple[i] = line.substring(lastMatch).trim();
		}

		static String readValue(BufferedReader reader) throws IOException {
			String line = reader.readLine();
			int colon = line.indexOf(':');
			if (colon == -1)
				throw new GdxRuntimeException("Invalid line: " + line);
			return line.substring(colon + 1).trim();
		}
	}
}
