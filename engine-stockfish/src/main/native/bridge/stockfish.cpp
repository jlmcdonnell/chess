#include <cstdio>
#include <unistd.h>
#include <jni.h>
#include <android/log.h>

#include "../stockfish/src/bitboard.h"
#include "../stockfish/src/endgame.h"
#include "../stockfish/src/position.h"
#include "../stockfish/src/psqt.h"
#include "../stockfish/src/search.h"
#include "../stockfish/src/syzygy/tbprobe.h"
#include "../stockfish/src/thread.h"
#include "../stockfish/src/tt.h"
#include "../stockfish/src/uci.h"

#define TAG "StockfishNative"
#define PARENT_WRITE_PIPE 0
#define PARENT_READ_PIPE 1
#define READ_FD 0
#define WRITE_FD 1
#define PARENT_READ_FD (pipes[PARENT_READ_PIPE][READ_FD])
#define PARENT_WRITE_FD (pipes[PARENT_WRITE_PIPE][WRITE_FD])
#define CHILD_READ_FD (pipes[PARENT_WRITE_PIPE][READ_FD])
#define CHILD_WRITE_FD (pipes[PARENT_READ_PIPE][WRITE_FD])

#define debug(format, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, format, ##__VA_ARGS__)

int pipes[2][2];

extern "C"
JNIEXPORT void JNICALL
Java_dev_mcd_chess_engine_stockfish_data_AndroidStockfishJni_main(JNIEnv *env, jobject thiz, int threadCount) {
    pipe(pipes[PARENT_READ_PIPE]);
    pipe(pipes[PARENT_WRITE_PIPE]);

    dup2(CHILD_READ_FD, STDIN_FILENO);
    dup2(CHILD_WRITE_FD, STDOUT_FILENO);

    std::cout << Stockfish::engine_info() << std::endl;

    using namespace Stockfish;

    CommandLine::init();

    UCI::init(Options);
    Tune::init();
    PSQT::init();
    Bitboards::init();
    Position::init();
    Bitbases::init();
    Endgames::init();
    Threads.set(threadCount);
    Search::clear(); // After threads are up
    Eval::NNUE::init();
    UCI::loop();
    Threads.set(0);

    close(CHILD_READ_FD);
    close(CHILD_WRITE_FD);

    close(PARENT_READ_FD);
    close(PARENT_WRITE_FD);
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_dev_mcd_chess_engine_stockfish_data_AndroidStockfishJni_readLine(JNIEnv *env, jobject thiz) {
    char buffer[1024];
    memset(buffer, 0, sizeof(buffer));

    int n = 0;
    while (read(PARENT_READ_FD, &buffer[n], 1) > 0) {
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
JNIEXPORT jboolean JNICALL
Java_dev_mcd_chess_engine_stockfish_data_AndroidStockfishJni_write(JNIEnv *env, jobject /*thisz*/, jstring command) {
    debug("IN: %s", env->GetStringUTFChars(command, JNI_FALSE));

    const char *nativeString = env->GetStringUTFChars(command, JNI_FALSE);
    write(PARENT_WRITE_FD, nativeString, strlen(nativeString));

    return JNI_TRUE;
}
