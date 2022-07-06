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
 * limitations under the License
 */

package android.server.wm.lifecycle;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.server.wm.app.Components.PipActivity.EXTRA_ENTER_PIP;
import static android.server.wm.lifecycle.LifecycleLog.ActivityCallback.ON_PAUSE;
import static android.server.wm.lifecycle.LifecycleLog.ActivityCallback.ON_RESTART;
import static android.server.wm.lifecycle.LifecycleLog.ActivityCallback.ON_RESUME;
import static android.server.wm.lifecycle.LifecycleLog.ActivityCallback.ON_START;
import static android.server.wm.lifecycle.LifecycleLog.ActivityCallback.ON_STOP;

import static org.junit.Assume.assumeTrue;

import android.app.Activity;
import android.content.Intent;
import android.platform.test.annotations.Presubmit;

import androidx.test.filters.FlakyTest;
import androidx.test.filters.MediumTest;

import org.junit.Test;

import java.util.Arrays;

/**
 * Build/Install/Run:
 *     atest CtsWindowManagerDeviceTestCases:ActivityLifecycleKeyguardTests
 */
@MediumTest
@Presubmit
public class ActivityLifecycleKeyguardTests extends ActivityLifecycleClientTestBase {

    @Test
    @FlakyTest(bugId = 131005232)
    public void testSingleLaunch() throws Exception {
        assumeTrue(supportsSecureLock());
        try (final LockScreenSession lockScreenSession = new LockScreenSession()) {
            lockScreenSession.setLockCredential().gotoKeyguard();

            final Activity activity = mFirstActivityTestRule.launchActivity(new Intent());
            waitAndAssertActivityStates(state(activity, ON_STOP));

            LifecycleVerifier.assertLaunchAndStopSequence(FirstActivity.class, getLifecycleLog());
        }
    }

    @Test
    @FlakyTest(bugId = 131005232)
    public void testKeyguardShowHide() throws Exception {
        assumeTrue(supportsSecureLock());

        // Launch first activity and wait for resume
        final Activity activity = mFirstActivityTestRule.launchActivity(new Intent());
        waitAndAssertActivityStates(state(activity, ON_RESUME));

        // Show and hide lock screen
        try (final LockScreenSession lockScreenSession = new LockScreenSession()) {
            lockScreenSession.setLockCredential().gotoKeyguard();
            waitAndAssertActivityStates(state(activity, ON_STOP));

            LifecycleVerifier.assertLaunchAndStopSequence(FirstActivity.class, getLifecycleLog());
            getLifecycleLog().clear();
        } // keyguard hidden

        // Verify that activity was resumed
        waitAndAssertActivityStates(state(activity, ON_RESUME));
        LifecycleVerifier.assertRestartAndResumeSequence(FirstActivity.class, getLifecycleLog());
    }

    @Test
    @FlakyTest(bugId = 131005232)
    public void testKeyguardShowHideOverSplitScreen() throws Exception {
        assumeTrue(supportsSecureLock());
        assumeTrue(supportsSplitScreenMultiWindow());

        final Activity firstActivity = mFirstActivityTestRule.launchActivity(new Intent());
        waitAndAssertActivityStates(state(firstActivity, ON_RESUME));

        // Enter split screen
        moveTaskToPrimarySplitScreenAndVerify(firstActivity);

        // Launch second activity to side
        final Activity secondActivity = mSecondActivityTestRule.launchActivity(
                new Intent().setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_MULTIPLE_TASK));

        // Wait for second activity to resume.
        waitAndAssertActivityStates(state(secondActivity, ON_RESUME));
        // Leaving the minimized dock, the stack state on the primary split screen should change
        // from Paused to Resumed.
        waitAndAssertActivityStates(state(firstActivity, ON_RESUME));

        // Show and hide lock screen
        getLifecycleLog().clear();
        try (final LockScreenSession lockScreenSession = new LockScreenSession()) {
            lockScreenSession.setLockCredential().gotoKeyguard();
            waitAndAssertActivityStates(state(firstActivity, ON_STOP));
            waitAndAssertActivityStates(state(secondActivity, ON_STOP));

            LifecycleVerifier.assertResumeToStopSequence(FirstActivity.class, getLifecycleLog());
            LifecycleVerifier.assertResumeToStopSequence(SecondActivity.class, getLifecycleLog());
            getLifecycleLog().clear();
        } // keyguard hidden

        waitAndAssertActivityStates(state(firstActivity, ON_RESUME),
                state(secondActivity, ON_RESUME));
        LifecycleVerifier.assertRestartAndResumeSequence(FirstActivity.class, getLifecycleLog());
        LifecycleVerifier.assertRestartAndResumeSequence(SecondActivity.class, getLifecycleLog());
    }

    @Test
    @FlakyTest(bugId = 131005232)
    public void testKeyguardShowHideOverPip() throws Exception {
        if (!supportsPip()) {
            // Skipping test: no Picture-In-Picture support
            return;
        }

        // Launch first activity
        final Activity firstActivity = mFirstActivityTestRule.launchActivity(new Intent());

        // Clear the log before launching to Pip
        waitAndAssertActivityStates(state(firstActivity, ON_RESUME));
        getLifecycleLog().clear();

        // Launch Pip-capable activity and enter Pip immediately
        final Activity pipActivity = mPipActivityTestRule.launchActivity(
                new Intent().putExtra(EXTRA_ENTER_PIP, true));

        // Wait and assert lifecycle
        waitAndAssertActivityStates(state(firstActivity, ON_RESUME), state(pipActivity, ON_PAUSE));

        // Show and hide lock screen
        getLifecycleLog().clear();
        try (final LockScreenSession lockScreenSession = new LockScreenSession()) {
            lockScreenSession.setLockCredential().gotoKeyguard();
            waitAndAssertActivityStates(state(firstActivity, ON_STOP));
            waitAndAssertActivityStates(state(pipActivity, ON_STOP));

            LifecycleVerifier.assertResumeToStopSequence(FirstActivity.class, getLifecycleLog());
            LifecycleVerifier.assertSequence(PipActivity.class, getLifecycleLog(),
                    Arrays.asList(ON_STOP), "keyguardShown");
            getLifecycleLog().clear();
        } // keyguard hidden

        // Wait and assert lifecycle
        waitAndAssertActivityStates(state(firstActivity, ON_RESUME), state(pipActivity, ON_PAUSE));
        LifecycleVerifier.assertRestartAndResumeSequence(FirstActivity.class, getLifecycleLog());
        LifecycleVerifier.assertSequence(PipActivity.class, getLifecycleLog(),
                Arrays.asList(ON_RESTART, ON_START, ON_RESUME, ON_PAUSE), "keyguardGone");
    }
}
