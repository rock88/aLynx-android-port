LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_OPTIM := release

LOCAL_C_INCLUDES := $(LOCAL_PATH)/src $(LOCAL_PATH)/src/core $(LOCAL_PATH)/src/gles $(LOCAL_PATH)/src/java-bridge $(LOCAL_PATH)/src/alynx
LOCAL_CFLAGS :=  -O3 -ffast-math -fomit-frame-pointer -DANDROID_NDK -DDISABLE_IMPORTGL
LOCAL_CPPFLAGS := -O3 -frtti -ffast-math -fomit-frame-pointer
#APP_STL := stlport_static

LOCAL_ARM_MODE := thumb

LOCAL_MODULE    := alynx
### Add all source file names to be included in lib separated by a whitespace
LOCAL_SRC_FILES := 	src/core/Cart.cpp \
					src/core/ErrorHandler.cpp \
					src/core/Memmap.cpp \
					src/core/Mikie.cpp \
					src/core/Ram.cpp \
					src/core/Rom.cpp \
					src/core/Susie.cpp \
					src/core/System.cpp \
					src/core/Unzip.c \
					\
					src/Utils.c \
					\
					src/alynx/alynx.cpp \
					src/alynx/video.cpp \
					src/alynx/audio.c \
					\
					src/filters/hq2x.cpp \
					src/filters/2xsai.cpp \
					\
					src/java-bridge/main.c \
					\
					src/gles/gles_render.c \
					src/gles/importgl.c \
					
					
					

LOCAL_LDLIBS    += -lGLESv1_CM -ldl -llog -lz

include $(BUILD_SHARED_LIBRARY)
