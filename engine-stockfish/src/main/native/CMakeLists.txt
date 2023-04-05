set(CMAKE_CXX_STANDARD 17)
cmake_minimum_required(VERSION 3.22.1)
project(stockfish)

set(CMAKE_VERBOSE_MAKEFILE ON)
add_compile_definitions(NNUE_EMBEDDING_OFF)

file(GLOB_RECURSE stockfishCppPaths stockfish/src/*.cpp)
file(GLOB_RECURSE bridgeCppPaths bridge/*.cpp)

add_library(
        stockfish
        SHARED
        ${stockfishCppPaths}
        ${bridgeCppPaths}
)

target_link_libraries(stockfish log)
