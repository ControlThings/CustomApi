LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
# Here we give our module name and source file(s)


LOCAL_MODULE    := mistcustomapi
LOCAL_SRC_FILES := 
LOCAL_C_INCLUDES := 
LOCAL_LDLIBS := -llog
LOCAL_CFLAGS := -O2 -Wall -Wno-pointer-sign -Werror -Wno-unused-variable -Wno-unused-function -fvisibility=hidden

#Put each function in own section, so that linker can discard unused code
LOCAL_CFLAGS += -ffunction-sections -fdata-sections 
#instruct linker to discard unsused code:
LOCAL_LDFLAGS += -Wl,--gc-sections

NDK_LIBS_OUT=jniLibs

include $(BUILD_SHARED_LIBRARY)
