/**
 * Copyright (C) 2020, ControlThings Oy Ab
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * @license Apache-2.0
 */
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
