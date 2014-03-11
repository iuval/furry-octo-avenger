package com.crystalclash.controllers.validators;

import java.util.regex.Pattern;

public class EmailValidator {
	public static boolean isValid(String email) {
		Pattern regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
		return regexPattern.matcher(email).matches();
	}
}
