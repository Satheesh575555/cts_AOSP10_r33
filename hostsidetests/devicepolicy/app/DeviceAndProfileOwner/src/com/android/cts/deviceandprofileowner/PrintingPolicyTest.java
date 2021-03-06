/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.cts.deviceandprofileowner;

import android.app.admin.DevicePolicyManager;
import android.os.UserManager;

/**
 * Validates that Device Owner or Profile Owner can disable printing.
 */
public class PrintingPolicyTest extends BaseDeviceAdminTest {

    public void testPrintingPolicy() throws Exception {
        mDevicePolicyManager.addUserRestriction(ADMIN_RECEIVER_COMPONENT,
                UserManager.DISALLOW_PRINTING);
        final PrintActivity activity = launchActivity("com.android.cts.deviceandprofileowner",
                PrintActivity.class, null);
        final String errorMessage = activity.getErrorMessage();
        assertNull(errorMessage);
    }
}
