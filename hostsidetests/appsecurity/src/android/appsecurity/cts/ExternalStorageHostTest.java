/*
 * Copyright (C) 2015 The Android Open Source Project
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

package android.appsecurity.cts;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.android.compatibility.common.tradefed.build.CompatibilityBuildHelper;
import com.android.ddmlib.Log;
import com.android.tradefed.device.DeviceNotAvailableException;
import com.android.tradefed.testtype.DeviceJUnit4ClassRunner;
import com.android.tradefed.testtype.junit4.BaseHostJUnit4Test;
import com.android.tradefed.util.AbiUtils;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Set of tests that verify behavior of external storage devices.
 */
@RunWith(DeviceJUnit4ClassRunner.class)
public class ExternalStorageHostTest extends BaseHostJUnit4Test {
    private static final String TAG = "ExternalStorageHostTest";

    private static class Config {
        public final String apk;
        public final String pkg;
        public final String clazz;

        public Config(String apk, String pkg, String clazz) {
            this.apk = apk;
            this.pkg = pkg;
            this.clazz = clazz;
        }
    }

    private static final String COMMON_CLASS =
            "com.android.cts.externalstorageapp.CommonExternalStorageTest";

    private static final String NONE_APK = "CtsExternalStorageApp.apk";
    private static final String NONE_PKG = "com.android.cts.externalstorageapp";
    private static final String NONE_CLASS = NONE_PKG + ".ExternalStorageTest";
    private static final String READ_APK = "CtsReadExternalStorageApp.apk";
    private static final String READ_PKG = "com.android.cts.readexternalstorageapp";
    private static final String READ_CLASS = READ_PKG + ".ReadExternalStorageTest";
    private static final String WRITE_APK = "CtsWriteExternalStorageApp.apk";
    private static final String WRITE_PKG = "com.android.cts.writeexternalstorageapp";
    private static final String WRITE_CLASS = WRITE_PKG + ".WriteExternalStorageTest";
    private static final String WRITE_APK_2 = "CtsWriteExternalStorageApp2.apk";
    private static final String WRITE_PKG_2 = "com.android.cts.writeexternalstorageapp2";
    private static final String MULTIUSER_APK = "CtsMultiUserStorageApp.apk";
    private static final String MULTIUSER_PKG = "com.android.cts.multiuserstorageapp";
    private static final String MULTIUSER_CLASS = MULTIUSER_PKG + ".MultiUserStorageTest";

    private static final String MEDIA_CLAZZ = "com.android.cts.mediastorageapp.MediaStorageTest";

    private static final Config MEDIA = new Config("CtsMediaStorageApp.apk",
            "com.android.cts.mediastorageapp", MEDIA_CLAZZ);
    private static final Config MEDIA_28 = new Config("CtsMediaStorageApp28.apk",
            "com.android.cts.mediastorageapp28", MEDIA_CLAZZ);
    private static final Config MEDIA_FULL = new Config("CtsMediaStorageAppFull.apk",
            "com.android.cts.mediastorageappfull", MEDIA_CLAZZ);

    private static final String PERM_READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final String PERM_WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    private int[] mUsers;

    private File getTestAppFile(String fileName) throws FileNotFoundException {
        CompatibilityBuildHelper buildHelper = new CompatibilityBuildHelper(getBuild());
        return buildHelper.getTestFile(fileName);
    }

    @Before
    public void setUp() throws Exception {
        mUsers = Utils.prepareMultipleUsers(getDevice());
        assertNotNull(getAbi());
        assertNotNull(getBuild());
    }

    @Before
    @After
    public void cleanUp() throws DeviceNotAvailableException {
        getDevice().uninstallPackage(NONE_PKG);
        getDevice().uninstallPackage(READ_PKG);
        getDevice().uninstallPackage(WRITE_PKG);
        getDevice().uninstallPackage(MULTIUSER_PKG);

        wipePrimaryExternalStorage();
    }

