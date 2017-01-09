//
// Created by jan on 1/9/17.
//

#include <stddef.h>
#include <stdbool.h>

/* javah -classpath ../../../build/intermediates/classes/debug:/home/jan/Android/Sdk/platforms/android-25/android.jar -o request_interface.h mist.customapi.RequestInterface */
#include "request_interface.h"
#include "mistcustomapi.h"
#include "jni_utils.h"


/*
 * Class:     mist_customapi_RequestInterface
 * Method:    jniWishApiRequest
 * Signature: (Ljava/lang/String;[BLmist/sandbox/Callback;)I
 */
JNIEXPORT jint JNICALL Java_mist_customapi_RequestInterface_jniWishApiRequest
  (JNIEnv *env, jobject jthis, jstring java_op, jbyteArray java_args_bson, jobject java_callback_localref) {
    int id = 0;

    jobject mistApiBridgeInstance = get_MistApiBridge_instance();
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
 * Class:     mist_customapi_RequestInterface
 * Method:    jniMistApiRequest
 * Signature: (Ljava/lang/String;[BLmist/sandbox/Callback;)I
 */
JNIEXPORT jint JNICALL Java_mist_customapi_RequestInterface_jniMistApiRequest
  (JNIEnv *env, jobject jthis, jstring java_op, jbyteArray java_args_bson, jobject java_callback_localref) {
    int id = 0;

    jobject mistApiBridgeInstance = get_MistApiBridge_instance();
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
 * Class:     mist_customapi_RequestInterface
 * Method:    jniMistApiRequestCancel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mist_customapi_RequestInterface_jniMistApiRequestCancel(JNIEnv *env, jobject jthis, jint rpc_id_to_cancel) {

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
