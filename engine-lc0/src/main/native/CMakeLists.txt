set(CMAKE_CXX_STANDARD 17)
cmake_minimum_required(VERSION 3.22.1)
project(bridge)

set(CMAKE_VERBOSE_MAKEFILE ON)

file(GLOB_RECURSE bridgeCppPaths ${CMAKE_SOURCE_DIR}/bridge/*.cpp)

include_directories(${CMAKE_SOURCE_DIR}/lczero)

add_library(
        bridge
        SHARED
        ${bridgeCppPaths}
)

set(LEELA_LIB ${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}/liblc0_lib.so)

target_link_libraries(bridge ${LEELA_LIB} log)

