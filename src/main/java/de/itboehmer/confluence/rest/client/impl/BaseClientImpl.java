/**
 * Copyright 2016 Micromata GmbH
 * Modifications Copyright 2017 Martin Böhmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.itboehmer.confluence.rest.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import org.apache.http.client.utils.URIBuilder;

import de.itboehmer.confluence.rest.core.RequestService;
import de.itboehmer.confluence.rest.core.RestException;
import de.itboehmer.confluence.rest.core.domain.content.AttachmentResultsBean;
import de.itboehmer.confluence.rest.core.impl.APIUriProvider;
import de.itboehmer.confluence.rest.core.util.URIHelper;

/**
 * @author Christian Schulze (c.schulze@micromata.de)
 * @author Martin Böhmer
 */
public abstract class BaseClientImpl {

	protected final ExecutorService executorService;

	private RequestService requestService;

	private APIUriProvider apiConfig;

	public BaseClientImpl(ExecutorService executorService, RequestService requestService, APIUriProvider apiConfig) {
		this.executorService = executorService;
		this.requestService = requestService;
		this.apiConfig = apiConfig;
	}

	protected URIBuilder buildPath(String... paths) {
		return URIHelper.buildPath(apiConfig.getRestApiBaseUri(), paths);
	}

	protected URIBuilder buildNonRestPath(String... paths) {
		return URIHelper.buildPath(apiConfig.getBaseUri(), paths);
	}

	protected <T> T executeGetRequest(URI uri, Class<T> resultClass) throws IOException, RestException {
		return requestService.executeGetRequest(uri, resultClass);
	}

	protected InputStream executeGetRequestForDownload(URI uri) throws IOException, RestException {
		return requestService.executeGetRequestForDownload(uri);
	}

	protected <T> T executePostRequest(URI uri, Object content, Class<T> resultClass)
			throws IOException, RestException {
		return requestService.executePostRequest(uri, content, resultClass);
	}

	protected AttachmentResultsBean executePostRequestForUpload(URI uri, InputStream inputStream, String title,
			String comment, Class<AttachmentResultsBean> resultClass) throws IOException, RestException {
		return requestService.executePostRequestForUpload(uri, inputStream, title, comment, resultClass);
	}
}
