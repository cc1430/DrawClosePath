//
// Created by chenchen on 2024/6/4.
//

#include <android/log.h>

#define LOG_TAG "ChenChen"
#define ANDROID_PLATFORM

#ifdef ANDROID_PLATFORM
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

#else
#define LOGD(fmt, ...) printf(fmt"\n", ##__VA_ARGS__)
   #define LOGI(fmt, ...) printf(fmt"\n", ##__VA_ARGS__)
   #define LOGW(fmt, ...) printf(fmt"\n", ##__VA_ARGS__)
   #define LOGE(fmt, ...) printf(fmt"\n", ##__VA_ARGS__)
#endif


