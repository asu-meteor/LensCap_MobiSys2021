// Copyright (c) 2018 Isara Technologies. All Rights Reserved.

#include "VisualTransceiverFunctions.h"
#include "VisualTransceiverPrivatePCH.h"
#include "ARBlueprintLibrary.h"
#include "GoogleARCoreFunctionLibrary.h"
#include "GoogleARCoreCameraImage.h"
#include "GoogleARCoreTypes.h"
#include "GoogleARCoreAugmentedImageDatabase.h"
#include "GoogleARCoreFunctionLibrary.h"
#include "RSA.h"


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

DECLARE_JAVA_METHOD(AndroidThunkJava_VisualTransceiverSendArrayFloat);
//DECLARE_JAVA_METHOD(AndroidThunkJava_VisualTransceiverSendArrayByte);
DECLARE_JAVA_METHOD(AndroidThunkJava_VisualTransceiverSendArrayInt);
DECLARE_JAVA_METHOD(AndroidThunkJava_VisualTransceiverSendArrayString);
DECLARE_JAVA_METHOD(AndroidThunkJava_VisualTransceiverReceiveFloat);
DECLARE_JAVA_METHOD(AndroidThunkJava_VisualTransceiverReceiveArrayFloat);
DECLARE_JAVA_METHOD(AndroidThunkJava_GetLensCapCountAndroid);
DECLARE_JAVA_METHOD(AndroidThunkJava_LensCapCheckPermission);
DECLARE_JAVA_METHOD(AndroidThunkJava_LensCapWriteFPS);

void UVisualTransceiverFunctions::InitJavaFunctions()
{
	// Same here, but we add the Java signature (the type of the parameters is between the parameters, and the return type is added at the end,
	// here the return type is V for "void")
	// More details here about Java signatures: http://www.rgagnon.com/javadetails/java-0286.html
	INIT_JAVA_METHOD(AndroidThunkJava_VisualTransceiverSendArrayFloat, "([Ljava/lang/Float;Ljava/lang/String;)V");
	//INIT_JAVA_METHOD(AndroidThunkJava_VisualTransceiverSendArrayByte, "([Ljava/lang/Byte;Ljava/lang/String;)V");
	INIT_JAVA_METHOD(AndroidThunkJava_VisualTransceiverSendArrayInt, "([Ljava/lang/Integer;Ljava/lang/String;)V");
	INIT_JAVA_METHOD(AndroidThunkJava_VisualTransceiverSendArrayString, "([Ljava/lang/String;Ljava/lang/String;)V");
	INIT_JAVA_METHOD(AndroidThunkJava_VisualTransceiverReceiveFloat, "()F");
	INIT_JAVA_METHOD(AndroidThunkJava_VisualTransceiverReceiveArrayFloat, "()[F");
	INIT_JAVA_METHOD(AndroidThunkJava_GetLensCapCountAndroid, "([Ljava/lang/Integer;)V");
	INIT_JAVA_METHOD(AndroidThunkJava_LensCapCheckPermission, "(Ljava/lang/String;)Z");
	INIT_JAVA_METHOD(AndroidThunkJava_LensCapWriteFPS, "([Ljava/lang/Float;)V");
	//INIT_JAVA_METHOD(AndroidThunkJava_GetLensCapCount, "(Ljava/lang/Integer;)V");
}
#undef DECLARE_JAVA_METHOD
#undef INIT_JAVA_METHOD

#endif

FLensCapType lens;
FRSA fsra;
static const int SobelThreshold = 128 * 128;
TArray<float> lenscap_face_data;
TArray<float> lenscap_image_data;
TArray<float> lenscap_pose_data;
TArray<float> lenscap_pointCloud_data;
TArray<float> lenscap_light_data;
TArray<float> lenscap_PassthroughCameraImageUV_data;
TArray<float> lenscap_planes_data;
TArray<float> lenscap_points_data;
TArray<int32> lenscap_trackingstate_data;
TArray<int32> lenscap_cameraimageintrinsics_data;
TArray<int32> lenscap_cameratextureintrinsics_data;
TArray<float> lenscap_transformarcoordinates2d;

bool lenscap_cameraconfig;
bool lenscap_linetrace;


void UVisualTransceiverFunctions::VisualTransceiverSendArrayFloat(const TArray<float>& dataToSend, const FString& TagValue)
{
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		//UE_LOG(LogVisualTransceiver, Warning, TEXT("LensCap VisualTransceiver Send Float JNI method\n"));
		jclass floatClass = Env->FindClass("java/lang/Float");
		auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(dataToSend.Num(), floatClass, NULL));
		jmethodID floatConstructor = Env->GetMethodID(floatClass, "<init>", "(F)V");
		//auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(dataToSend.Num(), FJavaWrapper::JavaStringClass, NULL));
		if (dataForJava)
		{
			for (uint32 Param = 0; Param < dataToSend.Num(); Param++)
			{
				//	auto StringValue = FJavaHelper::ToJavaString(Env, FString::SanitizeFloat(dataToSend[Param]));
				//	Env->SetObjectArrayElement(*dataForJava, Param, *StringValue);
				jobject wrappedFloat = Env->NewObject(floatClass, floatConstructor, static_cast<jfloat>(dataToSend[Param]));
				Env->SetObjectArrayElement(*dataForJava, Param, wrappedFloat);
			}
			auto ExtraValueArg = FJavaHelper::ToJavaString(Env, TagValue);
			// Execute the java code for this operation
			FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_VisualTransceiverSendArrayFloat, *dataForJava, *ExtraValueArg);
		}
	}
