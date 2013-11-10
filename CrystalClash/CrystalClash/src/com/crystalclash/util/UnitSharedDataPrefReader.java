package com.crystalclash.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class UnitSharedDataPrefReader {
	public static String[] load(String internalPrefFile) {
		return load(Gdx.files.internal(internalPrefFile));
	}

	private static String[] load(FileHandle prefFile) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				prefFile.read()), 64);
		String[] values = new String[5];
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.trim().length() == 0)
					break;
				else {
					values[0] = readValue(reader);
					values[1] = readValue(reader);
					values[2] = readValue(reader);
					values[3] = readValue(reader);
					values[4] = readValue(reader);
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
