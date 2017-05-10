#APP_STL := stlport_static
#APP_CPPFLAGS := -frtti -Wno-error=format-security -fsigned-char -Os $(CPPFLAGS)
#APP_OPTIM := release

#		C++ Exceptions |C++ RTTI |Standard Library
#system 	no 				no 			no 
#stlport 	no 				yes 		yes 
#gnustl  	yes 			yes 		yes

#system - 使用默认最小的C++运行库，这样生成的应用体积小，内存占用小，但部分功能将无法支持
#stlport_static - 使用STLport作为静态库，这项是Android开发网极力推荐的
#stlport_shared - 使用STLport作为动态库，这个可能产生兼容性和部分低版本的Android固件，目前不推荐使用。
#gnustl_static  - 使用GNU libstdc++ 作为静态库

#默认情况下STLPORT是不支持C++异常处理和RTTI，所以不要出现 -fexceptions 或 -frtti ，如果真的需要可以使用gnustl_static来支持标准C++的特性，但生成的文件体积会偏大，运行效率会低一些。
#支持C++异常处理，在Application.mk中加入 APP_CPPFLAGS += -fexceptions这句，同理支持RTTI，则加入APP_CPPFLAGS += -frtti，这里Android123再次提醒大家，第二条说的使用gnustl静态库，而不是stlport。
#强制重新编译 STLPort ，在Application.mk中加入 STLPORT_FORCE_REBUILD := true 可以强制重新编译STLPort源码，由于一些原因可能自己需要修改下STLPort库，一般普通的开发者无需使用此项。

#RTTI，运行时类型信息，代表你可以在运行时动态获取某个类型的信息。 

#  = 是最基本的赋值
# := 是覆盖之前的值 
# ?= 是如果没有被赋值过就赋予等号后面的值
# += 是添加等号后面的值

#添加宏有三种方式
#build_native.bat -- set NDK_DEBUG=1 CPPFLAGS+=-DCOCOS2D_DEBUG=1 CFLAGS+=-DMY_DEBUG=1
#Application.mk ---- APP_CPPFLAGS := -DCOCOS2D_DEBUG=1
#Android.mk -------- LOCAL_CFLAGS += -DMY_DEBUG

#ndk-build NDK_DEBUG=1    编译出的共享库带调试信息，是debug版本
#ndk-build NDK_DEBUG=0    编译出的共享库不带调试信息，是release版本
#如果没有指定NDK_DEBUG，则会从 AndroidManifest.xml中获取（查看 <application> 元素是否有 android:debuggable="true"）


#####################################################################################################
#C++要加下面两行 （覆盖原来的值）
#	 system          -> Use the default minimal system C++ runtime library.
#    gabi++_static   -> Use the GAbi++ runtime as a static library.
#    gabi++_shared   -> Use the GAbi++ runtime as a shared library.
#    stlport_static  -> Use the STLport runtime as a static library.
#    stlport_shared  -> Use the STLport runtime as a shared library.
#    gnustl_static   -> Use the GNU STL as a static library.
#    gnustl_shared   -> Use the GNU STL as a shared library.
#    c++_static      -> Use the LLVM libc++ as a static library.
#    c++_shared      -> Use the LLVM libc++ as a shared library.
# IMPORTANT: Defining APP_STL in Android.mk has no effect!
#
# 				C++       C++   Standard
#              Exceptions  RTTI    Library
#
#    system        no       no        no
#    gabi++       yes      yes        no
#    stlport      yes      yes       yes
#    gnustl       yes      yes       yes
#    libc++       yes      yes       yes
APP_STL := gnustl_static
#APP_STL := gnustl_shared

#A set of C compiler flags passed when compiling any C or C++ source code of any of the modules.
#-Werror把所有的警告都当作错误处理
APP_CFLAGS := -Wno-psabi -std=c11 #-Wno-error=format-security


#A set of C++ compiler flags passed when building C++ sources only.
#APP_CXXFLAGS An alias for APP_CPPFLAGS, to be considered obsolete as it may disappear in a future release of the NDK.
#-fpermissive 把代码的语法错误作为警告，并继续编译进程。
#-fno-rtti 禁止给类的虚函数产生运行时类型信息（RTTI）
APP_CPPFLAGS := -frtti -fexceptions -fsigned-char -Os -std=c++11 -Wno-literal-suffix -fpermissive


#IMPORTANT: This variable is provided here as a convenience to make it easier to transition to a newer version of the NDK. 
#It will be removed in a future revision. We thus encourage all developers to modify the module definitions properly instead of relying on it here.
#APP_GNUSTL_FORCE_CPP_FEATURES := exceptions rtti


