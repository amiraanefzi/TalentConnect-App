package com.talentconnect.candidatures.application;

public interface FileServiceGateway {

	void assertFileExists(String fileId, Long requesterUserId, String requesterRole);
}

