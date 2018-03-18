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

import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.EXPAND;
import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.LIMIT;
import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.POSTING_DAY;
import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.SPACEKEY;
import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.START;
import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.STATUS;
import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.TITLE;
import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.TYPE;
import static de.itboehmer.confluence.rest.core.misc.RestParamConstants.VERSION;
import static de.itboehmer.confluence.rest.core.misc.RestPathConstants.CONTENT;
import static de.itboehmer.confluence.rest.core.misc.RestPathConstants.CONTENT_ATTACHMENT;
import static de.itboehmer.confluence.rest.core.misc.RestPathConstants.CONTENT_LABEL;
import static de.itboehmer.confluence.rest.core.misc.RestPathConstants.SPECIFIC_CONTENT;

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.client.ContentClient;
import de.itboehmer.confluence.rest.core.RequestService;
import de.itboehmer.confluence.rest.core.domain.content.AttachmentBean;
import de.itboehmer.confluence.rest.core.domain.content.AttachmentResultsBean;
import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.ContentResultsBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelsBean;
import de.itboehmer.confluence.rest.core.impl.APIUriProvider;
import de.itboehmer.confluence.rest.core.misc.ContentStatus;
import de.itboehmer.confluence.rest.core.misc.ContentType;
import de.itboehmer.confluence.rest.core.misc.UnexpectedContentException;

/**
 * @author Christian Schulze (c.schulze@micromata.de)
 * @author Martin Böhmer
 */
public class ContentClientImpl extends BaseClientImpl implements ContentClient {

    private final Logger log = LoggerFactory.getLogger(ContentClientImpl.class);

    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ContentClientImpl(ExecutorService executorService, RequestService requestService, APIUriProvider apiConfig) {
        super(executorService, requestService, apiConfig);
    }

    @Override
    public Future<ContentBean> getContentById(String id, int version, List<String> expand) {
        if (log.isInfoEnabled()) {
            String message = "Getting content by ID. ID=%1$s, version=%2$s, expand=%3$s";
            log.info(String.format(message, id, version, expand));
        }
        return executorService.submit(() -> {
            // URI with parameters
            URIBuilder uriBuilder = buildPath(CONTENT, id);
            if (version > 0) {
                uriBuilder.addParameter(VERSION, String.valueOf(version));
            }
            if (CollectionUtils.isNotEmpty(expand) == true) {
                String join = StringUtils.join(expand, ",");
                uriBuilder.addParameter(EXPAND, join);
            }
            return executeGetRequest(uriBuilder.build(), ContentBean.class);
        });
    }

    @Override
    public Future<ContentResultsBean> getContent(ContentType type, String spacekey, String title, ContentStatus status, Date postingDay, List<String> expand, int start, int limit) {
        if (log.isInfoEnabled()) {
            String message = "Getting content. Type=%1$s, space=%2$s, title=%3$s, status=%4$s, postingDay=%5$s, expand=%6$s, start=%7$s, limit=%8$s";
            log.info(String.format(message, type, spacekey, title, status, postingDay, expand, start, limit));
        }
        // URI with parameters
        URIBuilder uriBuilder = buildPath(CONTENT);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (type != null) {
            nameValuePairs.add(new BasicNameValuePair(TYPE, type.getName()));
        }
        if (StringUtils.trimToNull(spacekey) != null) {
            nameValuePairs.add(new BasicNameValuePair(SPACEKEY, spacekey));
        }
        if (StringUtils.trimToNull(title) != null) {
            nameValuePairs.add(new BasicNameValuePair(TITLE, title));
        }
        if (status != null) {
            nameValuePairs.add(new BasicNameValuePair(STATUS, status.getName()));
        }
        if (postingDay != null) {
            nameValuePairs.add(new BasicNameValuePair(POSTING_DAY, sdf.format(postingDay)));
        }
        if (expand != null && expand.isEmpty() == false) {
            String join = StringUtils.join(expand, ",");
            nameValuePairs.add(new BasicNameValuePair(EXPAND, join));
        }
        if (start > 0) {
            nameValuePairs.add(new BasicNameValuePair(START, String.valueOf(start)));
        }
        if (limit > 0) {
            nameValuePairs.add(new BasicNameValuePair(LIMIT, String.valueOf(limit)));
        }
        uriBuilder.addParameters(nameValuePairs);
        // Request
        return executorService.submit(() -> {
            return executeGetRequest(uriBuilder.build(), ContentResultsBean.class);
        });
    }

