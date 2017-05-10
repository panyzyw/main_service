# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

# 动态JNI入口
add_library(
entry1
STATIC
src/main/jni/source/entry1/main.cpp
)

# 静态JNI入口
add_library(
entry2
STATIC
src/main/jni/source/entry2/source/main.cpp
)

# 动态库
add_library(
function
SHARED
src/main/jni/source/function/function.cpp
src/main/jni/source/function/InputEvent.cpp
src/main/jni/source/function/media.cpp
)

# 静态库
add_library(
tools
STATIC
src/main/jni/source/tools/MyConfig.cpp
)

# 可执行程序
add_executable(
function_exe
src/main/jni/source/test/test.cpp
)


include_directories(
    src/main/jni/source/entry2/header
    src/main/jni/header
    src/main/jni/header/test
    src/main/jni/header/tools
    src/main/jni/header/function
    src/main/jni/prebuilt/ffmpeg/include
)

# 这是增加第三方预编译库
# add_library(
# alsa
# SHARED
# IMPORTED
# )
# set_target_properties(
# alsa
# PROPERTIES IMPORTED_LOCATION
# ./src/main/jni/prebuilt/alsa/${ANDROID_ABI}/libalsa.so
# )

# add_library(
# cae
# SHARED
# IMPORTED
# )
# set_target_properties(
# cae
# PROPERTIES IMPORTED_LOCATION
# ./src/main/jni/prebuilt/cae/${ANDROID_ABI}/libcae.so
# )



# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.
find_library(log-lib log)
find_library(z-lib z)
find_library(c-lib c)
find_library(utils-lib utils)
find_library(cutils-lib cutils)
find_library(nativehelper-lib nativehelper)


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries(entry1 ${log-lib} ${z-lib} ${c-lib})
target_link_libraries(tools ${log-lib} ${z-lib} ${c-lib})
target_link_libraries(
function
entry1
tools
${log-lib}
${z-lib}
${c-lib}
${utils-lib}
${cutils-lib}
${nativehelper-lib}
)