#Build with LLVM Clang3.6
#NDK_TOOLCHAIN_VERSION=clang3.6
#
# Define this variable to either 4.8 or 4.9 to select version of the GCC compiler. 
# 4.8 is the default for 32-bit ABIs[32位手机？], and 
# 4.9 is the default for 64-bit ABIs[64位手机？]. 
# To select version of Clang, define this variable to clang3.4, clang3.5 or clang (which chooses the most recent version of clang).
# Build with ARM-Linux GCC4.9
NDK_TOOLCHAIN_VERSION=4.9


#APP_MODULES


#android-9之才有opengles的某些库（EGL）
#  android-3      -> Official Android 1.5 system images
#  android-4      -> Official Android 1.6 system images
#  android-5      -> Official Android 2.0 system images
#  android-6      -> Official Android 2.0.1 system images
#  android-7      -> Official Android 2.1 system images
#  android-8      -> Official Android 2.2 system images
#  android-9      -> Official Android 2.3 system images
#  android-14     -> Official Android 4.0 system images
#  android-18     -> Official Android 4.3 system images
# 32位的最低搜索库是这个参数定义的
# 64位的最低搜索库就android-21
APP_PLATFORM := android-16


#ARM指令版本
#################################################################################
# Name of the target CPU+ABI when this Android.mk is parsed
# You can specify one or more of the following values:
# armeabi        For ARMv5TE
# armeabi-v7a    For ARMv7			针对armv7-a及以上版本的cpu架构开启NEON优化是非常有必要的
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
#				other optional				Hard float when specified as armeabi-v7a-hard.
#											Incompatible with ARMv5, v6 devices.
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
APP_ABI := armeabi-v7a armeabi #arm64-v8a #x86 x86_64 #armeabi  mips arm64-v8a x86_64 mips64

#Set this variable to 'true' when your module has a very high number of sources and/or dependent static or shared libraries. 
#This forces the build system to use an intermediate list file, 
#and use it with the library archiver or static linker with the @$(listfile) syntax.
APP_SHORT_COMMANDS :=true

#编译入口确定
#APP_PROJECT_PATH This variable should give the absolute path to your Application's project root directory. 
#This is used to copy/install stripped versions of the generated JNI shared libraries to a specific location known to the APK-generating tools.
#Note that it is optional for $PROJECT/jni/Application.mk, but mandatory for $NDK/apps/<myapp/Application.mk
#
#By default, the NDK build system will look for a file named Android.mk under /jni
#If you want to override this behaviour, you can define APP_BUILD_SCRIPT to point to an alternate build script. 
#A non-absolute path will always be interpreted as relative to the NDK's top-level directory.
#APP_PROJECT_PATH := $(call my-dir)/../
#APP_BUILD_SCRIPT :=$(APP_PROJECT_PATH)jni/Android.mk
#APP_BUILD_SCRIPT := $(call my-dir)/Android.mk

#（追加新的值）（$取的值都是当ndk-build的参数传过来的）
ifeq ($(NDK_DEBUG),1)
  	APP_CFLAGS += $(CFLAGS) -DMY_DEBUG=1
  	APP_CPPFLAGS += $(CPPFLAGS)
	APP_OPTIM := debug
else
	APP_CFLAGS += -DNDEBUG -DMY_DEBUG=1
	APP_CPPFLAGS += -DNDEBUG
	APP_OPTIM := release
endif
 
#编译重力引擎
APP_CPPFLAGS +=-DCC_ENABLE_CHIPMUNK_INTEGRATION=1

#ndk-build NDK_DEBUG=0 is the equivalent of APP_OPTIM=release, and complies with the GCC -O2 option.
#ndk-build NDK_DEBUG=1 is the equivalent of APP_OPTIM=debug in Application.mk, and complies with the GCC -O0 option.

#Results of NDK_DEBUG (command line) and android:debuggable (manifest) combinations.
#                           NDK_DEBUG=0	                 NDK_DEBUG=1	                     NDK_DEBUG not specified
#android:debuggble="true"	Debug; Symbols; Optimized*1	 Debug; Symbols; Not optimized*2	 (same as NDK_DEBUG=1)
#android:debuggable="false"	Release; Symbols; Optimized	 Release; Symbols; Not optimized	 Release; No symbols; Optimized*3

#NDEBUG宏是Standard C中定义的宏，专门用来控制assert()的行为。如果定义了这个宏，则assert不会起作用。
#NDEBUG宏由于字面意思，也被用于作为判断debug/release版本的宏，不过这个是编译器、环境相关的，并不可靠。
#比如vc中，对生成的release版本项目，默认会定义这个宏，而gcc并没有定义，得用-DNDEBUG参数来定义。



