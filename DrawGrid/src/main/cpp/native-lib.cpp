#include <jni.h>
#include <string>
#include "AndroidLog.h"

#define VERSION "1.0.1"

extern "C" JNIEXPORT jstring JNICALL
Java_com_cc_nativeproject_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_cc_draw_view_GridImageView_getRow(JNIEnv *env, jobject thiz) {
    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "row", "I");
    jint a = env->GetIntField(thiz, id);
    return a;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_cc_draw_view_GridImageView_getColumn(JNIEnv *env, jobject thiz) {
    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "column", "I");
    jint a = env->GetIntField(thiz, id);
    return a;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_cc_draw_view_GridImageView_init(JNIEnv *env, jobject thiz, jint row, jint column) {
    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID rowId = env->GetFieldID(gridImageViewClazz, "row", "I");
    env->SetIntField(thiz, rowId, row);

    jfieldID columnId = env->GetFieldID(gridImageViewClazz, "column", "I");
    env->SetIntField(thiz, columnId, column);

    jobjectArray result;
    jclass clsIntArray;
    jint i, j;
    // 1.获得一个int型二维数组类的引用
    clsIntArray = env->FindClass("[I");
    if (clsIntArray == NULL) {
        return NULL;
    }
    // 2.创建一个数组对象（里面每个元素用clsIntArray表示）
    result = env->NewObjectArray(row, clsIntArray, NULL);
    if (result == NULL) {
        return NULL;
    }

    // 3.为数组元素赋值
    for (i = 0; i < row; ++i) {
        jint buff[256];
        jintArray intArr = env->NewIntArray(column);
        if (intArr == NULL) {
            return NULL;
        }
        for (j = 0; j < column; j++) {
            buff[j] = 0;
        }
        env->SetIntArrayRegion(intArr, 0, column, buff);
        env->SetObjectArrayElement(result, i, intArr);

        jfieldID id = env->GetFieldID(gridImageViewClazz, "integerArray", "[[I");
        env->SetObjectField(thiz, id, result);
        env->DeleteLocalRef(intArr);
    }

    return result;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_cc_draw_view_GridImageView_getArea(JNIEnv *env, jobject thiz) {
    std::string result;

    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "integerArray", "[[I");
    jobjectArray array = static_cast<jobjectArray>(env->GetObjectField(thiz, id));

    if (array == NULL) {
        // 错误处理...
        LOGE("array == null");
        return env->NewStringUTF(result.c_str());
    }

    // 获取数组的长度（行数）
    jsize rows = env->GetArrayLength(array);

    for (jsize i = 0; i < rows; i++) {
        // 获取第i行的jintArray对象
        jintArray rowArray = (jintArray) env->GetObjectArrayElement(array, i);

        // 检查是否成功获取了行数组
        if (rowArray == NULL) {
            // 错误处理...
            LOGE("rowArray == null");
            return env->NewStringUTF(result.c_str());
        }

        jsize columnSize = env->GetArrayLength(rowArray);
        jint* element = env->GetIntArrayElements(rowArray, JNI_FALSE);

        // 打印当前行的所有元素
        std::string str;
        for (jsize j = 0; j < columnSize; j++) {
            // 获取第j列的元素
            str.append(std::to_string(element[j]));
        }
        jint decimalNumber = std::stoi(str, 0, 2);
        result.append(std::to_string(decimalNumber));
        if (i != rows - 1) {
            result.append(",");
        }

        // 释放局部引用
        env->ReleaseIntArrayElements(rowArray, element, 0);
        env->DeleteLocalRef(rowArray);
    }

    return env->NewStringUTF(result.c_str());
}


extern "C"
JNIEXPORT void JNICALL
Java_com_cc_draw_view_GridImageView_fillRect(JNIEnv *env, jobject thiz, jint x, jint y,
                                             jboolean b_fill) {
    // 注意：这里我们假设数组是int[][]的Java数组，但JNI将其视为jobjectArray的数组
    // 我们需要获取数组的每一行，然后修改特定的元素

    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "integerArray", "[[I");
    jobjectArray array = static_cast<jobjectArray>(env->GetObjectField(thiz, id));


    // 获取数组的长度（行数）
    jsize rows = env->GetArrayLength(array);

    // 检查索引是否有效
    if (x < 0 || x >= rows || y < 0) {
        // 错误处理...
        return;
    }

    // 获取第x行的jintArray对象
    jintArray rowArray = (jintArray) env->GetObjectArrayElement(array, x);

    // 检查是否成功获取了行数组
    if (rowArray == NULL) {
        // 错误处理...
        return;
    }

    // 获取第x行的长度（列数）并检查y索引是否有效
    jsize cols = env->GetArrayLength(rowArray);
    if (y >= cols) {
        // 错误处理...
        env->DeleteLocalRef(rowArray); // 释放局部引用
        return;
    }

    // 修改第y列的值为1
    jint value = 1; // 注意：这个变量必须在SetIntArrayRegion调用之前定义，因为它在栈上
    if (b_fill) {
        value = 1;
    } else {
        value = 0;
    }

    env->SetIntArrayRegion(rowArray, y, 1, (const jint *) &value); // 注意：这里需要一个jint指针，所以我们传递一个临时变量的地址

    // 释放局部引用
    env->DeleteLocalRef(rowArray);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_cc_draw_view_GridImageView_setMode(JNIEnv *env, jobject thiz, jint mode) {
    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "mode", "I");
    env->SetIntField(thiz, id, mode);
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_cc_draw_view_GridImageView_isDrawMode(JNIEnv *env, jobject thiz) {
    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "mode", "I");
    jint mode = env->GetIntField(thiz, id);

    jfieldID modeDrawId = env->GetStaticFieldID(gridImageViewClazz, "MODE_DRAW", "I");
    jint modeDraw = env->GetStaticIntField(gridImageViewClazz, modeDrawId);
    return mode == modeDraw;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_cc_draw_view_GridImageView_isEraseMode(JNIEnv *env, jobject thiz) {
    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "mode", "I");
    jint mode = env->GetIntField(thiz, id);

    jfieldID modeEraseId = env->GetStaticFieldID(gridImageViewClazz, "MODE_ERASE", "I");
    jint modeErase = env->GetStaticIntField(gridImageViewClazz, modeEraseId);
    return mode == modeErase;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cc_draw_view_GridImageView_setShowPath(JNIEnv *env, jobject thiz, jboolean b_show_path) {
    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "showPath", "Z");
    env->SetBooleanField(thiz, id, b_show_path);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_cc_draw_view_GridImageView_isShowPath(JNIEnv *env, jobject thiz) {
    jclass gridImageViewClazz = env->GetObjectClass(thiz);
    jfieldID id = env->GetFieldID(gridImageViewClazz, "showPath", "Z");
    return env->GetBooleanField(thiz, id);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_cc_draw_sdk_CCSdk_initial(JNIEnv *env, jobject thiz) {
    LOGD("CCSdk version: " VERSION);
}