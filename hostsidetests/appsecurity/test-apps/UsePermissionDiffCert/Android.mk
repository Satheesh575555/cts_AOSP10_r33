# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := tests

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
    ../PermissionDeclareApp/src/com/android/cts/permissiondeclareapp/UtilsProvider.java

LOCAL_SDK_VERSION := current
LOCAL_STATIC_JAVA_LIBRARIES := androidx.test.rules

LOCAL_JAVA_LIBRARIES := android.test.base.stubs

LOCAL_PACKAGE_NAME := CtsUsePermissionDiffCert

# tag this module as a cts test artifact
LOCAL_COMPATIBILITY_SUITE := cts vts general-tests sts

# sign this app with a different cert than CtsPermissionDeclareApp
LOCAL_CERTIFICATE := cts/hostsidetests/appsecurity/certs/cts-testkey2

LOCAL_DEX_PREOPT := false

include $(BUILD_CTS_SUPPORT_PACKAGE)
