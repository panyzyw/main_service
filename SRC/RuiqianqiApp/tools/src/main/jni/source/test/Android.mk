# An Android.mk file must begin with the definition of the LOCAL_PATH variable. 
# It is used to locate source files in the development tree. In this example, the macro function 'my-dir',
# provided by the build system, is used to return the path of the current directory 
# (i.e. the directory containing the Android.mk file itself).
LOCAL_PATH := $(call my-dir)


# The CLEAR_VARS variable is provided by the build system and points to a special GNU Makefile 
# that will clear many LOCAL_XXX variables for you (e.g. LOCAL_MODULE, LOCAL_SRC_FILES, LOCAL_STATIC_LIBRARIES, etc...), 
# with the exception of LOCAL_PATH. This is needed because all build control files are parsed 
# in a single GNU Make execution context where all variables are global.
include $(CLEAR_VARS)


LOCAL_MODULE := test_exe
#This variable is optional, and allows you to redefine the name of generated files. 
# By default, module <foo> will always generate a static library named lib<foo>.a or
# a shared library named lib<foo>.so, which are standard Unix conventions.
# You can override this by defining LOCAL_MODULE_FILENAME, For example:
# LOCAL_MODULE := foo-version-1
# LOCAL_MODULE_FILENAME := libfoo
LOCAL_MODULE_FILENAME := test_exe