#endif
}
/*
void UVisualTransceiverFunctions::VisualTransceiverSendArrayByte(const TArray<uint8>& dataToSend, const FString& TagValue)
{
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		jclass byteClass = Env->FindClass("java/lang/Byte");
		auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(dataToSend.Num(), byteClass, NULL));
		jmethodID byteConstructor = Env->GetMethodID(byteClass, "<init>", "(B)V");
		if (dataForJava)
		{
			for (uint32 Param = 0; Param < dataToSend.Num(); Param++)
			{
				jobject wrappedByte = Env->NewObject(byteClass, byteConstructor, static_cast<jbyte>(dataToSend[Param]));
				Env->SetObjectArrayElement(*dataForJava, Param, wrappedByte);
			}
			auto ExtraValueArg = FJavaHelper::ToJavaString(Env, TagValue);
			// Execute the java code for this operation
			FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_VisualTransceiverSendArrayByte, *dataForJava, *ExtraValueArg);
		}
	}
#endif
}
*/
void UVisualTransceiverFunctions::VisualTransceiverSendArrayInt(const TArray<int32>& dataToSend, const FString& TagValue)
{
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		//UE_LOG(LogVisualTransceiver, Warning, TEXT("LensCap VisualTransceiver Send Int JNI method\n"));
		jclass integerClass = Env->FindClass("java/lang/Integer");
		auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(dataToSend.Num(), integerClass, NULL));
		jmethodID integerConstructor = Env->GetMethodID(integerClass, "<init>", "(I)V");
		//auto dataForJava = NewScopedJavaObject(Env, (jintArray)Env->NewIntArray(dataToSend.Num()));
		if (dataForJava)
		{
			for (uint32 Param = 0; Param < dataToSend.Num(); Param++)
			{
				jobject wrappedInt = Env->NewObject(integerClass, integerConstructor, static_cast<jint>(dataToSend[Param]));
				Env->SetObjectArrayElement(*dataForJava, Param, wrappedInt);
			}
			auto ExtraValueArg = FJavaHelper::ToJavaString(Env, TagValue);
			// Execute the java code for this operation
			FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_VisualTransceiverSendArrayInt, *dataForJava, *ExtraValueArg);
		}
	}
#endif
}

void GoogleARCoreDoSobelEdgeDetection(
	const uint8 *InYPlaneData,
	uint32 YPlanePixelStride,
	uint32 YPlaneRowStride,
	uint8 *OutPixels,
	int32 Width,
	int32 Height)
{
	TArray<uint8> YPlaneDataCopy(InYPlaneData, YPlaneRowStride * Height);

	int XKernel[3][3] = {
		{ -1, 0, 1 },
		{ -2, 0, 2 },
		{ -1, 0, 1 }
	};

	int YKernel[3][3] = {
		{ -1, -2, -1 },
		{ 0,  0,  0 },
		{ 1,  2,  1 }
	};

	for (int32 y = 0; y < Height; y++)
	{
		for (int32 x = 0; x < Width; x++)
		{
			int XMag = 0;
			int YMag = 0;

			for (int32 u = 0; u < 3; u++)
			{
				for (int32 v = 0; v < 3; v++)
				{
					int32 u2 = x + u - 1;
					int32 v2 = y + v - 1;

					if (u2 < 0) u2 = 0;
					if (u2 >= Width) u2 = Width - 1;
					if (v2 < 0) v2 = 0;
					if (v2 >= Height) v2 = Height - 1;

					uint8 SourcePixel = YPlaneDataCopy[
						u2 * YPlanePixelStride +
							v2 * YPlaneRowStride];

					XMag += SourcePixel * XKernel[u][v];
					YMag += SourcePixel * YKernel[u][v];
				}
			}

			int Magnitude = XMag * XMag + YMag * YMag;
			uint8 Output = Magnitude > SobelThreshold ? 0xFF : 0x1F;
			OutPixels[y * Width + x] = Output;
		}
	}
}

FString GetEdgeFeatures() {
	EGoogleARCoreFunctionStatus AcquireStatus = EGoogleARCoreFunctionStatus::NotAvailable;
	UGoogleARCoreCameraImage *CameraImage = nullptr;
	AcquireStatus = UGoogleARCoreFrameFunctionLibrary::AcquireCameraImage(CameraImage);
	if (AcquireStatus != EGoogleARCoreFunctionStatus::Success)
	{
		UE_LOG(LogTemp, Warning, TEXT("GetCameraImage Acquire Google ARCore Status Failed!!"));
	}

	int32_t Width = CameraImage->GetWidth();
	int32_t Height = CameraImage->GetHeight();
	int32_t planeCount = CameraImage->GetPlaneCount();

	uint8_t *TempRGBABuf = new uint8_t[Width * Height];
	UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Camera Frame Size: %d, %d"), Width, Height);

	// Y
	int32_t y_xStride = 0;
	int32_t y_yStride = 0;
	int32_t y_length = 0;
	uint8_t *y_planeData = nullptr;
	y_planeData = CameraImage->GetPlaneData(0, y_xStride, y_yStride, y_length);

	// U
	int32_t u_xStride = 0;
	int32_t u_yStride = 0;
	int32_t u_length = 0;
	uint8_t *u_planeData = nullptr;
	u_planeData = CameraImage->GetPlaneData(1, u_xStride, u_yStride, u_length);

	// V
	int32_t v_xStride = 0;
	int32_t v_yStride = 0;
	int32_t v_length = 0;
	uint8_t *v_planeData = nullptr;
	v_planeData = CameraImage->GetPlaneData(2, v_xStride, v_yStride, v_length);

	GoogleARCoreDoSobelEdgeDetection(y_planeData, y_xStride, y_yStride, TempRGBABuf, Width, Height);

	CameraImage->Release();

	FString aaa;
	aaa = BytesToString(TempRGBABuf, (Height*Width));

	//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap GetEdgeFeatures: %s"), *aaa);

	return aaa;
}

