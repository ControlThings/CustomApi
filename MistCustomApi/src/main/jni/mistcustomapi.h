//
// Created by jan on 1/9/17.
//

#ifndef CUSTOMAPI_MISTCUSTOMAPI_H
#define CUSTOMAPI_MISTCUSTOMAPI_H

#include <jni.h>

jobject get_MistApiBridge_instance(void);
JavaVM *get_javaVM(void);
bool is_connected(void);

jobject get_RequestInterfaceInstance(void);

#endif //CUSTOMAPI_MISTCUSTOMAPI_H
