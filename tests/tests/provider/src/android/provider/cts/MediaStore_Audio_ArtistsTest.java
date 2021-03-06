/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.provider.cts;

import static android.provider.cts.MediaStoreTest.TAG;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.platform.test.annotations.Presubmit;
import android.provider.MediaStore.Audio.Artists;
import android.provider.cts.MediaStoreAudioTestHelper.Audio1;
import android.provider.cts.MediaStoreAudioTestHelper.Audio2;
import android.util.Log;

import androidx.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@Presubmit
@RunWith(Parameterized.class)
public class MediaStore_Audio_ArtistsTest {
    private Context mContext;
    private ContentResolver mContentResolver;

    @Parameter(0)
    public String mVolumeName;

    @Parameters
    public static Iterable<? extends Object> data() {
        return ProviderTestUtils.getSharedVolumeNames();
    }

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        mContentResolver = mContext.getContentResolver();

        Log.d(TAG, "Using volume " + mVolumeName);
    }

    @Test
    public void testGetContentUri() {
        Cursor c = null;
        assertNotNull(c = mContentResolver.query(
                Artists.getContentUri(mVolumeName), null, null,
                null, null));
        c.close();
    }

    @Test
    public void testStoreAudioArtists() {
        Uri artistsUri = Artists.getContentUri(mVolumeName);
        // do not support insert operation of the artists
        try {
            mContentResolver.insert(artistsUri, new ContentValues());
            fail("Should throw UnsupportedOperationException!");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // the artist items are inserted when inserting audio media
        Uri uri = Audio1.getInstance().insert(mContentResolver, mVolumeName);

        String selection = Artists.ARTIST + "=?";
        String[] selectionArgs = new String[] { Audio1.ARTIST };
        try {
            // query
            Cursor c = mContentResolver.query(artistsUri, null, selection, selectionArgs, null);
            assertEquals(1, c.getCount());
            c.moveToFirst();

            assertEquals(Audio1.ARTIST, c.getString(c.getColumnIndex(Artists.ARTIST)));
            long id = c.getLong(c.getColumnIndex(Artists._ID));
            assertTrue(id > 0);
            assertNotNull(c.getString(c.getColumnIndex(Artists.ARTIST_KEY)));
            assertEquals(1, c.getInt(c.getColumnIndex(Artists.NUMBER_OF_ALBUMS)));
            assertEquals(1, c.getInt(c.getColumnIndex(Artists.NUMBER_OF_TRACKS)));
            c.close();

            // do not support update operation of the artists
            ContentValues artistValues = new ContentValues();
            artistValues.put(Artists.ARTIST, Audio2.ALBUM);
            try {
                mContentResolver.update(artistsUri, artistValues, selection, selectionArgs);
                fail("Should throw UnsupportedOperationException!");
            } catch (UnsupportedOperationException e) {
                // expected
            }

            // do not support delete operation of the artists
            try {
                mContentResolver.delete(artistsUri, selection, selectionArgs);
                fail("Should throw UnsupportedOperationException!");
            } catch (UnsupportedOperationException e) {
                // expected
            }

            // test filtering
            Uri filterUri = artistsUri.buildUpon()
                .appendQueryParameter("filter", Audio1.ARTIST).build();
            c = mContentResolver.query(filterUri, null, null, null, null);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            long fid = c.getLong(c.getColumnIndex(Artists._ID));
            assertTrue(id == fid);
            c.close();

            filterUri = artistsUri.buildUpon().appendQueryParameter("filter", "xyzfoo").build();
            c = mContentResolver.query(filterUri, null, null, null, null);
            assertEquals(0, c.getCount());
            c.close();
        } finally {
            mContentResolver.delete(uri, null, null);
        }
        // the artist items are deleted when deleting the audio media which belongs to the album
        Cursor c = mContentResolver.query(artistsUri, null, selection, selectionArgs, null);
        assertEquals(0, c.getCount());
        c.close();
    }
}
