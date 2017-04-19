package de.micromata.confluence.rest.client;

import de.micromata.confluence.rest.core.domain.content.AttachmentBean;
import de.micromata.confluence.rest.core.domain.content.ContentBean;
import de.micromata.confluence.rest.core.domain.content.ContentResultsBean;
import de.micromata.confluence.rest.core.misc.ContentStatus;
import de.micromata.confluence.rest.core.misc.ContentType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Client to receive Content from a confluence server.
 *
 * Authors: Christian Schulze (c.schulze@micromata.de), Martin Böhmer (mb@itboehmer.de)
 * Created: 04.07.2016
 * Modified: 19.04.2017
 * Project: ConfluenceTransferPlugin
 */
public interface ContentClient {


    /**
     * Get Content by ID
     *
     * @param id The ID of the content
     * @param version The Version of the content
     * @param expand A List of Fields to expand
     * @return A {@link Future} with the {@link ContentBean}
     */
    Future<ContentBean> getContentById(String id, int version, List<String> expand);

    /**
     * Returns a paginated list of Content.
     *
     * @param type the content type to return. Default value: page. Valid values: page, blogpost.
     * @param spacekey the space key to find content under.
     * @param title the title of the page to find. Required for page type.
     * @param status list of statuses the content to be found is in. Defaults to current is not specified. If set to 'any', content in 'current' and 'trashed' status will be fetched. Does not support 'historical' status for now.
     * @param postingDay the posting day of the blog post.
     * @param expand a comma separated list of properties to expand on the content. Default value: history,space,version.
     * @param start the start point of the collection to return
     * @param limit the limit of the number of items to return, this may be restricted by fixed system limits
     * @return A {@link Future} with the {@link ContentResultsBean}
     */
    Future<ContentResultsBean> getContent(ContentType type,
                                          String spacekey,
                                          String title,
                                          ContentStatus status,
                                          Date postingDay,
                                          List<String> expand,
                                          int start,
                                          int limit) throws URISyntaxException;
    
    /**
     * Creates the provided content.
     *
     * @param content The content to create.
     * @return A {@link Future} with the {@link ContentBean} representing the created page. Use this instance to continue working with the content.
	 */
    public Future<ContentBean> createContent(ContentBean content) throws URISyntaxException;
	
	/**
     * Uploads the provided attachment to content (page or blogpost).
     *
     * @param attachment The attachement to upload.
	 * @param parentContent The content (page or blogpost) to upload the attachment to.
     * @return A {@link Future} with the {@link AttachmentBean} representing the uploaded attachement. Use this instance to continue working with the attachment.
	 */
	public Future<AttachmentBean> uploadAttachment(AttachmentBean attachment, ContentBean parentContent) throws URISyntaxException;
    
}
