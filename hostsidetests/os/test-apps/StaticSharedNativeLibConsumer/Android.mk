#
# Copyright (C) 2017 The Android Open Source Project
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

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := tests

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := \
    androidx.test.rules

LOCAL_JNI_SHARED_LIBRARIES := libstaticsharednativelibconsumerjni

LOCAL_PACKAGE_NAME := CtsStaticSharedNativeLibConsumer
LOCAL_SDK_VERSION := current

LOCAL_MULTILIB := both

LOCAL_COMPATIBILITY_SUITE := cts vts general-tests cts_instant

include $(BUILD_CTS_SUPPORT_PACKAGE)

#########################################################################
# Build JNI Shared Library
#########################################################################

LOCAL_PATH:= $(LOCAL_PATH)/jni

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := tests

LOCAL_CFLAGS := -Wall -Wextra -Werror

LOCAL_SRC_FILES := $(call all-cpp-files-under)

LOCAL_SHARED_LIBRARIES := liblog \
    libstaticsharednativelibprovider

LOCAL_SDK_VERSION := current
LOCAL_NDK_STL_VARIANT := none

LOCAL_C_INCLUDES += \
    $(JNI_H_INCLUDE) \
    $(LOCAL_PATH)/../CtsStaticSharedNativeLibProvider/native/version.h

LOCAL_MODULE := libstaticsharednativelibconsumerjni

include $(BUILD_SHARED_LIBRARY)

