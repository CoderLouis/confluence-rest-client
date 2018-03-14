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
package de.itboehmer.confluence.rest.core.custom;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.itboehmer.confluence.rest.core.domain.BaseBean;
import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.cql.CqlSearchResult;
import de.itboehmer.confluence.rest.core.domain.cql.SearchResultEntry;
import de.itboehmer.confluence.rest.core.domain.space.SpaceBean;

/**
 * Author: Christian Schulze (c.schulze@micromata.de) Date: 07.07.2016 Project:
 * ConfluenceTransferPlugin
 */
public class CqlSearchResultDeserializer extends BaseDeserializer implements JsonDeserializer<CqlSearchResult> {

	private static final String SPACE = "space";
	private static final String CONTENT = "content";
	
	private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));

	@Override
	public CqlSearchResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		CqlSearchResult cqlSearchResult = gson.fromJson(json, CqlSearchResult.class);
		JsonObject jsonObject = json.getAsJsonObject();
		JsonElement resultsElement = jsonObject.get("results");
		JsonArray resultsArray = resultsElement.getAsJsonArray();
		List<SearchResultEntry> entries = deserializeResultsArray(resultsArray);
		cqlSearchResult.setResults(entries);
		return cqlSearchResult;
	}

	private List<SearchResultEntry> deserializeResultsArray(JsonArray results) {
		List<SearchResultEntry> retval = new ArrayList<>();
		for (JsonElement result : results) {
			JsonObject jsonObject = result.getAsJsonObject();
			if (jsonObject.has(CONTENT) == true) {
				retval.add(createSearchResultEntry(jsonObject, CONTENT, ContentBean.class));
			} else if (jsonObject.has(SPACE) == true) {
				retval.add(createSearchResultEntry(jsonObject, SPACE, SpaceBean.class));
			} else {
				// nothing
			}
		}
		return retval;
	}

	private SearchResultEntry createSearchResultEntry(JsonObject jsonObject, String type, Class<? extends BaseBean> beanClass) {
		JsonElement content = jsonObject.get(type);
		BaseBean bean = gson.fromJson(content, beanClass);
		String lastModifiedString = jsonObject.get("lastModified").getAsString();
		LocalDateTime lastModified = LocalDateTime.parse(lastModifiedString, INSTANT_FORMATTER);
		return new SearchResultEntry(bean, lastModified);
	}

}