    @Test
    public void testExternalStorageRename() throws Exception {
        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(WRITE_PKG);
            installPackage(WRITE_APK);

            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG, WRITE_CLASS, "testExternalStorageRename", user);
            }
        } finally {
            getDevice().uninstallPackage(WRITE_PKG);
        }
    }

    /**
     * Verify that app with no external storage permissions works correctly.
     */
    @Test
    public void testExternalStorageNone() throws Exception {
        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(NONE_PKG);
            String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};
            assertNull(getDevice().installPackage(getTestAppFile(NONE_APK), false, options));

            for (int user : mUsers) {
                runDeviceTests(NONE_PKG, COMMON_CLASS, user);
                runDeviceTests(NONE_PKG, NONE_CLASS, user);
            }
        } finally {
            getDevice().uninstallPackage(NONE_PKG);
        }
    }

    /**
     * Verify that app with
     * {@link android.Manifest.permission#READ_EXTERNAL_STORAGE} works
     * correctly.
     */
    @Test
    public void testExternalStorageRead() throws Exception {
        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(READ_PKG);
            String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};
            assertNull(getDevice().installPackage(getTestAppFile(READ_APK), false, options));

            for (int user : mUsers) {
                runDeviceTests(READ_PKG, COMMON_CLASS, user);
                runDeviceTests(READ_PKG, READ_CLASS, user);
            }
        } finally {
            getDevice().uninstallPackage(READ_PKG);
        }
    }

    /**
     * Verify that app with
     * {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE} works
     * correctly.
     */
    @Test
    public void testExternalStorageWrite() throws Exception {
        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(WRITE_PKG);
            String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};
            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK), false, options));

            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG, COMMON_CLASS, user);
                runDeviceTests(WRITE_PKG, WRITE_CLASS, user);
            }
        } finally {
            getDevice().uninstallPackage(WRITE_PKG);
        }
    }

    /**
     * Verify that app with WRITE_EXTERNAL can leave gifts in external storage
     * directories belonging to other apps, and those apps can read.
     */
    @Test
    public void testExternalStorageGifts() throws Exception {
        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(NONE_PKG);
            getDevice().uninstallPackage(READ_PKG);
            getDevice().uninstallPackage(WRITE_PKG);
            final String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};

            // We purposefully delay the installation of the reading apps to
            // verify that the daemon correctly invalidates any caches.
            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK), false, options));
            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteGiftTest", "testGifts", user);
            }

            assertNull(getDevice().installPackage(getTestAppFile(NONE_APK), false, options));
            assertNull(getDevice().installPackage(getTestAppFile(READ_APK), false, options));
            for (int user : mUsers) {
                runDeviceTests(READ_PKG, READ_PKG + ".ReadGiftTest", "testGifts", user);
                runDeviceTests(NONE_PKG, NONE_PKG + ".GiftTest", "testGifts", user);
            }
        } finally {
            getDevice().uninstallPackage(NONE_PKG);
            getDevice().uninstallPackage(READ_PKG);
            getDevice().uninstallPackage(WRITE_PKG);
        }
    }

    @Test
    public void testExternalStorageObbGifts() throws Exception {
        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(WRITE_PKG_2);
            getDevice().uninstallPackage(NONE_PKG);
            final String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};

            // We purposefully delay the installation of the reading apps to
            // verify that the daemon correctly invalidates any caches.
            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK_2), false, options));
            for (int user : mUsers) {
                updateAppOp(WRITE_PKG_2, user, "android:request_install_packages", true);
                updatePermissions(WRITE_PKG_2, user, new String[] {
                        PERM_READ_EXTERNAL_STORAGE,
                        PERM_WRITE_EXTERNAL_STORAGE,
                }, true);
            }

            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG_2, WRITE_PKG + ".WriteGiftTest", "testObbGifts", user);
            }

            assertNull(getDevice().installPackage(getTestAppFile(NONE_APK), false, options));
            for (int user : mUsers) {
                runDeviceTests(NONE_PKG, NONE_PKG + ".GiftTest", "testObbGifts", user);
            }

            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG_2, WRITE_PKG + ".WriteGiftTest",
                        "testAccessObbGifts", user);
                updateAppOp(WRITE_PKG_2, user, "android:request_install_packages", false);
                runDeviceTests(WRITE_PKG_2, WRITE_PKG + ".WriteGiftTest",
                        "testCantAccessObbGifts", user);
            }
        } finally {
            getDevice().uninstallPackage(WRITE_PKG_2);
            getDevice().uninstallPackage(NONE_PKG);
        }
    }

    @Test
    public void testExternalStorageUnsharedObb() throws Exception {
        final int numUsers = mUsers.length;
        Assume.assumeTrue(numUsers > 1);

        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(NONE_PKG);
            getDevice().uninstallPackage(WRITE_PKG);
            final String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};

            // We purposefully delay the installation of the reading apps to
            // verify that the daemon correctly invalidates any caches.
            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK), false, options));
            updateAppOp(WRITE_PKG, mUsers[0], "android:request_install_packages", true);
            runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteGiftTest", "testObbGifts", mUsers[0]);

            // Create a file in one user and verify that file is not accessible to other users.
            assertNull(getDevice().installPackage(getTestAppFile(NONE_APK), false, options));
            for (int i = 1; i < numUsers; ++i) {
                runDeviceTests(NONE_PKG, NONE_PKG + ".GiftTest", "testNoObbGifts", mUsers[i]);
                updateAppOp(WRITE_PKG, mUsers[i], "android:request_install_packages", true);
                runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteGiftTest", "testObbGifts", mUsers[i]);
            }

            // Delete a file in one user and verify that it doesn't affect files accessible to
            // other users.
            runDeviceTests(NONE_PKG, NONE_PKG + ".GiftTest", "testRemoveObbGifts", mUsers[0]);
            for (int i = 1; i < numUsers; ++i) {
                runDeviceTests(NONE_PKG, NONE_PKG + ".GiftTest", "testObbGifts", mUsers[i]);
            }

        } finally {
            getDevice().uninstallPackage(NONE_PKG);
            getDevice().uninstallPackage(WRITE_PKG);
        }
    }

    /**
     * Test multi-user emulated storage environment, ensuring that each user has
     * isolated storage.
     */
    @Test
    public void testMultiUserStorageIsolated() throws Exception {
        try {
            if (mUsers.length == 1) {
                Log.d(TAG, "Single user device; skipping isolated storage tests");
                return;
            }

            final int owner = mUsers[0];
            final int secondary = mUsers[1];

            // Install our test app
            getDevice().uninstallPackage(MULTIUSER_PKG);
            String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};
            final String installResult = getDevice()
                    .installPackage(getTestAppFile(MULTIUSER_APK), false, options);
            assertNull("Failed to install: " + installResult, installResult);

            // Clear data from previous tests
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testCleanIsolatedStorage", owner);
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testCleanIsolatedStorage", secondary);

            // Have both users try writing into isolated storage
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testWriteIsolatedStorage", owner);
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testWriteIsolatedStorage", secondary);

            // Verify they both have isolated view of storage
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testReadIsolatedStorage", owner);
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testReadIsolatedStorage", secondary);

            // Verify they can't poke at each other
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testUserIsolation", owner);
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testUserIsolation", secondary);

            // Verify they can't access other users' content using media provider
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testMediaProviderUserIsolation", owner);
            runDeviceTests(MULTIUSER_PKG, MULTIUSER_CLASS, "testMediaProviderUserIsolation", secondary);
        } finally {
            getDevice().uninstallPackage(MULTIUSER_PKG);
        }
    }

    /**
     * Test that apps with read permissions see the appropriate permissions
     * when apps with r/w permission levels move around their files.
     */
    @Test
    public void testMultiViewMoveConsistency() throws Exception {
        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(NONE_PKG);
            getDevice().uninstallPackage(READ_PKG);
            getDevice().uninstallPackage(WRITE_PKG);
            final String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};

            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK), false, options));
            assertNull(getDevice().installPackage(getTestAppFile(READ_APK), false, options));

            for (int user : mUsers) {
                runDeviceTests(READ_PKG, READ_PKG + ".ReadMultiViewTest", "testFolderSetup", user);
            }
            for (int user : mUsers) {
                runDeviceTests(READ_PKG, READ_PKG + ".ReadMultiViewTest", "testRWAccess", user);
            }

            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteMultiViewTest", "testMoveAway", user);
            }
            for (int user : mUsers) {
                runDeviceTests(READ_PKG, READ_PKG + ".ReadMultiViewTest", "testROAccess", user);
            }

            // for fuse file system
            Thread.sleep(10000);
            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteMultiViewTest", "testMoveBack", user);
            }
            for (int user : mUsers) {
                runDeviceTests(READ_PKG, READ_PKG + ".ReadMultiViewTest", "testRWAccess", user);
            }
        } finally {
            getDevice().uninstallPackage(NONE_PKG);
            getDevice().uninstallPackage(READ_PKG);
            getDevice().uninstallPackage(WRITE_PKG);
        }
    }

    /** Verify that app without READ_EXTERNAL can play default URIs in external storage. */
    @Test
    public void testExternalStorageReadDefaultUris() throws Exception {
        try {
            wipePrimaryExternalStorage();

            getDevice().uninstallPackage(NONE_PKG);
            getDevice().uninstallPackage(WRITE_PKG);
            final String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};

            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK), false, options));
            assertNull(getDevice().installPackage(getTestAppFile(NONE_APK), false, options));

            for (int user : mUsers) {
                updateAppOp(WRITE_PKG, user, "android:write_settings", true);
                runDeviceTests(
                        WRITE_PKG, WRITE_PKG + ".ChangeDefaultUris", "testChangeDefaultUris", user);

                runDeviceTests(
                        NONE_PKG, NONE_PKG + ".ReadDefaultUris", "testPlayDefaultUris", user);
            }
        } finally {
            // Make sure the provider and uris are reset on failure.
            for (int user : mUsers) {
                runDeviceTests(
                        WRITE_PKG, WRITE_PKG + ".ChangeDefaultUris", "testResetDefaultUris", user);
            }
            getDevice().uninstallPackage(NONE_PKG);
            getDevice().uninstallPackage(WRITE_PKG);
        }
    }

    /**
     * For security reasons, the shell user cannot access the shared storage of
     * secondary users. Instead, developers should use the {@code content} shell
     * tool to read/write files in those locations.
     */
    @Test
    public void testSecondaryUsersInaccessible() throws Exception {
        List<String> mounts = new ArrayList<>();
        for (String line : getDevice().executeShellCommand("cat /proc/mounts").split("\n")) {
            String[] split = line.split(" ");
            if (split[1].startsWith("/storage/") || split[1].startsWith("/mnt/")) {
                mounts.add(split[1]);
            }
        }

        for (int user : mUsers) {
            String probe = "/sdcard/../" + user;
            if (user == Utils.USER_SYSTEM) {
                // Primary user should always be visible. Skip checking raw
                // mount points, since we'd get false-positives for physical
                // devices that aren't multi-user aware.
                assertTrue(probe, access(probe));
            } else {
                // Secondary user should never be visible.
                assertFalse(probe, access(probe));
                for (String mount : mounts) {
                    probe = mount + "/" + user;
                    assertFalse(probe, access(probe));
                }
            }
        }
    }

    @Test
    public void testMediaSandboxed() throws Exception {
        doMediaSandboxed(MEDIA, true);
    }
    @Test
    public void testMediaSandboxed28() throws Exception {
        doMediaSandboxed(MEDIA_28, false);
    }
    @Test
    public void testMediaSandboxedFull() throws Exception {
        doMediaSandboxed(MEDIA_FULL, false);
    }

    private void doMediaSandboxed(Config config, boolean sandboxed) throws Exception {
        installPackage(config.apk);
        installPackage(MEDIA_FULL.apk);
        for (int user : mUsers) {
            updatePermissions(config.pkg, user, new String[] {
                    PERM_READ_EXTERNAL_STORAGE,
                    PERM_WRITE_EXTERNAL_STORAGE,
            }, true);
            updatePermissions(MEDIA_FULL.pkg, user, new String[] {
                    PERM_READ_EXTERNAL_STORAGE,
                    PERM_WRITE_EXTERNAL_STORAGE,
            }, true);

            // Create the files needed for the test from MEDIA_FULL pkg since shell
            // can't access secondary user's storage.
            runDeviceTests(MEDIA_FULL.pkg, MEDIA_FULL.clazz, "testStageFiles", user);

            if (sandboxed) {
                runDeviceTests(config.pkg, config.clazz, "testSandboxed", user);
            } else {
                runDeviceTests(config.pkg, config.clazz, "testNotSandboxed", user);
            }

            runDeviceTests(MEDIA_FULL.pkg, MEDIA_FULL.clazz, "testClearFiles", user);
        }
    }

    @Test
    public void testMediaNone() throws Exception {
        doMediaNone(MEDIA);
    }
    @Test
    public void testMediaNone28() throws Exception {
        doMediaNone(MEDIA_28);
    }
    @Test
    public void testMediaNoneFull() throws Exception {
        doMediaNone(MEDIA_FULL);
    }

    private void doMediaNone(Config config) throws Exception {
        installPackage(config.apk);
        for (int user : mUsers) {
            updatePermissions(config.pkg, user, new String[] {
                    PERM_READ_EXTERNAL_STORAGE,
                    PERM_WRITE_EXTERNAL_STORAGE,
            }, false);

            runDeviceTests(config.pkg, config.clazz, "testMediaNone", user);
        }
    }

    @Test
    public void testMediaRead() throws Exception {
        doMediaRead(MEDIA);
    }
    @Test
    public void testMediaRead28() throws Exception {
        doMediaRead(MEDIA_28);
    }
    @Test
    public void testMediaReadFull() throws Exception {
        doMediaRead(MEDIA_FULL);
    }

    private void doMediaRead(Config config) throws Exception {
        installPackage(config.apk);
        for (int user : mUsers) {
            updatePermissions(config.pkg, user, new String[] {
                    PERM_READ_EXTERNAL_STORAGE,
            }, true);
            updatePermissions(config.pkg, user, new String[] {
                    PERM_WRITE_EXTERNAL_STORAGE,
            }, false);

            runDeviceTests(config.pkg, config.clazz, "testMediaRead", user);
        }
    }

    @Test
    public void testMediaWrite() throws Exception {
        doMediaWrite(MEDIA);
    }
    @Test
    public void testMediaWrite28() throws Exception {
        doMediaWrite(MEDIA_28);
    }
    @Test
    public void testMediaWriteFull() throws Exception {
        doMediaWrite(MEDIA_FULL);
    }

    private void doMediaWrite(Config config) throws Exception {
        installPackage(config.apk);
        for (int user : mUsers) {
            updatePermissions(config.pkg, user, new String[] {
                    PERM_READ_EXTERNAL_STORAGE,
                    PERM_WRITE_EXTERNAL_STORAGE,
            }, true);

            runDeviceTests(config.pkg, config.clazz, "testMediaWrite", user);
        }
    }

    @Test
    public void testMediaEscalation() throws Exception {
        doMediaEscalation(MEDIA);
    }
    @Test
    public void testMediaEscalation28() throws Exception {
        doMediaEscalation(MEDIA_28);
    }
    @Test
    public void testMediaEscalationFull() throws Exception {
        doMediaEscalation(MEDIA_FULL);
    }

    private void doMediaEscalation(Config config) throws Exception {
        installPackage(config.apk);

        // TODO: extend test to exercise secondary users
        for (int user : Arrays.copyOf(mUsers, 1)) {
            updatePermissions(config.pkg, user, new String[] {
                    PERM_READ_EXTERNAL_STORAGE,
            }, true);
            updatePermissions(config.pkg, user, new String[] {
                    PERM_WRITE_EXTERNAL_STORAGE,
            }, false);

            runDeviceTests(config.pkg, config.clazz, "testMediaEscalation_Open", user);
            runDeviceTests(config.pkg, config.clazz, "testMediaEscalation_Update", user);
            runDeviceTests(config.pkg, config.clazz, "testMediaEscalation_Delete", user);
        }
    }

    @Test
    public void testExternalStorageClearing() throws Exception {
        String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};

        try {
            getDevice().uninstallPackage(WRITE_PKG);
            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK), false, options));
            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteGiftTest", "testClearingWrite", user);
            }

            // Uninstall and reinstall means all storage should be cleared
            getDevice().uninstallPackage(WRITE_PKG);
            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK), false, options));
            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteGiftTest", "testClearingRead", user);
            }
        } finally {
            getDevice().uninstallPackage(WRITE_PKG);
        }
    }

    @Test
    public void testIsExternalStorageLegacy() throws Exception {
        String[] options = {AbiUtils.createAbiFlag(getAbi().getName())};

        try {
            getDevice().uninstallPackage(WRITE_PKG);
            getDevice().uninstallPackage(WRITE_PKG_2);
            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK), false, options));
            assertNull(getDevice().installPackage(getTestAppFile(WRITE_APK_2), false, options));
            for (int user : mUsers) {
                runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteGiftTest",
                        "testIsExternalStorageLegacy", user);
                updatePermissions(WRITE_PKG, user, new String[] {
                        PERM_READ_EXTERNAL_STORAGE,
                        PERM_WRITE_EXTERNAL_STORAGE,
                }, false);
                runDeviceTests(WRITE_PKG, WRITE_PKG + ".WriteGiftTest",
                        "testIsExternalStorageLegacy", user);

                runDeviceTests(WRITE_PKG_2, WRITE_PKG + ".WriteGiftTest",
                        "testNotIsExternalStorageLegacy", user);
                updateAppOp(WRITE_PKG_2, user, "android:request_install_packages", true);
                runDeviceTests(WRITE_PKG_2, WRITE_PKG + ".WriteGiftTest",
                        "testIsExternalStorageLegacy", user);
            }
        } finally {
            getDevice().uninstallPackage(WRITE_PKG);
            getDevice().uninstallPackage(WRITE_PKG_2);
        }
    }

    private boolean access(String path) throws DeviceNotAvailableException {
        final long nonce = System.nanoTime();
        return getDevice().executeShellCommand("ls -la " + path + " && echo " + nonce)
                .contains(Long.toString(nonce));
    }

    private void updatePermissions(String packageName, int userId, String[] permissions,
            boolean grant) throws Exception {
        final String verb = grant ? "grant" : "revoke";
        for (String permission : permissions) {
            getDevice().executeShellCommand(
                    "cmd package " + verb + " --user " + userId + " --uid " + packageName + " "
                            + permission);
        }
    }

    private void updateAppOp(String packageName, int userId, String appOp, boolean allow)
            throws Exception {
        updateAppOp(packageName, false, userId, appOp, allow);
    }

    private void updateAppOp(String packageName, boolean targetsUid, int userId,
            String appOp, boolean allow)
            throws Exception {
        final String verb = allow ? "allow" : "default";
        getDevice().executeShellCommand(
                "cmd appops set --user " + userId + (targetsUid ? " --uid " : " ") + packageName
                        + " " + appOp + " " + verb);
    }

    private void wipePrimaryExternalStorage() throws DeviceNotAvailableException {
        // Can't delete everything under /sdcard as that's going to remove the mounts.
        getDevice().executeShellCommand("find /sdcard -type f -delete");
        getDevice().executeShellCommand("rm -rf /sdcard/DCIM");
        getDevice().executeShellCommand("rm -rf /sdcard/MUST_*");
    }

    private void runDeviceTests(String packageName, String testClassName, int userId)
            throws DeviceNotAvailableException {
        runDeviceTests(getDevice(), packageName, testClassName, null, userId, null);
    }

    private void runDeviceTests(String packageName, String testClassName, String testMethodName,
            int userId) throws DeviceNotAvailableException {
        runDeviceTests(getDevice(), packageName, testClassName, testMethodName, userId, null);
    }
}
