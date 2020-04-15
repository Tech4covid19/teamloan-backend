package pt.teamloan.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

public class SecureKeyGenerator {
	private static SecureRandom random = new SecureRandom();

	private SecureKeyGenerator() {
	}

	public static String generateSecureKey() {
		return new BigInteger(130, random).toString(32).toUpperCase();
	}

	public static String generateUniqueSecureKey() {
		return RandomStringUtils.randomAlphanumeric(10) + "-" + UUID.randomUUID().toString();
	}
}
