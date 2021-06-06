//
// Created by Meteor on 11/17/2020.
//

#include <jni.h>
#include <string>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <android/sharedmem.h>
#include <android/sharedmem_jni.h>
#include <android/log.h>

//extern "C" JNIEXPORT jstring JNICALL
//Java_com_example_myapplication_MainActivity_stringFromJNI(
//        JNIEnv* env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}
jbyteArray *mapfinal ;
int size;

#define ASHMEM_NAME_LEN         256
#define __ASHMEMIOC             0x77
#define ASHMEM_SET_NAME         _IOW(__ASHMEMIOC, 1, char[ASHMEM_NAME_LEN])
#define ASHMEM_SET_SIZE         _IOW(__ASHMEMIOC, 3, size_t)


struct memArea{
    jbyteArray *map;
    int fd;
    int size;
};

struct memArea maps[10];
int num = 0;
int arrayLength;

static jint getFD(JNIEnv *env, jclass cl, jstring path,jint size)
{
    const char *name = env->GetStringUTFChars(path,NULL);

    //jint fd = open("/dev/ashmem",O_RDWR);
    jint fd = ASharedMemory_create(name, size);
    //size_t memSize = ASharedMemory_getSize(fd);
    //char *buffer = (char *) mmap(NULL, memSize, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    //ioctl(fd,ASHMEM_SET_NAME,name);
    //ioctl(fd,ASHMEM_SET_SIZE,size);

    maps[num].size = size;
    maps[num].fd = fd;
    maps[num++].map = (jbyteArray *)mmap(0,size,PROT_READ|PROT_WRITE,MAP_SHARED,fd,0);
    //strcpy(buffer, "111");
    //ASharedMemory_setProt(fd, PROT_READ|PROT_WRITE);
    env->ReleaseStringUTFChars(path,name);

    return fd;


}

static void setmap(JNIEnv *env, jclass cl, jint fd, jint sz)
{
    size = sz;
    mapfinal = (jbyteArray *)mmap(0,size,PROT_READ|PROT_WRITE,MAP_SHARED,fd,0);
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 client-lib", "setmap done: %s", mapfinal);
    // __android_log_write(ANDROID_LOG_ERROR, "lenscap1",reinterpret_cast<const char *>(mapfinal[0]));
}

static void setNum(JNIEnv *env, jclass cl,jint fd, jobject input)
{
    jbyte *bytes = env->GetByteArrayElements(static_cast<jbyteArray>(input), 0);
    arrayLength = env->GetArrayLength(static_cast<jarray>(input));
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 setNum", "arraylength = %d", arrayLength);
    //char *chars = new char[arrayLength];
    //memset(chars, 0x0, arrayLength);
    //memcpy(chars, bytes, arrayLength);
    //[arrayLength] = 0;
    //maps[0].map = new char[arrayLength];
    //memset(chars, 0x0, arrayLength + 1);
    memset(maps[0].map, 0x0, arrayLength);
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 setNum", "pass memset", "1");
    memcpy(maps[0].map, bytes, arrayLength);
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 setNum", "pass memcpy", "1");
    //maps[0].map[arrayLength] = 0;
    env->ReleaseByteArrayElements(static_cast<jbyteArray>(input), bytes, 0);
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 setNum", "buf = %s", chars);
    //memcpy(maps[0].map, input, sizeof(input));
}
static jobject getNum(JNIEnv *env, jclass cl, int insize)
{
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 client-lib", "getNum start", "arrayLength");
    //jbyte *bytes = env->GetByteArrayElements(reinterpret_cast<jbyteArray>(mapfinal), 0);
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 client-lib", "GetByteArrayElements start", "arrayLength");
    //int arrayLength = env->GetArrayLength(reinterpret_cast<jarray>(mapfinal));
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 shim-lib", "getNum %s", mapfinal);
    jbyteArray jarray = env->NewByteArray(insize);
    env->SetByteArrayRegion(jarray, 0, insize, reinterpret_cast<const jbyte *>(mapfinal));
    return jarray;
}
/*
static jobject getNum(JNIEnv *env, jclass cl)
{
    //(ANDROID_LOG_ERROR, "lenscap1 getNum", "arrayLength = %s", maps[0].map);
    //int arrayLength = env->GetArrayLength();
    //(ANDROID_LOG_ERROR, "lenscap1 getNum", "arrayLength = %d", arrayLength);
    jbyteArray jarray = env->NewByteArray(arrayLength);
    env->SetByteArrayRegion(jarray, 0, arrayLength, reinterpret_cast<const jbyte *>(maps[0].map));
    //__android_log_print(ANDROID_LOG_ERROR, "lenscap1 readNum", "buf = %d", maps[0].map[0]);
    return jarray;
}

static jint setNum(JNIEnv *env, jclass cl,jint fd, jint pos,jint num)
{
    for(int i = 0; i < num; i++)
    {
        if(maps[i].fd == fd)
        {
            if(pos < (maps[i].size/ sizeof(int)))
            {
                maps[i].map[pos] = num;
                return 0;
            }
            return -1;
        }
    }
    return -1;
}
static jint getNum(JNIEnv *env, jclass cl,jint fd, jint pos)
{
    for(int i = 0; i < num; i++)
    {
        if(maps[i].fd == fd)
        {
            if(pos < (maps[i].size/ sizeof(int)))
            {
                return maps[i].map[pos];
            }
            return -1;
        }
    }
    return -1;
}
*/

static JNINativeMethod method_table[] = {
        { "setVal", "(ILjava/lang/Object;)V", (void *) setNum },
        //{ "setVal", "(III)I", (void *) setNum },
        //{ "getVal", "(II)I", (void *) getNum },
        { "setMap", "(II)V", (void *)setmap },
        { "getVal", "(I)Ljava/lang/Object;", (void *) getNum },
        { "getFD", "(Ljava/lang/String;I)I", (void *)getFD }

};


extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    } else {
        jclass clazz = env->FindClass("edu/ame/asu/meteor/lenscap/visualtransceiver/ShmLib");
        if (clazz) {
            jint ret = env->RegisterNatives(clazz, method_table, sizeof(method_table) / sizeof(method_table[0]));
            env->DeleteLocalRef(clazz);
            return ret == 0 ? JNI_VERSION_1_6 : JNI_ERR;
        } else {
            return JNI_ERR;
        }
    }
}

