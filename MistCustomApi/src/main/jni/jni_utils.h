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
// Created by jan on 6/7/16.
//

#ifndef WISH_JNI_UTILS_H
#define WISH_JNI_UTILS_H

int android_wish_printf(const char *format, ...);
int android_wish_vprintf(const char *format, va_list arg_list);

int getJNIEnv(JavaVM *vm, JNIEnv **result_env, bool * didAttach);
int detachThread();

void check_and_report_exception(JNIEnv *env);

#endif //WISH_JNI_UTILS_H
