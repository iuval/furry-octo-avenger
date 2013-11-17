package com.crystalclash.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

<<<<<<< HEAD:CrystalClash/CrystalClash/src/com/crystalclash/util/UnitSharedDataPrefReader.java
public class UnitSharedDataPrefReader {
	public static String[] load(String internalPrefFile) {
=======
public class SharedDataPrefReader {
	public static int[] load(String internalPrefFile) {
>>>>>>> 9e4b547... Agrega emblemas(con algunos de ejemplo) y Profile view donde se puede cambiar el emblema, y despues se podra cambiar el nombre y la pass ahi:CrystalClash/CrystalClash/src/com/crystalclash/util/SharedDataPrefReader.java
		return load(Gdx.files.internal(internalPrefFile));
	}

	private static String[] load(FileHandle prefFile) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				prefFile.read()), 64);
<<<<<<< HEAD:CrystalClash/CrystalClash/src/com/crystalclash/util/UnitSharedDataPrefReader.java
		String[] values = new String[5];
=======
		int[] values = new int[5];
>>>>>>> 9e4b547... Agrega emblemas(con algunos de ejemplo) y Profile view donde se puede cambiar el emblema, y despues se podra cambiar el nombre y la pass ahi:CrystalClash/CrystalClash/src/com/crystalclash/util/SharedDataPrefReader.java
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.trim().length() == 0)
					break;
				else {
<<<<<<< HEAD:CrystalClash/CrystalClash/src/com/crystalclash/util/UnitSharedDataPrefReader.java
					values[0] = readValue(reader);
					values[1] = readValue(reader);
					values[2] = readValue(reader);
					values[3] = readValue(reader);
					values[4] = readValue(reader);
=======
					values[0] = Integer.parseInt(readValue(reader));
					values[1] = Integer.parseInt(readValue(reader));
					values[2] = Integer.parseInt(readValue(reader));
					values[3] = Integer.parseInt(readValue(reader));
					values[4] = Integer.parseInt(readValue(reader));
>>>>>>> 9e4b547... Agrega emblemas(con algunos de ejemplo) y Profile view donde se puede cambiar el emblema, y despues se podra cambiar el nombre y la pass ahi:CrystalClash/CrystalClash/src/com/crystalclash/util/SharedDataPrefReader.java
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
