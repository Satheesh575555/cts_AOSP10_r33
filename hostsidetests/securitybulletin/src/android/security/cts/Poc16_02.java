/**
 * Copyright (C) 2019 The Android Open Source Project
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

package android.security.cts;

import android.platform.test.annotations.SecurityTest;

public class Poc16_02 extends SecurityTestCase {
    /**
     *  b/25800375
     */
    @SecurityTest(minPatchLevel = "2016-02")
    public void testPocCVE_2016_0811() throws Exception {
        AdbUtils.runPocAssertNoCrashes("CVE-2016-0811", getDevice(), "mediaserver");
    }
}
