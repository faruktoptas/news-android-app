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

package com.moblino.countrynews.parser;

import com.moblino.countynews.common.model.RssItem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class XMLParser extends DefaultHandler {

    private String elementValue = null;
    private boolean elementOn = false;
    private RssItem rssItem;

    private String tempTitle = "";
    private String tempLink;
    private String tempImage;
    private String tempPubdate;
    private String tempDescription;

    private boolean parsingTitle = false;
    private boolean parsingDesc = false;
    private boolean parsingLink = false;

    private final ArrayList<RssItem> items;

    public XMLParser() {
        super();
        items = new ArrayList<RssItem>();
    }

    public ArrayList<RssItem> getItems() {
        return items;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        elementOn = true;
        if (localName.equals("item") || localName.equals("entry")) {
            rssItem = new RssItem();
        } else if (localName.equalsIgnoreCase("title") && !qName.contains("media")) {
            parsingTitle = true;
            tempTitle = "";
        } else if (localName.equalsIgnoreCase("description")) {
            parsingDesc = true;
            tempDescription = "";
        } else if (localName.equalsIgnoreCase("link") && !qName.equals("atom:link")) {
            parsingLink = true;
            tempLink = "";
        }
        if (attributes != null && !localName.equals("source")) { // nasa rss source is item source not image
            String url = attributes.getValue("url");
            if (url != null && !url.isEmpty()) {
                tempImage = url;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        elementOn = false;

        /**
         * Sets the values after retrieving the values from the XML tags
         * */
        if (rssItem != null) {
            if (localName.equalsIgnoreCase("item") || localName.equals("entry")) {
                rssItem = new RssItem();
                rssItem.setTitle(tempTitle.trim());
                rssItem.setLink(tempLink);
                rssItem.setImage(HtmlUtil.optimizeImage(tempImage, tempLink));
                rssItem.setPubDate(tempPubdate);
                rssItem.setDescription((HtmlUtil.fixDescription(tempDescription)));
                if (tempImage == null && tempDescription != null && HtmlUtil.getImageSourceFromDescription(tempDescription) != null) {
                    rssItem.setImage(HtmlUtil.optimizeImage(HtmlUtil.getImageSourceFromDescription(tempDescription), tempLink));
                }
                items.add(rssItem);
                tempLink = "";
                tempImage = null;
                tempPubdate = "";
                // Log.v("asd","pended: " + tempTitle);

            } else if (localName.equalsIgnoreCase("title") && !qName.contains("media")) {
                // tempTitle = elementValue;
                parsingTitle = false;
                elementValue = "";
                tempTitle = tempTitle.replace("\n", "");
            } else if (localName.equalsIgnoreCase("link")
                    && !elementValue.isEmpty()) {
                // tempLink = elementValue;
                parsingLink = false;
                elementValue = "";
                tempLink = tempLink.replace("\n", "");
            } else if (localName.equalsIgnoreCase("image")
                    || localName.equalsIgnoreCase("url")) {
                if (elementValue != null && !elementValue.isEmpty()) {
                    tempImage = elementValue;
                }
            } else if (localName.equals("pubDate")) {
                tempPubdate = elementValue;
            } else if (localName.equals("description")) {
                parsingDesc = false;
                elementValue = "";
            }

        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        String buff = new String(ch, start, length);
        if (elementOn) {
            if (buff.length() > 2) {
                elementValue = buff;
                elementOn = false;
            }
        }
        if (parsingTitle) {
            tempTitle = tempTitle + buff;
        }
        if (parsingDesc) {
            tempDescription = tempDescription + buff;
        }
        if (parsingLink) {
            tempLink = tempLink + buff;
        }
    }

}