void UVisualTransceiverFunctions::VisualTransceiverSendArrayString(const TArray<FString>& dataToSend, const FString& TagValue)
{
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		//UE_LOG(LogVisualTransceiver, Warning, TEXT("LensCap VisualTransceiver Send String JNI method\n"));

		auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(dataToSend.Num(), FJavaWrapper::JavaStringClass, NULL));
		if (dataForJava)
		{
			for (uint32 Param = 0; Param < dataToSend.Num(); Param++)
			{
				auto StringValue = FJavaHelper::ToJavaString(Env, dataToSend[Param]);
				Env->SetObjectArrayElement(*dataForJava, Param, *StringValue);
			}
			auto ExtraValueArg = FJavaHelper::ToJavaString(Env, TagValue);
			// Execute the java code for this operation
			FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_VisualTransceiverSendArrayString, *dataForJava, *ExtraValueArg);
		}
	}
#endif
}


float UVisualTransceiverFunctions::VisualTransceiverReceiveFloat()
{
	float result = 0.0f;
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		result = (float)FJavaWrapper::CallFloatMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_VisualTransceiverReceiveFloat);
		UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap VisualTransceiverReceiveFloat %f"), result);
	}
#endif
	return result;
}

TArray<float> UVisualTransceiverFunctions::VisualTransceiverReceiveArrayFloat()
{
	TArray<float> result;
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		//auto dataForJava = (jfloatArray)Env->NewFloatArray(3);
		auto dataForJava = (jfloatArray)FJavaWrapper::CallObjectMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_VisualTransceiverReceiveArrayFloat);
		jfloat* tiltFloatValues = Env->GetFloatArrayElements(dataForJava, 0);
		jsize lenArray = Env->GetArrayLength(dataForJava);
		//result.Add(tiltFloatValues[0]);
		//result.Add(tiltFloatValues[1]);
		//result.Add(tiltFloatValues[2]);
		for (uint32 i = 0; i < lenArray; i++) {
			result.Add(tiltFloatValues[i]);
		}
		Env->ReleaseFloatArrayElements(dataForJava, tiltFloatValues, 0);
		UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap VisualTransceiverReceiveFloat length %d"), lenArray);
	}
