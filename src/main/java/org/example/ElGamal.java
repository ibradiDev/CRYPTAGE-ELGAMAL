package org.example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class ElGamal {
	private final SecureRandom random = new SecureRandom();
	private BigInteger p, g, y, x;

	// Générer les clés publiques et privées
	public void generateKeys(int bitLength) {
		p = BigInteger.probablePrime(bitLength, random);
		g = new BigInteger(bitLength - 1, random);
		x = new BigInteger(bitLength - 2, random);
		y = g.modPow(x, p);
	}

	// Chiffrement d'un message texte
	public BigInteger[][] encrypt(String message) {
		byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
		BigInteger[] encryptedMessage = new BigInteger[messageBytes.length];
		BigInteger[] aValues = new BigInteger[messageBytes.length];

		for (int i = 0; i < messageBytes.length; i++) {
			BigInteger m = BigInteger.valueOf(messageBytes[i]);
			BigInteger k = new BigInteger(p.bitLength() - 1, random);
			BigInteger a = g.modPow(k, p);
			BigInteger b = m.multiply(y.modPow(k, p)).mod(p);

			aValues[i] = a;
			encryptedMessage[i] = b;
		}
		return new BigInteger[][]{aValues, encryptedMessage};
	}

	// Déchiffrement d'un message chiffré
	public String decrypt(BigInteger[][] cipherText) {
		BigInteger[] aValues = cipherText[0];
		BigInteger[] encryptedMessage = cipherText[1];
		byte[] decryptedBytes = new byte[encryptedMessage.length];

		for (int i = 0; i < encryptedMessage.length; i++) {
			BigInteger a = aValues[i];
			BigInteger b = encryptedMessage[i];
			BigInteger s = a.modPow(x, p);
			BigInteger sInverse = s.modInverse(p);
			BigInteger m = b.multiply(sInverse).mod(p);

			decryptedBytes[i] = m.byteValue();
		}
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}

	// Méthodes pour exporter les clés publiques
	public String exportPublicKey() {
		return p.toString() + "," + g.toString() + "," + y.toString();
	}

	// Méthode pour importer les clés publiques
	public void importPublicKey(String publicKeyStr) {
		String[] parts = publicKeyStr.split(",");
		this.p = new BigInteger(parts[0]);
		this.g = new BigInteger(parts[1]);
		this.y = new BigInteger(parts[2]);
	}
}