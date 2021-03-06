/*
 * Copyright (C) 2017 The Android Open Source Project
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
package android.jvmti.cts;

import android.jvmti.JvmtiActivity;

import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import art.CtsMain;

/**
 * Base class for JVMTI tests. Ensures that the agent is connected for the tests. If you
 * do not subclass this test, make sure that JniBindings.waitFor is appropriately called.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public abstract class JvmtiTestBase {

    /**
     * A reference to the activity being tested.
     */
    protected JvmtiActivity mActivity;

    @Rule
    public ActivityTestRule<JvmtiActivity> mActivityRule =
            new ActivityTestRule<>(JvmtiActivity.class);

    @Before
    public void setup() {
        mActivity = mActivityRule.getActivity();

        // Make sure that the agent is ready.
        CtsMain.waitFor();
    }
}
