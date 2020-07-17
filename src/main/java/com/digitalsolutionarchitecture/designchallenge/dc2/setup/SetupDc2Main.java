package com.digitalsolutionarchitecture.designchallenge.dc2.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Random;
import java.util.UUID;

public class SetupDc2Main {

	public static void main(String[] args) {

		String mysqlHost = fetchConfig(args, 0, "MySQL Host Name", false);
		String mysqlPort = fetchConfig(args, 1, "MySQL Port", false);
		String mysqlDatabase = fetchConfig(args, 2, "MySQL Database", false);
		String mysqlUser = fetchConfig(args, 3, "MySQL User", false);
		String mysqlPassword = fetchConfig(args, 4, "MySQL User Password", true);

		int surveyParticipants = 1000000;
		int questionnaireId = 1000;
		int numberOfLickertQuestionsPerQuestionnaire = 10;
		int numberOfTextQuestionsPerQuestionnaire = 2;

		System.out.println("Configuration");
		System.out.println("=============");
		System.out.println("MySQL Host:          " + mysqlHost);
		System.out.println("MySQL Database:      " + mysqlDatabase);
		System.out.println("MySQL Port:          " + mysqlPort);
		System.out.println("MySQL User:          " + mysqlUser);
		System.out.println("MySQL User Password: " + "***");
		System.out.println("#Participants:       " + surveyParticipants);
		System.out.println();

		System.out.print("Loading MySQL Driver... ");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("done");
		} catch (ClassNotFoundException e) {
			System.out.println("FAILED");
			e.printStackTrace();
			return;
		}

		System.out.print("Connecting to data base... ");
		try (Connection c = DriverManager.getConnection("jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/"
				+ mysqlDatabase
				+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				mysqlUser, mysqlPassword);
				PreparedStatement s = c.prepareStatement(
						"INSERT INTO dc2Answers (QuestionnaireId, ParticipantId, QuestionId, IntAnswer, StringAnswer) VALUES (?, ?, ?, ?, ?)");
			) {

			System.out.println("done");
			Random random = new Random(1);

			System.out.println("Creating fake survey results...");
			c.setAutoCommit(false);
			s.setInt(1, questionnaireId);
			long start = System.currentTimeMillis();
			for (int a = 0; a < surveyParticipants; a++) {
				byte[] participantId = UUIDUtils.getBytesFromUUID(UUID.randomUUID());
				s.setBytes(2, participantId);
				s.setString(5, null);
				for(int questionId = 0; questionId < numberOfLickertQuestionsPerQuestionnaire ; questionId++) {
					int answer = random.nextInt(6);
					s.setInt(3, questionId);
					s.setInt(4, answer);
					
					s.execute();
				}
				s.setNull(4, Types.INTEGER);
				for(int questionId = 0; questionId < numberOfTextQuestionsPerQuestionnaire ; questionId++) {
					s.setInt(3, questionId + numberOfLickertQuestionsPerQuestionnaire);
					s.setString(5, "Eine Textantwort");
				}
				
				c.commit();
				if(a % 200 == 0) {
					c.commit();
					System.out.println(
							(a + 1) + " survey answers written in " + ((System.currentTimeMillis() - start) / 1000) + "s");
				}
			}
			c.commit();
			System.out.println("Done creating fake survey results...");

		} catch (SQLException e) {
			System.out.println("FAILED");
			e.printStackTrace();
		}
	}

	private static String fetchConfig(String[] args, int index, String prompt, boolean isPassword) {
		if (args.length > index) {
			return args[index];
		} else {
			if (!isPassword) {
				return System.console().readLine(prompt + ": ");
			} else {
				return new String(System.console().readPassword(prompt + ": "));
			}
		}
	}

}
