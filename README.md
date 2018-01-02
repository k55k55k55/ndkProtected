# 前言
很多后台服务很容被系统干掉，尤其红米类型的手机，它们可能是为了省电一些服务它们会自动消除掉。试了很多种方法，不如提高优先级什么的都没有太大的效果，这个ndk双进程守护相对好一点，但是也并不是多么的保险，仅供参考。
上一篇介绍AS下的ndk[Android-studio的ndk](http://blog.csdn.net/k393393/article/details/78954115)
# 正文
开启服务
```
Intent intent = new Intent(this, LittleWidgetService.class);
        startService(intent);
```
定义进程的守护方法
```
public class LittleWidgetWatcher {
    static {
        System.loadLibrary("daoshuriwidget");
    }


    public native void openLittleWidgetWatcher(int uId);
}
```
使用sigaction做信号处理，将信号处理的方法交给sig_handler处理的，当子进程挂了的时候会向其父进程发送一个SIGCHLD信号，父进程就会收到SIGCHLD信号，并且开始执行sig_handler方法，重生成子进程。
```
JNIEXPORT void JNICALL
Java_com_fanao_daoshuri_ndkprotect_LittleWidgetWatcher_openLittleWidgetWatcher(JNIEnv *env,jobject instance, jint userId) {
//父进程 也就是小插件的进程
     user_id = userId;
/* sigaction用于信号处理，sa.sa_flags=SA_RESTART：使被信号打断的系统调用自动重新发起
 信号处理交给sig_handler处理的，当子进程挂了的时候会向其父进程发送一个SIGCHLD信号，
 父进程就会收到SIGCHLD信号，并且开始执行sig_handler方法，重生成子进程*/
     LOGI("开启进程\n");
     struct sigaction sa;
     sa.sa_flags = 0;
     sa.sa_handler = sig_handler;
     sigaction(SIGCHLD, &sa, NULL);
     create_child();
}
```
fork出子进程
```
void create_child() {
/*    一个进程调用fork（）函数后，系统先给新的进程分配资源，例如存储数据和代码的空间。
    然后把原来的进程的所有值都复制到新的新进程中，只有少数值与原来的进程的值不同。
    相当于克隆了一个自己。
    pid==0 说明是子进程，pid>是父进程
    */
    pid_t pid = fork();
    if (pid < 0) {

    } else if (pid > 0) {
        //父进程
        LOGI("fork的父进程 %d",pid);
    } else if (pid == 0) {
        LOGI("fork的子进程开启 ");
        // 开启子线程
        child_start_monitor();
    }

}
```
开启子进程，让它不断监听父进程是否挂掉
```
void child_start_monitor() {
    pthread_t tid;

/*    原型：int  pthread_create（（pthread_t  *thread,  pthread_attr_t  *attr,  void  *（*start_routine）（void  *）,  void  *arg）
    用法：#include  <pthread.h>
    功能：创建线程（实际上就是确定调用该线程函数的入口点），在线程创建以后，就开始运行相关的线程函数。
    说明：thread：线程标识符；
    attr：线程属性设置；
    start_routine：线程函数的起始地址；
    arg：传递给start_routine的参数；
    返回值：成功，返回0；出错，返回-1。*/
    pthread_create(&tid, NULL, thread_rt, NULL);
}
```
如果父进程挂掉后就通过调用am.startservice重新开启服务,要制定服务的进程id
```
//指针函数，返回值为指针的函数，类似于Java的run方法
void *thread_rt(void *data) {
    pid_t pid;
    //getppid获取父进程的id  getpid获取自己的id
    //子进程反复的 循环在判断父进程是否被干掉，如果被干掉就说明小插件的服务被干掉了
    while ((pid = getppid()) != 1) {
        sleep(20);
        LOGI("循环 %d ", pid);
    }
    // 如果父进程被干掉了，那么它的子进程就会被init托管，而这个进程的id就是1
    LOGI("重启父进程");
    //调用am.startservice重新开启服务 要制定服务的进程id
    // execlp("am", "am", "startservice", "--user", user_id,
    //     "com.pybeta.daymatter.service/com.pybeta.daymatter.service.AutoRefreshService",
    //      (char *) NULL);
    execlp("am", "am", "startservice", "--user", user_id,
           "com.fanao.daoshuri.ndkprotect/com.fanao.daoshuri.ndkprotect.LittleWidgetService",
           (char *) NULL);
}
