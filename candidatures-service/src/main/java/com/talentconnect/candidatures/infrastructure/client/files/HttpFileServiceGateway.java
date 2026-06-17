package com.talentconnect.candidatures.infrastructure.client.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.talentconnect.candidatures.application.FileServiceGateway;
import com.talentconnect.candidatures.exception.FileServiceIntegrationException;
import com.talentconnect.candidatures.exception.InvalidFileException;
import com.talentconnect.candidatures.exception.ResourceNotFoundException;

@Component
public class HttpFileServiceGateway implements FileServiceGateway {

	private final RestClient restClient;

	public HttpFileServiceGateway(
			RestClient.Builder restClientBuilder,
			@Value("${app.clients.file-service.base-url:http://localhost:8082}") String fileServiceBaseUrl) {
		this.restClient = restClientBuilder
				.baseUrl(fileServiceBaseUrl)
				.build();
	}

	@Override
	public void assertFileExists(String fileId, Long requesterUserId, String requesterRole) {
		if (fileId == null || fileId.isBlank()) {
			throw new InvalidFileException("L'identifiant du fichier CV est obligatoire");
		}

		try {
			restClient.get()
					.uri("/api/files/{fileId}/metadata", fileId)
					.accept(MediaType.APPLICATION_JSON)
					.header("X-User-Id", String.valueOf(requesterUserId))
					.header("X-Role", toRoleHeader(requesterRole))
					.retrieve()
					.toBodilessEntity();
		} catch (HttpClientErrorException.NotFound exception) {
			throw new ResourceNotFoundException("Fichier introuvable: " + fileId);
		} catch (HttpClientErrorException exception) {
			throw new FileServiceIntegrationException("file-service a rejete la requete de validation du fichier", exception);
		} catch (HttpServerErrorException | ResourceAccessException exception) {
			throw new FileServiceIntegrationException("file-service indisponible ou en erreur", exception);
		}
	}

	private String toRoleHeader(String role) {
		if (role == null || role.isBlank()) {
			return "";
		}
		return role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role;
	}
}


