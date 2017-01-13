//
// Created by jan on 1/9/17.
//

#include <stddef.h>
#include <stdbool.h>

/* javah -classpath ../../../build/intermediates/classes/debug:/home/jan/Android/Sdk/platforms/android-25/android.jar -o request_interface.h mist.RequestInterface */
#include "request_interface.h"
#include "mistcustomapi.h"
#include "jni_utils.h"

static JavaVM *javaVM = NULL;
static jobject requestInterfaceInstance = NULL;

jobject get_RequestInterfaceInstance(void) {
    return requestInterfaceInstance;
}


/*
 * Class:     mist_RequestInterface
 * Method:    jniWishApiRequest
 * Signature: (Ljava/lang/String;[BLmist/sandbox/Callback;)I
 */
JNIEXPORT jint JNICALL Java_mist_RequestInterface_jniWishApiRequest
  (JNIEnv *env, jobject jthis, jstring java_op, jbyteArray java_args_bson, jobject java_callback_localref) {

    if (!is_connected()) {
        android_wish_printf("Not connected!");
        return 0;
    }

    int id = 0;

    jobject mistApiBridgeInstance = get_MistApiBridge_instance();
    if (mistApiBridgeInstance == NULL) {
        android_wish_printf("Error: mistApiBridgeInstance is null");
        return 0;
    }
    jclass mistApiBridgeClass = (*env)->GetObjectClass(env, mistApiBridgeInstance);
    if (mistApiBridgeClass == NULL) {
        android_wish_printf("Cannot get MistApiBridge class");
        return 0;
    }

    jmethodID wishApiRequestMethodId = (*env)->GetMethodID(env, mistApiBridgeClass, "wishApiRequest", "(Ljava/lang/String;[BLmist/sandbox/Callback;)I");
    if (wishApiRequestMethodId == NULL) {
        android_wish_printf("Cannot get wishApiRequest method");
        return 0;
    }

    id = (*env)->CallIntMethod(env, mistApiBridgeInstance, wishApiRequestMethodId, java_op, java_args_bson, java_callback_localref);
    check_and_report_exception(env);

    return id;
}

/*
 * Class:     mist_RequestInterface
 * Method:    jniMistApiRequest
 * Signature: (Ljava/lang/String;[BLmist/sandbox/Callback;)I
 */
JNIEXPORT jint JNICALL Java_mist_RequestInterface_jniMistApiRequest(JNIEnv *env, jobject jthis, jstring java_op, jbyteArray java_args_bson, jobject java_callback_localref) {

    if (!is_connected()) {
      android_wish_printf("Not connected!");
      return 0;
    }

    int id = 0;

    jobject mistApiBridgeInstance = get_MistApiBridge_instance();
    if (mistApiBridgeInstance == NULL) {
        android_wish_printf("Error: mistApiBridgeInstance is null");
        return 0;
    }
    jclass mistApiBridgeClass = (*env)->GetObjectClass(env, mistApiBridgeInstance);
    if (mistApiBridgeClass == NULL) {
        android_wish_printf("Cannot get MistApiBridge class");
        return 0;
    }

    jmethodID mistApiRequestMethodId = (*env)->GetMethodID(env, mistApiBridgeClass, "mistApiRequest", "(Ljava/lang/String;[BLmist/sandbox/Callback;)I");
    if (mistApiRequestMethodId == NULL) {
        android_wish_printf("Cannot get mistApiRequest method");
        return 0;
    }

    id = (*env)->CallIntMethod(env, mistApiBridgeInstance, mistApiRequestMethodId, java_op, java_args_bson, java_callback_localref);
    check_and_report_exception(env);

    return id;
}

/*
 * Class:     mist_RequestInterface
 * Method:    jniMistApiRequestCancel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mist_RequestInterface_jniMistApiRequestCancel(JNIEnv *env, jobject jthis, jint rpc_id_to_cancel) {

    if (!is_connected()) {
      android_wish_printf("Not connected!");
      return;
    }

    jobject mistApiBridgeInstance = get_MistApiBridge_instance();
    jclass mistApiBridgeClass = (*env)->GetObjectClass(env, mistApiBridgeInstance);
    if (mistApiBridgeClass == NULL) {
        android_wish_printf("Cannot get MistApiBridge class");
        return;
    }

    jmethodID wishApiRequestMethodId = (*env)->GetMethodID(env, mistApiBridgeClass, "mistApiCancel", "(I)V");
    if (wishApiRequestMethodId == NULL) {
        android_wish_printf("Cannot get mistApiCancel method");
        return;
    }

    (*env)->CallVoidMethod(env, mistApiBridgeInstance, wishApiRequestMethodId, rpc_id_to_cancel);
    check_and_report_exception(env);
}

/*
 * Class:     mist_RequestInterface
 * Method:    isConnected
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_mist_RequestInterface_isConnected(JNIEnv *env, jobject jthis) {
    jboolean ret = is_connected() ? JNI_TRUE : JNI_FALSE;
    return ret;
}

/*
 * Class:     mist_RequestInterface
 * Method:    registerInstance
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mist_RequestInterface_registerInstance(JNIEnv *env, jobject jthis) {
    /* Register a refence to the JVM */
    if ((*env)->GetJavaVM(env,&javaVM) < 0) {
        android_wish_printf("Failed to GetJavaVM");
        return;
    }

    requestInterfaceInstance = (*env)->NewGlobalRef(env, jthis);
}