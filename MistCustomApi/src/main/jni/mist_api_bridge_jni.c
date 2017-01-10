//
// Created by jan on 1/9/17.
//

#include <stddef.h>
#include <stdbool.h>

/* javah -classpath ../../../build/intermediates/classes/debug:/home/jan/Android/Sdk/platforms/android-16/android.jar -o request_interface.h mist.RequestInterface */
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

static bool connected = false;

bool is_connected(void) {
    return connected;
}

/*
 * Class:     mist_MistApiBridgeJni
 * Method:    register
 * Signature: (Lmist//MistApiBridge;)V
 */
JNIEXPORT void JNICALL Java_mist_MistApiBridgeJni_register (JNIEnv *env, jobject jthis, jobject mistApiBridge_localref) {
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
 * Class:     mist_MistApiBridgeJni
 * Method:    connected
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_mist_MistApiBridgeJni_connected(JNIEnv *env, jobject jthis, jboolean new_connected_status) {
    connected = new_connected_status;
    android_wish_printf("MistApiBridgeJni connected: %i", new_connected_status);
}



