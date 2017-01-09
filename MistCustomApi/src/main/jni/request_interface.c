//
// Created by jan on 1/9/17.
//

#include <stddef.h>
#include <stdbool.h>


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


    return id;
}

/*
 * Class:     mist_customapi_RequestInterface
 * Method:    jniMistApiRequestCancel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mist_customapi_RequestInterface_jniMistApiRequestCancel
  (JNIEnv *env, jobject jthis, jint rpc_id_to_cancel) {

}
