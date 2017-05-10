/*
 * MyNoncopy.h
 *
 *  Created on: 2016年1月7日
 *      Author: zc
 */

#ifndef JNI_MYNONCOPY_H_
#define JNI_MYNONCOPY_H_

/**
 * 通常情况下, 要写一个单例类就要在类的声明把它们的
 * 【构造函数, 析构函数, 拷贝构造函数, 赋值函数】
 * 隐藏到private或者protected之中, 每个类都这么做麻烦.
 * 有noncopyable类, 只要让单例类直接继承noncopyable.
 *
 * class noncopyable的基本思想是把构造函数和析构函数设置protected权限，这样子类可以调用，但是外面的类不能调用，
 * 那么当子类需要定义构造函数的时候不至于通不过编译。但是最关键的是noncopyable把copy构造函数和copy赋值函数做成了
 * private，这就意味着除非子类定义自己的copy构造和赋值函数，否则在子类没有定义的情况下，外面的调用者是不能够通过
 * 赋值和copy构造等手段来产生一个新的子类对象的。
 *
 *
 * 底层程序入口及出口
 * 1. 声明引用时，必须同时对其进行初始化。
 * 2. 不能建立数组的引用。因为数组是一个由若干个元素所组成的集合，所以无法建立一个数组的别名。
 *
 * 引用：
 *	MyClass& x = func();
 *	这个只是将引用指向func()返回的变量。
 *
 * 对象：
 *	分两种情况：
 *	MyClass x = func();
 *	这种情况调用的是MyClass的拷贝构造函数给x赋的值。
 *	MyClass x;
 * 	x = func();
 *	这种情况调用的是MyClass的operator=给x赋的值。
 */
class MyNoncopy {
protected:
	MyNoncopy() {
	}
	~MyNoncopy() {
	}
private:
	// emphasize the following members are private
	MyNoncopy(const MyNoncopy&);
	/**
	 * 当我们写一个类的时候, 正常情况下不需要为该类重载=运算符, 因为系统为每个类提供了默认的赋值运算符, 该操作会把这个类的所有数据成员都进行一次=的操作进行赋值
	 * 然而，当有指针的时候就不能这么做了，否则两个对象的指针指向同一块地址，释放的时候造成了重复释放，出错了。
	 * 所以要么不用，要么慎用！
	 */
	const MyNoncopy& operator=(const MyNoncopy&);
};


#endif /* JNI_MYNONCOPY_H_ */
