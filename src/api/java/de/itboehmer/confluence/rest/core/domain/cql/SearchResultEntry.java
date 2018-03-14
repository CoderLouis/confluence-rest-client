package de.itboehmer.confluence.rest.core.domain.cql;

import java.time.LocalDateTime;

import de.itboehmer.confluence.rest.core.domain.BaseBean;

/**
 * Entry in a search result, which encloses a bean (e.g., a ContentBean).
 */
public class SearchResultEntry {

	private LocalDateTime lastModified;
	private BaseBean bean;

	public SearchResultEntry(BaseBean bean, LocalDateTime lastModified) {
		this.bean = bean;
		this.lastModified = lastModified;
	}

	public BaseBean getBean() {
		return bean;
	}
	
	public LocalDateTime getLastModified() {
		return lastModified;
	}
}
