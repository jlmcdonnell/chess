#include <jni.h>
#include <cstdio>
#include <algorithm>
#include <iostream>
#include "utils/numa.h"
#include "chess/board.h"
#include "utils/commandline.h"
#include "engine.h"
#include <android/log.h>

#define PARENT_WRITE_PIPE 0
#define PARENT_READ_PIPE 1
#define PARENT_ERROR_PIPE 2

#define READ_FD 0
#define WRITE_FD 1
#define ERROR_FD 2

#define PARENT_READ_FD (pipes[PARENT_READ_PIPE][READ_FD])
#define PARENT_WRITE_FD (pipes[PARENT_WRITE_PIPE][WRITE_FD])
#define PARENT_ERROR_FD (pipes[PARENT_ERROR_PIPE][ERROR_FD])

#define CHILD_READ_FD (pipes[PARENT_WRITE_PIPE][READ_FD])
#define CHILD_WRITE_FD (pipes[PARENT_READ_PIPE][WRITE_FD])
#define CHILD_ERROR_FD (pipes[PARENT_ERROR_PIPE][ERROR_FD])

#define TAG "Lc0Native"
#define debug(format, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, format, ##__VA_ARGS__)

int pipes[3][3];

jstring readFd(JNIEnv *env, int fd) {
    char buffer[1024];
    memset(buffer, 0, sizeof(buffer));

    int n = 0;
    while (read(fd, &buffer[n], 1) > 0) {
        if (buffer[n] == '\n') {
            break;
        }
        n++;
    }
    buffer[n] = '\0';

    debug("OUT: %s", buffer);

    if (strlen(buffer) == 0) {
        return nullptr;
    } else {
        return env->NewStringUTF(buffer);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_dev_mcd_chess_engine_lc0_Lc0JniImpl_main(JNIEnv *env, jobject thiz, jstring weightsPath) {
    using namespace lczero;

    debug("Lc0JniImpl_main");

    pipe(pipes[PARENT_READ_PIPE]);
    pipe(pipes[PARENT_WRITE_PIPE]);
    pipe(pipes[PARENT_ERROR_PIPE]);

    dup2(CHILD_READ_FD, STDIN_FILENO);
    dup2(CHILD_WRITE_FD, STDOUT_FILENO);
    dup2(CHILD_ERROR_FD, STDERR_FILENO);

    const char *weightsPathChars = env->GetStringUTFChars(weightsPath, 0);
    debug("Weights path: %s", weightsPathChars);
    std::string weightsPathString = "--weights=";
    weightsPathString += weightsPathChars;

    const char *argv[] = {
            "lc0",
            weightsPathString.c_str(),
    };
    try {
        Numa::Init();
        Numa::BindThread(0);
        InitializeMagicBitboards();
        CommandLine::RegisterMode("uci", "(default) Act as UCI engine");
        CommandLine::Init(2, argv);
        CommandLine::ConsumeCommand("uci");

        std::vector<std::string> args = CommandLine::Arguments();

        EngineLoop loop;
        debug("Running loop");
        loop.RunLoop();
        debug("loop exit");
    } catch (std::exception &e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

    close(CHILD_READ_FD);
    close(CHILD_WRITE_FD);
    close(CHILD_ERROR_FD);

    close(PARENT_READ_FD);
    close(PARENT_WRITE_FD);
    close(PARENT_ERROR_FD);

    debug("Lc0JniImpl_main done");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_dev_mcd_chess_engine_lc0_Lc0JniImpl_readLine(JNIEnv *env, jobject thiz) {
    return readFd(env, PARENT_READ_FD);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_dev_mcd_chess_engine_lc0_Lc0JniImpl_readError(JNIEnv *env, jobject thiz) {
    return readFd(env, PARENT_ERROR_FD);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_dev_mcd_chess_engine_lc0_Lc0JniImpl_write(JNIEnv *env, jobject thiz, jstring command) {
    debug("IN: %s", env->GetStringUTFChars(command, JNI_FALSE));
    const char *nativeString = env->GetStringUTFChars(command, JNI_FALSE);
    write(PARENT_WRITE_FD, nativeString, strlen(nativeString));
    return true;
}
