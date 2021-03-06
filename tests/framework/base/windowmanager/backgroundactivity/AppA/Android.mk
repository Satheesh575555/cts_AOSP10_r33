# Copyright (C) 2019 The Android Open Source Project
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

# Don't include this package in any target.
LOCAL_MODULE_TAGS := tests

LOCAL_USE_AAPT2 := true

LOCAL_STATIC_JAVA_LIBRARIES := \
    cts-wm-app-base

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.legacy_legacy-support-v4

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_SDK_VERSION := test_current

# Tag this module as a cts test artifact
LOCAL_COMPATIBILITY_SUITE := cts vts general-tests

LOCAL_PACKAGE_NAME := CtsBackgroundActivityAppA

include $(BUILD_CTS_SUPPORT_PACKAGE)
