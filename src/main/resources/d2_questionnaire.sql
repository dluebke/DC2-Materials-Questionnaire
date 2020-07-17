CREATE TABLE dc2Answers (
	QuestionnaireId INT(10) NOT NULL,
	ParticipantId BINARY(16) NOT NULL,
	QuestionId INT(10) NOT NULL,
	IntAnswer INT(10),
	StringAnswer VARCHAR(255) COLLATE utf8mb4_german2_ci,
	PRIMARY KEY (QuestionnaireId,ParticipantId, QuestionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci;