#endif
	return result;
}
//Andrei-added functs
void UVisualTransceiverFunctions::VT_Send_sendTransformARCoordinates2D(EGoogleARCoreCoordinates2DType InputCoordinatesType, const TArray<FVector2D>& InputCoordinates, EGoogleARCoreCoordinates2DType OutputCoordinatesType, TArray<FVector2D>& OutputCoordinates)
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_TransformARCoordinates2D"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		UGoogleARCoreFrameFunctionLibrary::TransformARCoordinates2D(InputCoordinatesType, InputCoordinates, OutputCoordinatesType, OutputCoordinates);
		TArray<float> dataToSend= { (float)InputCoordinatesType,(float)OutputCoordinatesType};
		for (int i = 0; i < InputCoordinates.Num(); i++)
		{
			dataToSend.Append({InputCoordinates[i].X, InputCoordinates[i].Y});
		}
		for (int i = 0; i < OutputCoordinates.Num(); i++)
		{
			dataToSend.Append({ OutputCoordinates[i].X, OutputCoordinates[i].Y });
		}
		for (int k = 0; k < dataToSend.Num(); k++)
		{
			UE_LOG(LogTemp, Warning, TEXT("sendtransformarcoordinates2d data ,%f"), dataToSend[k]);
		}
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		lenscap_transformarcoordinates2d=dataToSend;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_cameraimageintrinsics Needs Permission!!"));
	}
}
EGoogleARCoreFunctionStatus UVisualTransceiverFunctions::VT_Send_sendcameratextureintrinsics(UGoogleARCoreCameraIntrinsics *&OutCameraIntrinsics)
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_cameratextureintrinsics"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		TArray<int32> intdataToSend = { (int32)UGoogleARCoreFrameFunctionLibrary::GetCameraTextureIntrinsics(OutCameraIntrinsics) };
		UE_LOG(LogTemp, Warning, TEXT("cameratexture intrinsics state data ,%i"), UGoogleARCoreFrameFunctionLibrary::GetCameraTextureIntrinsics(OutCameraIntrinsics));
		lenscap_cameratextureintrinsics_data = intdataToSend;
		VisualTransceiverSendArrayInt(intdataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_cameratextureintrinsics Needs Permission!!"));
	}
	return UGoogleARCoreFrameFunctionLibrary::GetCameraTextureIntrinsics(OutCameraIntrinsics);
}
EGoogleARCoreFunctionStatus UVisualTransceiverFunctions::VT_Send_sendcameraimageintrinsics(UGoogleARCoreCameraIntrinsics *&OutCameraIntrinsics)
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_cameraimageintrinsics"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		TArray<int32> intdataToSend = { (int32)UGoogleARCoreFrameFunctionLibrary::GetCameraImageIntrinsics(OutCameraIntrinsics) };
		UE_LOG(LogTemp, Warning, TEXT("cameraimage intrinsics state data ,%i"), UGoogleARCoreFrameFunctionLibrary::GetCameraImageIntrinsics(OutCameraIntrinsics));
		VisualTransceiverSendArrayInt(intdataToSend, extraTag);
		lenscap_cameraimageintrinsics_data = intdataToSend;
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_cameraimageintrinsics Needs Permission!!"));
	}
	return UGoogleARCoreFrameFunctionLibrary::GetCameraImageIntrinsics(OutCameraIntrinsics);
}
bool UVisualTransceiverFunctions::VT_Send_sendARCoreLineTrace(UObject* WorldContextObject, const FVector2D& ScreenPosition, TSet<EGoogleARCoreLineTraceChannel> TraceChannels, TArray<FARTraceResult>& OutHitResults)
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_linetrace"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		bool Res = UGoogleARCoreFrameFunctionLibrary::ARCoreLineTrace(WorldContextObject,ScreenPosition,TraceChannels,OutHitResults);
		lenscap_linetrace = Res;
		TArray<FString> stringdataToSend;
		if (Res)
		{
			stringdataToSend = { "True" };
		}
		else
		{
			stringdataToSend = { "False" };
		}
		VisualTransceiverSendArrayString(stringdataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
		return Res;
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_ARCoreLineTrace Needs Permission!!"));
	}
	return false;
}
UTexture* UVisualTransceiverFunctions::VT_Send_sendCameraTexture()
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_cameratexture"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		UE_LOG(LogTemp, Warning, TEXT("camera texture data ,%f"), UGoogleARCoreFrameFunctionLibrary::GetCameraTexture()->);
		//TArray<float> dataToSend = {UGoogleARCoreFrameFunctionLibrary::GetCameraTexture()};
		//VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_cameratexture Needs Permission!!"));
	}
	return UGoogleARCoreFrameFunctionLibrary::GetCameraTexture();
}
EGoogleARCoreTrackingState UVisualTransceiverFunctions::VT_Send_ARCoreTrackingState()
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_trackingstate"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
	TArray<int32> intdataToSend = {(int32)UGoogleARCoreFrameFunctionLibrary::GetTrackingState()};
	UE_LOG(LogTemp, Warning, TEXT("tracking state data ,%i"), UGoogleARCoreFrameFunctionLibrary::GetTrackingState());
	lenscap_trackingstate_data =intdataToSend;
	VisualTransceiverSendArrayInt(intdataToSend, extraTag);
	TArray<int32> Result;
	GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_ARCoreTrackingState Needs Permission!!"));
	}
	return UGoogleARCoreFrameFunctionLibrary::GetTrackingState();
}
bool UVisualTransceiverFunctions::VT_Send_ARCoreCameraconfig(FGoogleARCoreCameraConfig& OutCurrentCameraConfig)
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_cameraconfig"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		bool Res=UGoogleARCoreSessionFunctionLibrary::GetARCoreCameraConfig(OutCurrentCameraConfig);
		lenscap_cameraconfig = Res;
		TArray<FString> stringdataToSend;
		if (Res)
		{
			stringdataToSend = { "True" };
		}
		else
		{
			stringdataToSend = { "False" };
		}
		VisualTransceiverSendArrayString(stringdataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
		return Res;
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_cameraconfig Needs Permission!!"));
	}
	return false;
}
void UVisualTransceiverFunctions::VT_Send_Image(TArray<UGoogleARCoreAugmentedImage*>& OutAugmentedImageList) {
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_image"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		//lens.LensCap_face = lens.LensCap_face + 1;
		UGoogleARCoreSessionFunctionLibrary::GetAllAugmentedImages(OutAugmentedImageList);
		TArray<float> dataToSend;
		for (int32 i = 0; i < OutAugmentedImageList.Num(); i++)
		{
			FVector imageA = OutAugmentedImageList[i]->GetExtent();
			dataToSend.Append({imageA.X,imageA.Y,imageA.Z});
		}
		lenscap_image_data = dataToSend;
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
		for (int k = 0; k < dataToSend.Num(); k++)
		{
			UE_LOG(LogTemp, Warning, TEXT("image data ,%f"), dataToSend[k]);
		}
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_Face Needs Permission!!"));
	}
}
void UVisualTransceiverFunctions::VT_Send_TrackablePoints(TArray<UARTrackedPoint*>& OutTrackablePointList)
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_points"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		UGoogleARCoreSessionFunctionLibrary::GetAllTrackablePoints(OutTrackablePointList);
		TArray<float> dataToSend;
		for(int i=0; i < OutTrackablePointList.Num(); i++)
		{
			FTransform pointA = OutTrackablePointList[i]->GetLocalToTrackingTransform();
			TArray<float> internalA = { pointA.GetLocation().X, pointA.GetLocation().Y, pointA.GetLocation().Z,
				pointA.GetRotation().X, pointA.GetRotation().Y, pointA.GetRotation().Z, pointA.GetRotation().W,
				pointA.GetScale3D().X, pointA.GetScale3D().Y, pointA.GetScale3D().Z };
			dataToSend.Append(internalA);
		}
		lenscap_points_data = dataToSend;
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
		for (int k = 0; k < dataToSend.Num(); k++)
		{
			UE_LOG(LogTemp, Warning, TEXT("Trackable points,%f"), dataToSend[k]);
		}
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_TrackablePoints Needs Permission!!"));
	}
}
void UVisualTransceiverFunctions::VT_Send_AllPlanes(TArray<UARPlaneGeometry*>& OutPlaneList)
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_plane"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		UGoogleARCoreSessionFunctionLibrary::GetAllPlanes(OutPlaneList);
		TArray<float> dataToSend;
		for(int i=0; i < OutPlaneList.Num(); i++)
		{
			FVector planeA = OutPlaneList[i]->GetCenter();
			FVector planeB = OutPlaneList[i]->GetExtent();
			dataToSend.Append({planeA.X,planeA.Y,planeA.Z,planeB.X,planeB.Y,planeB.Z});
			for(int j=0;j<OutPlaneList[i]->GetBoundaryPolygonInLocalSpace().Num();j++)
			{
				FVector planeC = OutPlaneList[i]->GetBoundaryPolygonInLocalSpace()[j];
				dataToSend.Append({planeC.X,planeC.Y,planeC.Z});
			}
		}
		lenscap_planes_data = dataToSend;
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
		for (int k = 0; k < dataToSend.Num(); k++)
		{
			UE_LOG(LogTemp, Warning, TEXT("Plane data ,%f"), dataToSend[k]);
		}
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_AllPlanes Needs Permission!!"));
	}
}
void UVisualTransceiverFunctions::VT_Send_PassthroughCameraImageUV(TArray<float>& InUV, TArray<float>& OutUV)
{
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_passthrough"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		UGoogleARCoreSessionFunctionLibrary::GetPassthroughCameraImageUV(InUV, OutUV);
		TArray<float> dataToSend;
		dataToSend.Append(InUV);
		dataToSend.Append(OutUV);
		lenscap_PassthroughCameraImageUV_data = dataToSend;
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
		for (int k = 0; k < dataToSend.Num(); k++)
		{
			UE_LOG(LogTemp, Warning, TEXT("pass through camera,%f"), dataToSend[k]);
		}
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_PassthroughCameraImageUV Needs Permission!!"));
	}
}

