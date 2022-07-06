# Copyright (C) 2015 The Android Open Source Project
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

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE_PATH := $(TARGET_OUT_DATA_APPS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := CtsLeanbackJankApp
LOCAL_SDK_VERSION := current
LOCAL_MIN_SDK_VERSION := 21

# Tag this module as a cts test artifact
LOCAL_COMPATIBILITY_SUITE := cts vts general-tests

LOCAL_USE_AAPT2 := true

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_STATIC_JAVA_LIBRARIES := \
    compatibility-device-util-axt \
    ctstestrunner-axt \
    ub-uiautomator \
    androidx.test.janktesthelper \
    glide

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.legacy_legacy-support-v4 \
    androidx.recyclerview_recyclerview \
    androidx.leanback_leanback

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_CTS_SUPPORT_PACKAGE)
