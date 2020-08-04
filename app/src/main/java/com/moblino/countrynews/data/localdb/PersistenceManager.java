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
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.moblino.countrynews.model.RssItem;

import java.sql.SQLException;
import java.util.List;

public abstract class PersistenceManager<E extends PersistenceManager.Modal> {

    Dao dao;
    private final DatabaseHelper mDatabaseHelper;

    PersistenceManager(Context context, Class c) {
        mDatabaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);

        try {
            dao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(), c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public final boolean create(E e) {
        if (exists(e.getId())) {
            String TAG = "PersistenceManager";
            Log.e(TAG, "An entry with the same id already exists.");
            return false;
        }
        try {
            dao.create(e);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final E read(int id) {
        try {
            return (E) dao.queryForId(id + "");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final List<E> readAll() {
        try {
            return (List<E>) dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final boolean update(E e){
        try {
            dao.update(e);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean delete(int id) {
        try {
            dao.deleteById(id);
            return true;
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    public final boolean exists(int id) {
        try {
            return dao.queryForId(id + "") != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface Modal {
        int getId();
    }


    public void clearDatabase(){
        try {
            TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), RssItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}