void UVisualTransceiverFunctions::VT_Send_Camera_Pose(FTransform& LastPose) {
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_cameraPose"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		lens.LensCap_cameraPose = lens.LensCap_cameraPose + 1;
		UGoogleARCoreFrameFunctionLibrary::GetPose(LastPose);
		//UE_LOG(LogTemp, Warning, TEXT("VT_Send_Camera_Pose Time Now: Hour: %d, Minute: %d, Second: %d, Milli: %d, current: %d, Tag: %s"), 
		//	currentDate.GetHour(), currentDate.GetMinute(), currentDate.GetSecond(), currentDate.GetMillisecond(), currentMilli, *extraTag);
		//UE_LOG(LogTemp, Warning, TEXT("VT_Send_Camera_Pose Time Now: %s"), *extraTag);
		float x = LastPose.GetLocation().X;
		float y = LastPose.GetLocation().Y;
		float z = LastPose.GetLocation().Z;
		float pitch = LastPose.GetRotation().X;
		float roll = LastPose.GetRotation().Y;
		float yaw = LastPose.GetRotation().Z;
		float w = LastPose.GetRotation().W;
		float x3d = LastPose.GetScale3D().X;
		float y3d = LastPose.GetScale3D().Y;
		float z3d = LastPose.GetScale3D().Z;
		TArray<float> dataToSend = { x, y, z, pitch, roll, yaw, x3d, y3d, z3d };
		lenscap_pose_data = dataToSend;
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_Camera_Pose Needs Permission!!"));
	}
}

void UVisualTransceiverFunctions::VT_Send_Feature_Edge(FString& TempRGBABuf) {
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_edge"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		lens.LensCap_edge = lens.LensCap_edge + 1;
		TempRGBABuf = GetEdgeFeatures();
		TArray<FString> dataToSend;
		dataToSend.Add(TempRGBABuf);
		VisualTransceiverSendArrayString(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_Feature_Edge Needs Permission!!"));
	}
}

