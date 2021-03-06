#
# Copyright (C) 2018 The Android Open Source Project
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
#
LOCAL_PATH := $(call my-dir)

# A fake framework that mimics an older, pre-release SDK for the purposes of
# testing what happens when an app linked against a pre-release SDK is installed
# on release device.

include $(CLEAR_VARS)
LOCAL_USE_AAPT2 := true
LOCAL_PACKAGE_NAME := fake-framework
LOCAL_SDK_VERSION := core_current
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_MODULE_TAGS := optional
LOCAL_EXPORT_PACKAGE_RESOURCES := true
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_UNINSTALLABLE_MODULE := true
include $(BUILD_PACKAGE)
