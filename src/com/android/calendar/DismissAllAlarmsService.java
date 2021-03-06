/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.calendar;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Calendar.CalendarAlerts;

/**
 * Service for asynchronously marking all fired alarms as dismissed. 
 */
public class DismissAllAlarmsService extends IntentService {
    private static final String[] PROJECTION = new String[] {
            CalendarAlerts._ID,
            CalendarAlerts.STATE,
    };
    private static final int COLUMN_INDEX_STATE = 1;  

    public DismissAllAlarmsService() {
        super("DismissAllAlarmsService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onHandleIntent(Intent intent) {
        // Mark all fired alarms as dismissed
        Uri uri = CalendarAlerts.CONTENT_URI_BY_INSTANCE;
        String selection = CalendarAlerts.STATE + "=" + CalendarAlerts.FIRED;
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, PROJECTION, selection, null, null);
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                cursor.updateInt(COLUMN_INDEX_STATE, CalendarAlerts.DISMISSED);
            }
            cursor.commitUpdates();
            cursor.close();
        }

        // Stop this service
        stopSelf();
    }
}
