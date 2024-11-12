package org.example;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		SocketConfig socketConfig = new SocketConfig();
		Scanner scanner = new Scanner(System.in);

		System.out.println("Voulez-vous démarrer le serveur ou le client ? (s/c)");
		String choice = scanner.nextLine();

		if ("s".equalsIgnoreCase(choice)) {
			socketConfig.startCommunicationForServer();
		} else if ("c".equalsIgnoreCase(choice)) {
			socketConfig.startCommunicationForClient();
		} else {
			System.out.println("Choix invalide");
		}

		scanner.close();
	}
}