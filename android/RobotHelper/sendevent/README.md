### 这是啥？

使用go语言重新实现了android的sendevent工具

#### 与安卓自带的sendevent有啥不同？

原本sendevent每次调用都会重新开一个进程，然后重新打开一次文件，从而造成很大的资源浪费。

这个版本会一直等待输入，不会关闭进程，也不会关闭文件


### 实现原理

这个文件参考了 [https://android.googlesource.com/platform/system/core/+/android-5.0.2_r3/toolbox/sendevent.c](https://android.googlesource.com/platform/system/core/+/android-5.0.2_r3/toolbox/sendevent.c) 这个是android sendevent工具源码
