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

package com.moblino.countrynews.data.localdb;

import android.content.Context;


import com.moblino.countynews.common.model.RssItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faruktoptas on 01/08/16.
 */
public class FavouritePersistenceManager extends PersistenceManager<RssItem> {

    /**
     * This constructor is used to initialise the DAO object for the given class.
     * @param context
     */
    public FavouritePersistenceManager(Context context) {
        super(context, RssItem.class);
    }

    /**
     * Returns the records that contains the given name in the 'name' column
     *
     * @param url: The string that is expected in the 'name' column
     * @return
     */
    public List<RssItem> getFavouritesByUrl(String url) {

        // query for all users which has the given name
        try {
            return dao.query(dao.queryBuilder().where().eq("link", url).prepare());

        } catch (SQLException e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<RssItem> getFavouritesFeedId(int feedId) {

        // query for all users which has the given name
        try {
            return dao.query(dao.queryBuilder().where().eq("feedId", feedId).prepare());

        } catch (SQLException e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void deleteByUrl(String url){
        List<RssItem> list =  getFavouritesByUrl(url);
        if (list != null && list.size() > 0){
            delete(list.get(0).getId());
        }
    }

}