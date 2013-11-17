package com.crystalclash.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class UnitStatsPrefReader {

	public static Hashtable<String, int[]> load(String internalPrefFile) {
		return load(Gdx.files.internal(internalPrefFile));
	}

	private static Hashtable<String, int[]> load(FileHandle prefFile) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				prefFile.read()), 64);
		Hashtable<String, int[]> unitValues = new Hashtable<String, int[]>();
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.trim().length() == 0)
					break;
				else {
					int[] data = new int[6];
					data[0] = Integer.parseInt(readValue(reader));
					data[1] = Integer.parseInt(readValue(reader));
					data[2] = Integer.parseInt(readValue(reader));
					data[3] = Integer.parseInt(readValue(reader));
					data[4] = Integer.parseInt(readValue(reader));
					data[5] = Integer.parseInt(readValue(reader));

					unitValues.put(line, data);
				}
			}
		} catch (Exception ex) {
			throw new GdxRuntimeException("Error reading pref file: " + prefFile, ex);
		} finally {
			try {
				reader.close();
			} catch (IOException ignored) {
			}
		}
		return unitValues;
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
