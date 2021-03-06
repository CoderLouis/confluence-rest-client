/**
 * Copyright 2016 Micromata GmbH
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
package de.itboehmer.confluence.rest.core.domain.cql;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import de.itboehmer.confluence.rest.core.domain.common.LinksBean;

/**
 * Author: Christian Schulze (c.schulze@micromata.de)
 * Date: 06.07.2016
 * Project: ConfluenceTransferPlugin
 */
public class CqlSearchResult {

    private List<SearchResultEntry> results = new ArrayList<>();
    @Expose
    private int start;
    @Expose
    private int limit;
    @Expose
    private int size;
    @Expose
    private int totalSize;
    @Expose
    private String cqlQuery;
    @Expose
    private int searchDuration;
    @Expose
    private LinksBean _links;

    public LinksBean get_links() {
        return _links;
    }

    public void set_links(LinksBean _links) {
        this._links = _links;
    }

    public String getCqlQuery() {
        return cqlQuery;
    }

    public void setCqlQuery(String cqlQuery) {
        this.cqlQuery = cqlQuery;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }


    public int getSearchDuration() {
        return searchDuration;
    }

    public void setSearchDuration(int searchDuration) {
        this.searchDuration = searchDuration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public List<SearchResultEntry> getResults() {
        return results;
    }

    public void setResults(List<SearchResultEntry> results) {
        this.results = results;
    }
}
