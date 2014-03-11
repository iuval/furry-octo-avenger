package com.crystalclash.controllers.validators;

import java.util.regex.Pattern;

public class PasswordValidator {
	public static boolean isValid(String pass) {
		Pattern regexPattern = Pattern.compile("(?=.*?\\d)(?=.*?[a-zA-Z]).{8,16}");
		return regexPattern.matcher(pass).matches();
	}
}