void UVisualTransceiverFunctions::VT_Send_Face(TArray<UGoogleARCoreAugmentedFace*>& OutAugmentedFaceList) {
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_face"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		lens.LensCap_face = lens.LensCap_face + 1;
		UGoogleARCoreSessionFunctionLibrary::GetAllAugmentedFaces(OutAugmentedFaceList);
		TArray<float> dataToSend;
		for (int32 i = 0; i < OutAugmentedFaceList.Num(); i++)
		{
			FTransform faceA;
			faceA = OutAugmentedFaceList[i]->GetLocalToWorldTransformOfRegion(EGoogleARCoreAugmentedFaceRegion::NoseTip);
			TArray<float> internalA = { faceA.GetLocation().X, faceA.GetLocation().Y, faceA.GetLocation().Z,
				faceA.GetRotation().X, faceA.GetRotation().Y, faceA.GetRotation().Z, faceA.GetRotation().W,
				faceA.GetScale3D().X, faceA.GetScale3D().Y, faceA.GetScale3D().Z };

			FTransform faceB;
			faceB = OutAugmentedFaceList[i]->GetLocalToWorldTransformOfRegion(EGoogleARCoreAugmentedFaceRegion::ForeheadLeft);
			TArray<float> internalB = { faceB.GetLocation().X, faceB.GetLocation().Y, faceB.GetLocation().Z,
				faceB.GetRotation().X, faceB.GetRotation().Y, faceB.GetRotation().Z, faceB.GetRotation().W,
				faceB.GetScale3D().X, faceB.GetScale3D().Y, faceB.GetScale3D().Z};

			FTransform faceC;
			faceC = OutAugmentedFaceList[i]->GetLocalToWorldTransformOfRegion(EGoogleARCoreAugmentedFaceRegion::ForeheadRight);
			TArray<float> internalC = { faceC.GetLocation().X, faceC.GetLocation().Y, faceC.GetLocation().Z,
				faceC.GetRotation().X, faceC.GetRotation().Y, faceC.GetRotation().Z, faceC.GetRotation().W,
				faceC.GetScale3D().X, faceC.GetScale3D().Y, faceC.GetScale3D().Z };

			dataToSend.Append(internalA);
			dataToSend.Append(internalB);
			dataToSend.Append(internalC);
		}
		/*
		TArray<uint8> inPub = {211};
		TArray<uint8> inPriv = {212};
		TArray<uint8> inMod = {213};
		FRSAKeyHandle fs1 = fsra.CreateKey(inPub, inPriv, inMod);
		int32 bytesEncrypted;
		TArray<uint8> inRSA = {123};
		for (int32 i = 0; i < dataToSend.Num(); i++) {
			FString ttttstttring;
			ttttstttring = FString::SanitizeFloat(dataToSend[i]);
			uint8* abcdefg = new uint8[ttttstttring.Len()];
			//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Send Face String Length %d"), ttttstttring.Len());
			StringToBytes(ttttstttring, abcdefg, ttttstttring.Len());
			for (int32 j = 0; j < ttttstttring.Len(); j++) {
				//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Send Face String: %s; Bytes %d"), *ttttstttring, abcdefg[j]);
				inRSA.Add(abcdefg[j]);
			}
		}
		TArray<uint8> outRSA;
		bytesEncrypted = fsra.EncryptPrivate(inRSA, outRSA, fs1);
		UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Send Face Bytes Encrypted: %ld"), bytesEncrypted);
		UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Send Face Keys Size %ld, MaxSize, %ld"), fsra.GetKeySize(fs1), fsra.GetMaxDataSize(fs1));
		//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Send Face Key Size: %d"), fsra.GetKeySize(fs1));
		*/
		lenscap_face_data = dataToSend;
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_Face Needs Permission!!"));
	}
}

void UVisualTransceiverFunctions::VT_Send_Point_Cloud(TArray<FVector>& OutLatestPointCloud) {
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_pointCloud"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		lens.LensCap_pointCloud = lens.LensCap_pointCloud + 1;
		//UGoogleARCoreFrameFunctionLibrary::GetPointCloud(OutLatestPointCloud);
		TArray<float> dataToSend;
		OutLatestPointCloud = UARBlueprintLibrary::GetPointCloud();
		for (int i = 0; i < OutLatestPointCloud.Num(); i++) {
			float tmp[] = { OutLatestPointCloud[i].X, OutLatestPointCloud[i].Y, OutLatestPointCloud[i].Z };
			dataToSend.Append(tmp, UE_ARRAY_COUNT(tmp));
		}
		lenscap_pointCloud_data = dataToSend;
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_Point_Cloud Needs Permission!!"));
	}
}

