// Copyright (c) 2018 Isara Technologies. All Rights Reserved.

#include "NetworkTransceiverFunctions.h"
#include "NetworkTransceiverPrivatePCH.h"

#if PLATFORM_ANDROID

#include "Android/AndroidJNI.h"
#include "Android/AndroidApplication.h"

#define INIT_JAVA_METHOD(name, signature) \
if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true)) { \
	name = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, #name, signature, false); \
	check(name != NULL); \
} else { \
	check(0); \
}

#define DECLARE_JAVA_METHOD(name) \
static jmethodID name = NULL;

DECLARE_JAVA_METHOD(AndroidThunkJava_NetworkTransceiverSend);
DECLARE_JAVA_METHOD(AndroidThunkJava_NetworkTransceiverReceive);

void UNetworkTransceiverFunctions::InitJavaFunctions()
{
	// Same here, but we add the Java signature (the type of the parameters is between the parameters, and the return type is added at the end,
	// here the return type is V for "void")
	// More details here about Java signatures: http://www.rgagnon.com/javadetails/java-0286.html
	INIT_JAVA_METHOD(AndroidThunkJava_NetworkTransceiverSend, "([Ljava/lang/String;)V");
	INIT_JAVA_METHOD(AndroidThunkJava_NetworkTransceiverReceive, "([Ljava/lang/String;)V");
}
#undef DECLARE_JAVA_METHOD
#undef INIT_JAVA_METHOD

#endif

void UNetworkTransceiverFunctions::NetworkTransceiverSend(const TArray<float>& dataToSend)
{
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		UE_LOG(LogNetworkTransceiver, Warning, TEXT("LensCap NetworkTransceiver Send JNI method\n"));
		auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(dataToSend.Num(), FJavaWrapper::JavaStringClass, NULL));
		if (dataForJava)
		{
			for (uint32 Param = 0; Param < dataToSend.Num(); Param++)
			{
				auto StringValue = FJavaHelper::ToJavaString(Env, FString::SanitizeFloat(dataToSend[Param]));
				Env->SetObjectArrayElement(*dataForJava, Param, *StringValue);
			}

			// Execute the java code for this operation
			FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_NetworkTransceiverSend, *dataForJava);
		}
	}
#endif
}

void UNetworkTransceiverFunctions::NetworkTransceiverReceive(const TArray<float>& dataToReceive)
{
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		UE_LOG(LogNetworkTransceiver, Warning, TEXT("LensCap NetworkTransceiver Receive JNI method\n"));
		auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(dataToReceive.Num(), FJavaWrapper::JavaStringClass, NULL));
		if (dataForJava)
		{
			for (uint32 Param = 0; Param < dataToReceive.Num(); Param++)
			{
				auto StringValue = FJavaHelper::ToJavaString(Env, FString::SanitizeFloat(dataToReceive[Param]));
				Env->SetObjectArrayElement(*dataForJava, Param, *StringValue);
			}

			// Execute the java code for this operation
			FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_NetworkTransceiverReceive, *dataForJava);
		}
	}
#endif
}