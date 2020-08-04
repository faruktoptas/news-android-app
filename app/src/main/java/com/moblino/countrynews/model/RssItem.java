/*
 *  Copyright (c) 2020. Faruk Toptaş
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.moblino.countrynews.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.moblino.countrynews.data.localdb.PersistenceManager;

import java.io.Serializable;

@DatabaseTable(tableName = "Favourites")
public class RssItem implements PersistenceManager.Modal, Serializable {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String title;

	@DatabaseField
	private String link;

	@DatabaseField
	private String image;

	@DatabaseField
	private String pubDate;

	@DatabaseField
	private int feedId;

    @DatabaseField
    private String feedTitle;

	@DatabaseField
    private String description;

    public RssItem(String title, String link, String image, String pubDate, int feedId, String feedTitle, String description) {

        this.title = title;
        this.link = link;
        this.image = image;
        this.pubDate = pubDate;
        this.feedId = feedId;
        this.feedTitle = feedTitle;
		this.description = description;
    }

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title.replace("&#39;","'").replace("&#039;","'");
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link.trim();
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RssItem() {
	}

	@Override
	public int getId() {
		return id;
	}

    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        feedTitle = feedTitle;
    }
}