void UVisualTransceiverFunctions::VT_Send_Light_Estimation(FGoogleARCoreLightEstimate& LightEstimation) {
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60 * 60 * 1000 * currentDate.GetHour() + 60 * 1000 * currentDate.GetMinute() + 1000 * currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("LensCap_lightEstimation"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm) {
		lens.LensCap_lightEstimation = lens.LensCap_lightEstimation + 1;
		UGoogleARCoreFrameFunctionLibrary::GetLightEstimation(LightEstimation);
		TArray<float> dataToSend;
		//float tmp[] = { LightEstimation.PixelIntensity, LightEstimation.RGBScaleFactor.X, LightEstimation.RGBScaleFactor.Y, LightEstimation.RGBScaleFactor.Z};
		dataToSend.Add(LightEstimation.PixelIntensity);
		dataToSend.Add(LightEstimation.RGBScaleFactor.X);
		dataToSend.Add(LightEstimation.RGBScaleFactor.Y);
		dataToSend.Add(LightEstimation.RGBScaleFactor.Z);
		lenscap_light_data = dataToSend;
		VisualTransceiverSendArrayFloat(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_Light_Estimation Needs Permission!!"));
	}
}

void UVisualTransceiverFunctions::VT_Send_Camera_Frame(FTransform& LastPose) {
	int32 currentMilli;
	FDateTime currentDate = FDateTime::UtcNow();
	currentMilli = 60*60*1000*currentDate.GetHour() + 60*1000*currentDate.GetMinute() + 1000*currentDate.GetSecond() + currentDate.GetMillisecond();
	FString extraTag;
	FString Tag = FString(TEXT("lenscap_cameraFrame"));
	extraTag = Tag + FString(TEXT("_")) + FString::FromInt(currentMilli);
	bool perm = LensCapCheckPermission(Tag);
	if (perm)
	{
		lens.LensCap_cameraImage = lens.LensCap_cameraImage + 1;
		EGoogleARCoreFunctionStatus AcquireStatus = EGoogleARCoreFunctionStatus::NotAvailable;
		UGoogleARCoreCameraImage *CameraImage = nullptr;
		AcquireStatus = UGoogleARCoreFrameFunctionLibrary::AcquireCameraImage(CameraImage);
		if (AcquireStatus != EGoogleARCoreFunctionStatus::Success)
		{
			UE_LOG(LogTemp, Warning, TEXT("VT_Send_Camera_Frame Acquire Google ARCore Status Failed!!"));
		}

		int32_t Width = CameraImage->GetWidth();
		int32_t Height = CameraImage->GetHeight();
		int32_t planeCount = CameraImage->GetPlaneCount();
		//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Camera Frame Size: %d, %d"), Width, Height);
		uint8_t *TempRGBABuf = new uint8_t[Width * Height];
		UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Camera Frame Size: %d, %d"), Width, Height);
		//uint8 *OutPixels;

		// Y
		int32_t y_xStride = 0;
		int32_t y_yStride = 0;
		int32_t y_length = 0;
		uint8_t *y_planeData = nullptr;
		y_planeData = CameraImage->GetPlaneData(0, y_xStride, y_yStride, y_length);

		// U
		int32_t u_xStride = 0;
		int32_t u_yStride = 0;
		int32_t u_length = 0;
		uint8_t *u_planeData = nullptr;
		u_planeData = CameraImage->GetPlaneData(1, u_xStride, u_yStride, u_length);

		// V
		int32_t v_xStride = 0;
		int32_t v_yStride = 0;
		int32_t v_length = 0;
		uint8_t *v_planeData = nullptr;
		v_planeData = CameraImage->GetPlaneData(2, v_xStride, v_yStride, v_length);
		//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Y U V Length: %d, %d, %d"), y_length, u_length, v_length);
		CameraImage->Release();
		FString yyy;
		FString uuu;
		FString vvv;
		yyy = BytesToString(y_planeData, y_length);
		uuu = BytesToString(u_planeData, u_length);
		vvv = BytesToString(v_planeData, v_length);
		TArray<FString> dataToSend;
		dataToSend.Add(yyy);
		dataToSend.Add(uuu);
		dataToSend.Add(vvv);
		VisualTransceiverSendArrayString(dataToSend, extraTag);
		//dataToSend.Append(y_planeData, UE_ARRAY_COUNT(y_planeData));
		//VisualTransceiverSendArrayByte(dataToSend, extraTag);
		TArray<int32> Result;
		GetLensCapCountAndroid(Result);
	}
	else {
		UE_LOG(LogTemp, Warning, TEXT("VT_Send_Camera_Frame Needs Permission!!"));
	}
	//VisualTransceiverSendArrayInt(dataToSend, Tag);
}

int32 UVisualTransceiverFunctions::GetLensCapCount(FString inputType) {
//#if PLATFORM_ANDROID
//	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
//	{
		//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap ARCore GetLensCapCount Called"));
		int32 Result = 0;
		//auto Argument = FJavaHelper::ToJavaString(Env, inputType);
		//Result = FJavaWrapper::CallIntMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_GetLensCapCount, *Argument);
		// Execute the java code for this operation
		FString right;
		inputType.Split(TEXT("_"), NULL, &right);
		if (right.Equals(TEXT("cameraFrame"))) {
			Result = lens.LensCap_cameraImage;
		}
		else if (right.Equals(TEXT("cameraPose"))) {
			Result = lens.LensCap_cameraPose;
		}
		else if (right.Equals(TEXT("lightEstimation"))) {
			Result = lens.LensCap_lightEstimation;
		}
		else if (right.Equals(TEXT("pointCloud"))) {
			Result = lens.LensCap_pointCloud;
		}
		else if (right.Equals(TEXT("edge"))) {
			Result = lens.LensCap_edge;
		}
		else if (right.Equals(TEXT("face"))) {
			Result = lens.LensCap_face;
		}
		else {
			UE_LOG(LogTemp, Error, TEXT("UE4 LensCap ARCore GetLensCapCount Error"));
			Result = 0;
		}
		//FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, FJavaWrapper::AndroidThunkJava_GetLensCapCount, *Result);
		return Result;
//	}
//#endif
//	return 0;
}

TArray<int32> GetLensCapCountInternal() {
	TArray<int32> Result;
	Result.Add(lens.LensCap_cameraImage);
	Result.Add(lens.LensCap_cameraPose);
	Result.Add(lens.LensCap_lightEstimation);
	Result.Add(lens.LensCap_pointCloud);
	Result.Add(lens.LensCap_edge);
	Result.Add(lens.LensCap_face);
	return Result;
}

void getDataCopy() {

}

void UVisualTransceiverFunctions::GetLensCapCountAndroid(const TArray<int32>& dataCount) {
#if PLATFORM_ANDROID
	//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap ARCore GetLensCapCount Called"));
	TArray<int32> Result;
	Result = GetLensCapCountInternal();
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		jclass integerClass = Env->FindClass("java/lang/Integer");
		auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(Result.Num(), integerClass, NULL));
		jmethodID integerConstructor = Env->GetMethodID(integerClass, "<init>", "(I)V");
		if (dataForJava)
		{
			for (uint32 Param = 0; Param < Result.Num(); Param++)
			{
				jobject wrappedInt = Env->NewObject(integerClass, integerConstructor, static_cast<jint>(Result[Param]));
				Env->SetObjectArrayElement(*dataForJava, Param, wrappedInt);
			}
			// Execute the java code for this operation
			FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_GetLensCapCountAndroid, *dataForJava);
		}
	}
