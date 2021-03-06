#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_example_bankapplication2_MainActivity_baseUrlFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string baseURL = "NgK\u007FJo_6{#g?h<a={Vh@<f`7v*fDffb?s`FrB7Aps\\F>C73iHTEze";
    return env->NewStringUTF(baseURL.c_str());
}

extern "C"
jstring
Java_com_example_bankapplication2_AccountActivity_baseUrlFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string baseURL = "NgK\u007FJo_6{#g?h<a={Vh@<f`7v*fDffb?s`FrB7Aps\\F>C73iHTEze";
    return env->NewStringUTF(baseURL.c_str());
}

extern "C"
jstring
Java_com_example_bankapplication2_MainActivity_Masterkey(
        JNIEnv* env,
        jobject /* this */) {
    std::string Masterkey = "xTkClk4=zX;rglc?L)fu8lg@vViF=e`=xY:A:hhmxU8?o8bjx(fGi;d8u,l?9lgk";
    return env->NewStringUTF(Masterkey.c_str());
}

extern "C"
jstring
Java_com_example_bankapplication2_MainActivity_Filename(
        JNIEnv* env,
        jobject /* this */) {
    std::string filename = "'V:~LDEfTTDtdJI{";
    return env->NewStringUTF(filename.c_str());
}

extern "C"
jstring
Java_com_example_bankapplication2_AccountActivity_Filename(
        JNIEnv* env,
        jobject /* this */) {
    std::string filename = "'V:~LDEfJTKpdJI{";
    return env->NewStringUTF(filename.c_str());
}