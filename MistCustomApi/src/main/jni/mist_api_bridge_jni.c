//
// Created by jan on 1/9/17.
//

#include <stddef.h>
#include <stdbool.h>

#include "mist_api_bridge_jni.h"
#include "jni_utils.h"

/* Global reference to the MistApiBridge class will be stored here by register() */
static jobject mistApiBridge_instance = NULL;

jobject get_MistApiBridge_instance(void) {
    return mistApiBridge_instance;
}

static JavaVM *javaVM = NULL;

JavaVM *get_javaVM(void) {
    return javaVM;
}

/*
 * Class:     mist_customapi_MistApiBridgeJni
 * Method:    register
 * Signature: (Lmist/customapi/MistApiBridge;)V
 */
JNIEXPORT void JNICALL Java_mist_customapi_MistApiBridgeJni_register (JNIEnv *env, jobject jthis, jobject mistApiBridge_localref) {
    android_wish_printf("register.");
    /* Register a refence to the JVM */
    if ((*env)->GetJavaVM(env,&javaVM) < 0) {
        android_wish_printf("Failed to GetJavaVM");
        return;
    }

    /* Create a global reference to the WishAppBridge instance here */
    mistApiBridge_instance = (*env)->NewGlobalRef(env, mistApiBridge_localref);
    if (mistApiBridge_instance  == NULL) {
        android_wish_printf("Out of memory!");
        return;
    }
}

/*
 * Class:     mist_customapi_MistApiBridgeJni
 * Method:    connected
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_mist_customapi_MistApiBridgeJni_connected(JNIEnv *env, jobject jthis, jboolean connected) {

}