#endif
}

bool UVisualTransceiverFunctions::LensCapCheckPermission(const FString& Tag) {
	bool Result = false;
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv())
	{
		auto Argument = FJavaHelper::ToJavaString(Env, Tag);
		Result = FJavaWrapper::CallBooleanMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_LensCapCheckPermission, *Argument);
	}
	//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap CheckPermission %s"), Result?TEXT("True"):TEXT("False"));
#endif
	return Result;
}

void UVisualTransceiverFunctions::LensCapWriteFPS(const TArray<float>& dataToSend) {
#if PLATFORM_ANDROID
	if (JNIEnv* Env = FAndroidApplication::GetJavaEnv(true))
	{
		UE_LOG(LogVisualTransceiver, Warning, TEXT("LensCap VisualTransceiver Write FPS\n"));
		jclass floatClass = Env->FindClass("java/lang/Float");
		auto dataForJava = NewScopedJavaObject(Env, (jobjectArray)Env->NewObjectArray(dataToSend.Num(), floatClass, NULL));
		jmethodID floatConstructor = Env->GetMethodID(floatClass, "<init>", "(F)V");
		if (dataForJava)
		{
			for (uint32 Param = 0; Param < dataToSend.Num(); Param++)
			{
				jobject wrappedFloat = Env->NewObject(floatClass, floatConstructor, static_cast<jfloat>(dataToSend[Param]));
				Env->SetObjectArrayElement(*dataForJava, Param, wrappedFloat);
			}
			// Execute the java code for this operation
			FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, AndroidThunkJava_LensCapWriteFPS, *dataForJava);
		}
	}
#endif
}

#if PLATFORM_ANDROID
JNI_METHOD bool Java_com_epicgames_ue4Network_GameActivity_nativeValidateLensCapFloatData(JNIEnv* jenv, jobject thiz, jstring tag, jfloatArray dataValidate) {
	jfloat* dataFloatValues = jenv->GetFloatArrayElements(dataValidate, 0);
	jsize len = jenv->GetArrayLength(dataValidate);
	TArray<float> result;
	TArray<float> testData = {1.0f, 2.0f, 3.0f};
	for (int i = 0; i < len; i++) {
		result.Add(dataFloatValues[i]);
		UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Java_com_epicgames_ue4_GameActivity_validateLensCapData %f"), result[i]);
	}
	jenv->ReleaseFloatArrayElements(dataValidate, dataFloatValues, 0);
	if (result==testData) {
		return true;
	}
	else {
		return false;
	}
}
#endif

#if PLATFORM_ANDROID
JNI_METHOD bool Java_edu_ame_asu_meteor_lenscap_visualtransceiver_VisualLensCapTransceiver_nativeValidateVisualTransceiverFloatData(JNIEnv* jenv, jobject thiz, jstring tag, jfloatArray dataValidate) {
	const char* charsId = jenv->GetStringUTFChars(tag, 0);
	FString InterString = FString(UTF8_TO_TCHAR(charsId));
	//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Java_com_epicgames_ue4_GameActivity_validateLensCapData String %s"), *InterString);
	jenv->ReleaseStringUTFChars(tag, charsId);
	jfloat* dataFloatValues = jenv->GetFloatArrayElements(dataValidate, 0);
	jsize len = jenv->GetArrayLength(dataValidate);
	TArray<float> result;
	for (int i = 0; i < len; i++) {
		result.Add(dataFloatValues[i]);
		//UE_LOG(LogTemp, Warning, TEXT("UE4 LensCap Java_com_epicgames_ue4_GameActivity_validateLensCapData %f"), result[i]);
	}
	jenv->ReleaseFloatArrayElements(dataValidate, dataFloatValues, 0);
	if (InterString.Equals(TEXT("face"))) {
		if (result == lenscap_face_data) {
			return true;
		}
		else {
			return false;
		}
	}
	else if (InterString.Equals(TEXT("cameraPose"))) {
		if (result == lenscap_pose_data) {
			return true;
		}
		else {
			return false;
		}
	}
	else if (InterString.Equals(TEXT("pointCloud"))) {
		if (result == lenscap_pointCloud_data) {
			return true;
		}
		else {
			return false;
		}
	}
	else if (InterString.Equals(TEXT("lightEstimation"))) {
		if (result == lenscap_light_data) {
			return true;
		}
		else {
			return false;
		}
	}
	else {
		UE_LOG(LogTemp, Error, TEXT("UE4 LensCap Data Type Not Supported"));
		return false;
	}
}
#endif