package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SocketConfig {
	private final ElGamal elGamal;

	public SocketConfig() {
		this.elGamal = new ElGamal();
		this.elGamal.generateKeys(512); // Générer les clés avec une taille de 512 bits
	}

	// Fonction pour démarrer le client
	public void startCommunicationForClient() {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Entrez l'adresse IP du serveur:");
			String host = scanner.nextLine();
			System.out.println("Entrez le port du serveur:");
			int port = Integer.parseInt(scanner.nextLine());

			Socket socket = new Socket(host, port);
			Thread.sleep(3000);
			System.out.println("Connexion réussie");

			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

			// Recevoir la clé publique du serveur
			String serverPublicKey = input.readLine();
			elGamal.importPublicKey(serverPublicKey);

			// Envoi d'un message chiffré au serveur
			System.out.println("Entrez votre message (ou 'quit' pour quitter):");
			String message = scanner.nextLine();

			BigInteger[][] encryptedMessage = elGamal.encrypt(message);
			sendEncryptedMessage(output, encryptedMessage);
			System.out.println("Message chiffré envoyé au serveur.");

			// Envoyer la clé publique du client au serveur
			output.println(elGamal.exportPublicKey());
			System.out.println("Clé publique du client envoyée au serveur.");

			Thread.sleep(1000);
			System.out.println("En attente de la confirmation du serveur...");

			BigInteger[][] serverResponse = receiveEncryptedMessage(input);
			String decryptedResponse = elGamal.decrypt(serverResponse);
			System.out.println("Réponse déchiffrée du serveur : " + decryptedResponse);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Fonction pour démarrer le serveur
	public void startCommunicationForServer() {
		int port = 1234;
		try (ServerSocket serverSocket = new ServerSocket(port);
		     Scanner scanner = new Scanner(System.in)) {

			System.out.printf("Serveur démarré sur le port %d et en attente de connexion...\n", port);
			Socket clientSocket = serverSocket.accept();
			Thread.sleep(2000);
			System.out.println("Connexion établie avec le client.");

//			Configuration du canal de communication
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

			// Envoyer la clé publique au client
			System.out.println("Exportation de la clé publique du serveur...");
			output.println(elGamal.exportPublicKey());
			System.out.println("Clé publique du serveur exportée.");

			System.out.println("En attente du message client...");
			BigInteger[][] clientMessage = receiveEncryptedMessage(input);
			String decryptedMessage = elGamal.decrypt(clientMessage);
			System.out.println("Message déchiffré du client : " + decryptedMessage);

			// Recevoir la clé publique du client
			String clientPublicKey = input.readLine();
			elGamal.importPublicKey(clientPublicKey);

			System.out.println("Entrez une réponse pour le client : ");
			String response = scanner.nextLine();
			BigInteger[][] encryptedResponse = elGamal.encrypt(response);
			sendEncryptedMessage(output, encryptedResponse);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendEncryptedMessage(PrintWriter output, BigInteger[][] encryptedMessage) {
		for (int i = 0; i < encryptedMessage[0].length; i++) {
			output.println(encryptedMessage[0][i]);
			output.println(encryptedMessage[1][i]);
		}
		output.println("END");
	}

	private BigInteger[][] receiveEncryptedMessage(BufferedReader input) throws IOException {
		BigInteger[] aValues = new BigInteger[100];
		BigInteger[] bValues = new BigInteger[100];
		int index = 0;

		while (true) {
			String line = input.readLine();
			if (line.equals("END")) break;
			aValues[index] = new BigInteger(line);
			bValues[index] = new BigInteger(input.readLine());
			index++;
		}

		BigInteger[][] encryptedMessage = new BigInteger[2][index];
		System.arraycopy(aValues, 0, encryptedMessage[0], 0, index);
		System.arraycopy(bValues, 0, encryptedMessage[1], 0, index);
		return encryptedMessage;
	}
}