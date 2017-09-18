package de.itboehmer.confluence.rest.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Supports making requests to a REST API.
 */
public interface RequestService {

	<T> T executeGetRequest(URI uri, Class<T> resultClass) throws IOException, RestException;

	InputStream executeGetRequestForDownload(URI uri) throws IOException, RestException;

	<T> T executePostRequest(URI uri, Object content, Class<T> resultClass) throws IOException, RestException;

	<T> T executePostRequestForUpload(URI uri, InputStream inputStream, String title, String comment,
			Class<T> resultClass) throws IOException, RestException;

}
