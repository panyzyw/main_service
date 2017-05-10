#LOCAL_STATIC_LIBRARIES
#These are the static libraries that you want to include in your module. 
#Mostly, we use shared libraries, but there are a couple of places, 
#like executables in sbin and host executables where we use static libraries instead.

#LOCAL_WHOLE_STATIC_LIBRARIES
#These are the static libraries that you want to include in your module 
#without allowing the linker to remove dead code from them. 
#This is mostly useful if you want to add a static library to a shared library and 
#have the static library's content exposed from the shared library.

#LOCAL_LDLIBS
#系统动态库(如果动态和静态同时存在，优先动态库#-Lxxx -lyyy )
#链接的库不产生依赖关系，一般用于不需要重新编译的库，如库不存在，则会报错找不到。
#且貌似只能链接那些存在于系统目录下本模块需要连接的库。如果某一个库既有动态库又有静态库，
#那么在默认情况下是链接的动态库而非静态库。

#LOCAL_SHARED_LIBRARIES
#会生成依赖关系，当库不存在时会去编译这个库。

#LOCAL_STATIC_LIBRARIES
#会生成依赖关系，当库不存在时会去编译这个库。而且只编译进使用到的代码。

#LOCAL_WHOLE_STATIC_LIBRARIES
#会生成依赖关系，当库不存在时会去编译这个库。能将静态库所有的代码都加到编译结果中（不管用没用到，都编进去了）


#LOCAL_EXPORT_C_INCLUDES:=module_include
#这个主要用来输出当前模块的头文件所在路径,其他模块如果依赖它,就不需要指定它的头文件路径了.

#LOCAL_EXPORT_LDLIBS:= -lxxx -lyyy
#当用静态库生成动态库时,可以用此方法设置静态库的依赖环境,一般用法是:
#LOCAL_EXPORT_LDLIBS:=$(LOCAL_LDLIBS) 
#上面提到用LOCAL_LDLIBS设置链接路径的好处就在这里,可以直接导出库和库所在的路径.


#include $(PREBUILT_SHARED_LIBRARY)
#include $(PREBUILT_STATIC_LIBRARY)
#include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY) 
#include $(BUILD_EXECUTABLE)

include $(call all-subdir-makefiles)