    public Future<ContentBean> createContent(ContentBean content) {
        if (log.isInfoEnabled()) {
            String message = "Creating content. Title=%1$s, space=%2$s";
            String spaceKey = (content.getSpace() != null) ? content.getSpace().getKey() : null;
            log.info(String.format(message, content.getTitle(), spaceKey));
        }
        // Request
        return executorService.submit(() -> {
            URI uri = buildPath(CONTENT).build();
            return executePostRequest(uri, content, ContentBean.class);
        });
    }

	@Override
    public Future<ContentBean> updateContent(ContentBean content) {
        if (log.isInfoEnabled()) {
            String message = "Updating content. Title=%1$s, space=%2$s";
            String spaceKey = (content.getSpace() != null) ? content.getSpace().getKey() : null;
            log.info(String.format(message, content.getTitle(), spaceKey));
        }
        // Request
        return executorService.submit(() -> {
            String contentUriPath = String.format(SPECIFIC_CONTENT, content.getId());
            URI uri = buildPath(contentUriPath).build();
            return executePostRequest(uri, content, ContentBean.class);
        });
    }

    @Override
    public Future<AttachmentBean> uploadAttachment(AttachmentBean attachment, ContentBean parentContent) {
        return executorService.submit(() -> {
            // URI
            String attachmentUriPath = String.format(CONTENT_ATTACHMENT, parentContent.getId());
            URI uri = buildPath(attachmentUriPath).build();
            // Check for comment
            String comment = null;
            if (attachment.getMetadata() != null && attachment.getMetadata().getComment() != null) {
                comment = attachment.getMetadata().getComment();
            }
            // Request
            AttachmentResultsBean results = executePostRequestForUpload(uri, attachment.getInputStream(), attachment.getTitle(), comment, AttachmentResultsBean.class);
            
            // Extract result
            int numberOfResults = 0;
            if (results.getResults() != null) {
                numberOfResults = results.getResults().size();
            }
            if (numberOfResults == 1) {
                return (AttachmentBean) results.getResults().get(0);
            } else {
                throw new UnexpectedContentException("Attachment result set with 1 element", "Attachemnt result set with " + numberOfResults + " elements");
            }
        });
    }

	@Override
    public Future<InputStream> downloadAttachement(AttachmentBean attachment) {
        // Check input
        if (attachment.getId() == null) {
            throw new IllegalArgumentException("ID of the attachment cannot be null");
        }
        if (attachment.getTitle() == null) {
            throw new IllegalArgumentException("Title of the attachment cannot be null");
        }
        return executorService.submit(() -> {
            // Determine download URI
            String downloadUriPath = null;
            if (attachment.getLinks() != null) {
                // Provided
                downloadUriPath = attachment.getLinks().getDownload();
            } else {
                // Not Provided
                Future<ContentBean> future = getContentById(attachment.getId(), 0, null);
                ContentBean attachmentContent = future.get();
                downloadUriPath = attachmentContent.getLinks().getDownload();
            }
            downloadUriPath = URLDecoder.decode(downloadUriPath, "UTF-8");
            URI uri = buildNonRestPath(downloadUriPath).build();
            // Request
            return executeGetRequestForDownload(uri);
        });
    }

    @Override
    public Future<LabelsBean> addLabels(ContentBean content, List<LabelBean> labels) {
        if (log.isInfoEnabled()) {
            String message = "Adding labels to content. Content ID=%1$s, labels %2$s";
            log.info(String.format(message, content.getId(), labels));
        }

        return executorService.submit(() -> {
            // URI
            String attachmentUriPath = String.format(CONTENT_LABEL, content.getId());
            URI uri = buildPath(attachmentUriPath).build();
            // Request
            return executePostRequest(uri, content, LabelsBean.class);
        });
    }

}