# 自定义变量的使用
ZCCL_SRC_PATH := $(LOCAL_PATH)
# wildcard : 扩展通配符，用于查找一个目录下的所有符合条件的文件
ZCCL_CPP_LIST :=$(wildcard $(ZCCL_SRC_PATH)/*.cpp)
ZCCL_CPP_LIST +=$(wildcard $(ZCCL_SRC_PATH)/*.c)
#ZCCL_CPP_LIST +=$(wildcard $(ZCCL_SRC_PATH)/*.s)


# 编译源文件列表
LOCAL_SRC_FILES := $(ZCCL_CPP_LIST:$(LOCAL_PATH)/%=%)
#$(warning $(LOCAL_SRC_FILES))
#$(info $(LOCAL_SRC_FILES))


# 编译相关的头文件目录
LOCAL_C_INCLUDES := $(LOCAL_PATH) \
                    $(LOCAL_PATH)/../../header \
                    $(LOCAL_PATH)/../../header/test

# C和C++共用的
LOCAL_CFLAGS += -std=c11

# 仅配置C++相关特性
LOCAL_CPPFLAGS := -std=c++11 -frtti -fexceptions

# 配置一些宏的
ifeq ($(NDK_DEBUG),1)
	# An optional set of compiler flags that will be passed when building C and C++ source files.
	# C和C++共用的
  	LOCAL_CFLAGS +=  $(CFLAGS)
	# An optional set of compiler flags that will be passed when building C++ source files only.
	# 仅仅给C++用的
	LOCAL_CPPFLAGS += $(CPPFLAGS)
	# An alias for LOCAL_CPPFLAGS. Note that use of this flag is obsolete as it may disappear in future releases of the NDK.
  	LOCAL_CXXFLAGS += $(CPPFLAGS)
endif

# 编译静态库及二进制可用（so建议使用LOCAL_WHOLE_STATIC_LIBRARIES）
# The list of static libraries modules that the current module depends on.
# If the current module is a shared library or an executable, 
# this will force these libraries to be linked into the resulting binary.
#
# If the current module is a static library, this simply tells that another other module 
# that depends on the current one will also depend on the listed libraries.
#LOCAL_STATIC_LIBRARIES :=libfunc


# 将全部静态库加到动态库中，编译动态库使用LOCAL_WHOLE_STATIC_LIBRARIES）
# This is generally useful when there are circular dependencies between several static libraries. 
# Note that when used to build a shared library, this will force all object files from your whole static libraries
# to be added to the final binary. This is not true when generating executables though.
#LOCAL_WHOLE_STATIC_LIBRARIES :=libfunc


# 会生成依赖关系，当库不存在时会去编译这个库。
# The list of shared libraries modules this module depends on at runtime. 
# This is necessary at link time and to embed the corresponding information in the generated file.
#LOCAL_SHARED_LIBRARIES :=func


#系统动态库(如果动态和静态同时存在，优先动态库#-Lxxx -lyyy )
#链接的库不产生依赖关系，一般用于不需要重新编译的库，如库不存在，则会报错找不到。
#且貌似只能链接那些存在于系统目录下本模块需要连接的库。如果某一个库既有动态库又有静态库，那么在默认情况下是链接的动态库而非静态库。
#
# The list of additional linker flags to be used when building your shared library or executable. 
# This is useful to pass the name of specific system libraries with the '-l' prefix. 
# For example, the following will tell the linker to generate a module that links to /system/lib/libz.so at load time:
# NOTE:This is ignored for static libraries, and ndk-build will print a warning if you define it in such a module.
# is always ignored for static libraries
# is always ignored for static libraries
# is always ignored for static libraries
# 因为编译静态库，又不能把其他的静态库包含到本库中，如果这个是链接到动态库呢，一般没这种用的
LOCAL_LDLIBS := -llog -ldl -lm -lz


# The list of other linker flags to be used when building your shared library or executable. 
# For example, the following will use the ld.bfd linker on ARM/X86 GCC 4.6+ where ld.gold is the default
# NOTE: This is ignored for static libraries, and ndk-build will print a warning if you define it in such a module.
#LOCAL_LDFLAGS += -fuse-ld=bfd

############################################################################################################
#Define this variable to record a set of C/C++ compiler flags that will be added to the LOCAL_CFLAGS definition of any other module 
#that uses this one with LOCAL_STATIC_LIBRARIES or LOCAL_SHARED_LIBRARIES.
#
#For example, consider the module 'foo' with the following definition:
#          include $(CLEAR_VARS)
#          LOCAL_MODULE := foo
#          LOCAL_SRC_FILES := foo/foo.c
#          LOCAL_EXPORT_CFLAGS := -DFOO=1
#          include $(BUILD_STATIC_LIBRARY)
#
#And another module, named 'bar' that depends on it as:
#          include $(CLEAR_VARS)
#          LOCAL_MODULE := bar
#          LOCAL_SRC_FILES := bar.c
#          LOCAL_CFLAGS := -DBAR=2
#          LOCAL_STATIC_LIBRARIES := foo
#          include $(BUILD_SHARED_LIBRARY)
#Then, the flags '-DFOO=1 -DBAR=2' will be passed to the compiler when building bar.c.
#
#Exported flags are prepended to your module's LOCAL_CFLAGS so you can easily override them. 
#They are also transitive: if 'zoo' depends on 'bar' which depends on 'foo', then 'zoo' will also inherit all flags exported by 'foo'.
#
#Finally, exported flags are not used when building the module that exports them.[导出的东西，在编译自己的时候是没有用的]
#In the above example, -DFOO=1 would not be passed to the compiler when building foo/foo.c.
#############################################################################################################
### LOCAL_EXPORT_CFLAGS

#Same as LOCAL_EXPORT_CFLAGS, but for C++ flags only.
### LOCAL_EXPORT_CPPFLAGS

#Same as LOCAL_EXPORT_CFLAGS, but for C include paths. This can be useful if 'bar.c' wants to include headers that are provided by module 'foo'.
### LOCAL_EXPORT_C_INCLUDES

#Same as LOCAL_EXPORT_CFLAGS, but for linker flags.
### LOCAL_EXPORT_LDFLAGS

#Same as LOCAL_EXPORT_CFLAGS, but for passing the name of specific system libraries with the '-l' prefix. 
#Note that the imported linker flags will be appended to your module's LOCAL_LDLIBS
### LOCAL_EXPORT_LDLIBS

 
# C++相关特性
# This is an optional variable that can be defined to indicate the file extension(s) of C++ source files. 
# They must begin with a dot. The default is '.cpp' but you can change it.
LOCAL_CPP_EXTENSION := .cxx .cpp .cc
# This is an optional variable that can be defined to indicate that your code relies on specific C++ features. 
# To indicate that your code uses RTTI (RunTime Type Information)
# To indicate that your code uses C++ exceptions
# It is recommended to use this variable instead of enabling -frtti and -fexceptions directly in your LOCAL_CPPFLAGS definition.
LOCAL_CPP_FEATURES := rtti exceptions


# By default, any undefined reference encountered when trying to build a shared library will result in an "undefined symbol" error. 
# This is a great help to catch bugs in your source code.
# However, if for some reason you need to disable this check, set this variable to 'true'. 
# Note that the corresponding shared library may fail to load at runtime.
# NOTE: This is ignored for static libraries, and ndk-build will print a warning if you define it in such a module.
LOCAL_ALLOW_UNDEFINED_SYMBOLS :=true


#NDK在编译的时候默认生成的是thumb/thumb2指令
#这个加上了会大一些 这样再生成的指令就是ARM指令了
# By default, ARM target binaries are generated in 'thumb' mode, where each instruction are 16-bit wide, and linked with /thumb STL libraries. 
# You can define this variable to 'arm' if you want to force the generation of the module's object files in 'arm' (32-bit instructions) mode.
#
# Note that you can also instruct the build system to only build specific sources in ARM mode 
# by appending an '.arm' suffix to its source file name. For example, with:
# LOCAL_SRC_FILES := foo.c bar.c.arm
# Tells the build system to always compile 'bar.c' in ARM mode, 
# and to build foo.c according to the value of LOCAL_ARM_MODE.
#
# NOTE: Setting APP_OPTIM to 'debug' in your Application.mk will also force the generation of ARM binaries as well. 
# This is due to bugs in the toolchain debugger that don't deal too well with thumb code. 
LOCAL_ARM_MODE :=arm


# 采用NEON优化技术   (仅支持armeabi-v7a)
# NEON support is only possible for armeabi-v7a ABI
# its variant armeabi-v7a-hard and x86 ABI
# Defining this variable to 'true' allows the use of ARM Advanced SIMD (a.k.a. NEON) GCC intrinsics in your C and C++ sources, as well as NEON instructions in Assembly files.
#
# You should only define it when targeting the 'armeabi-v7a' ABI that corresponds to the ARMv7 instruction set. 
# Note that not all ARMv7 based CPUs support the NEON instruction set extensions and that you should perform runtime detection to be able to use this code at runtime safely. 
# To learn more about this, please read the documentation at Android NDK & ARM NEON Instruction Set Extension Support and Android NDK CPU Features detection library.
#
# Alternatively, you can also specify that only specific source files may be compiled with NEON support by using the '.neon' suffix, as in:
# LOCAL_SRC_FILES = foo.c.neon bar.c zoo.c.arm.neon
# In this example, 'foo.c' will be compiled in thumb+neon mode, 'bar.c' will be compiled in 'thumb' mode, and 'zoo.c' will be compiled in 'arm+neon' mode.
#
# Note that the '.neon' suffix must appear after the '.arm' suffix if you use both (i.e. foo.c.arm.neon works, but not foo.c.neon.arm !)
#TARGET_ARCH=(arm x86 mips arm64)
ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
LOCAL_ARM_NEON :=true
else 

endif


#################################################################################
# Name of the target CPU+ABI when this Android.mk is parsed
# You can specify one or more of the following values:
# armeabi        For ARMv5TE
# armeabi-v7a    For ARMv7
# arm64-v8a      For ARMv8 AArch64
# x86       	 For i686
# x86_64    	 For x86-64
# mips      	 For mips32 (r1)
# mips64    	 For mips64 (r6)
################################################################################
# 				Instruction Set(s)			Notes
# armeabi		ARMV5TE and later
#				Thumb-1						No hard float.
#			
# armeabi-v7a	armeabi 
#				Thumb-2 
#				VFPv3-D16 
#				other optional				Hard float when specified as armeabi-v7a-hard.Incompatible with ARMv5, v6 devices.
#			
# arm64-v8a		AArch-64	
#
# x86			x86 (IA-32) MMX
#				SSE/2/3
#				SSSE3						No support for MOVBE or SSE4.
#			
# x86_64		x86-64
#				MMX
#				SSE/2/3
#				SSSE3
#				SSE4.1, 4.2
#				POPCNT	
#			
# mips			MIPS32r1 and later			Hard float.
#
# mips 64		MIPS64r6	
###############################################################################
#TARGET_ARCH_ABI :=armeabi #armeabi-v7a


#Name of the target Android platform when this Android.mk is parsed
#TARGET_PLATFORM :=android-9


# The concatenation of target platform and ABI, it really is defined as - and is useful when you want to test against a specific target system image for a real device.
# By default, this will be 'android-3-armeabi'
# (Up to Android NDK 1.6_r1, this used to be 'android-3-arm' by default)
#TARGET_ABI :=android-9-arm


#include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_EXECUTABLE)
#include $(PREBUILT_SHARED_LIBRARY)
#include $(PREBUILT_STATIC_LIBRARY)


#LOCAL_STATIC_LIBRARIES（编译静态库及二进制可用）
#These are the static libraries that you want to include in your module. 
#Mostly, we use shared libraries, but there are a couple of places, 
#like executables in sbin and host executables where we use static libraries instead.

#LOCAL_WHOLE_STATIC_LIBRARIES（编译动态库可用）
#These are the static libraries that you want to include in your module 
#without allowing the linker to remove dead code from them. 
#This is mostly useful if you want to add a static library to a shared library and 
#have the static library's content exposed from the shared library.

