// Copyright (c) 2018 Isara Technologies. All Rights Reserved.

#pragma once
#include "GoogleARCoreTypes.h"
#include "VisualTransceiverFunctions.generated.h"

USTRUCT()
struct FLensCapType
{
	GENERATED_BODY()

	UPROPERTY()
	int32 LensCap_pointCloud;

	UPROPERTY()
	int32 LensCap_cameraPose;

	UPROPERTY()
	int32 LensCap_lightEstimation;

	UPROPERTY()
	int32 LensCap_cameraImage;

	UPROPERTY()
	int32 LensCap_edge;

	UPROPERTY()
	int32 LensCap_face;

	FLensCapType() {
		LensCap_pointCloud = 0;
		LensCap_cameraPose = 0;
		LensCap_lightEstimation = 0;
		LensCap_cameraImage = 0;
		LensCap_edge = 0;
		LensCap_face = 0;
	}
};

UCLASS(NotBlueprintable)
class UVisualTransceiverFunctions : public UObject {
	GENERATED_BODY()
	
public:

#if PLATFORM_ANDROID
	static void InitJavaFunctions();
#endif

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap VisualTransceiver Send Float"), Category = "LensCap|JNI")
	static void VisualTransceiverSendArrayFloat(const TArray<float>& dataToSend, const FString& TagValue);

	//UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap VisualTransceiver Send Byte"), Category = "LensCap|JNI")
	//static void VisualTransceiverSendArrayByte(const TArray<uint8>& dataToSend, const FString& TagValue);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap VisualTransceiver Send Int"), Category = "LensCap|JNI")
	static void VisualTransceiverSendArrayInt(const TArray<int32>& dataToSend, const FString& TagValue);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap VisualTransceiver Send String"), Category = "LensCap|JNI")
	static void VisualTransceiverSendArrayString(const TArray<FString>& dataToSend, const FString& TagValue);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap VisualTransceiver Receive Float"), Category = "LensCap|Receive")
	static float VisualTransceiverReceiveFloat();

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Receive Float Array"), Category = "LensCap|Receive")
	static TArray<float> VisualTransceiverReceiveArrayFloat();

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap_GoogleARCore_CameraPose", HidePin = "LastPose"), Category = "LensCap|Send")
	static void VT_Send_Camera_Pose(FTransform& LastPose);
	
	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap_GoogleARCore_Collect Face", HidePin = "OutAugmentedFaceList"), Category = "LensCap|Send")
	static void VT_Send_Face(TArray<UGoogleARCoreAugmentedFace*>& OutAugmentedFaceList);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send Point Cloud", HidePin = "OutLatestPointCloud"), Category = "LensCap|Send")
	static void VT_Send_Point_Cloud(TArray<FVector>& OutLatestPointCloud);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send Feature Edge", HidePin = "TempRGBABuf"), Category = "LensCap|Send")
	static void VT_Send_Feature_Edge(FString& TempRGBABuf);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send Light Estimation", HidePin = "LightEstimation"), Category = "LensCap|Send")
	static void VT_Send_Light_Estimation(FGoogleARCoreLightEstimate& LightEstimation);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap_GoogleARCore_Collect CameraFrame", HidePin = "LastPose"), Category = "LensCap|Send")
	static void VT_Send_Camera_Frame(FTransform& LastPose);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap GetLensCapCount"), Category = "LensCap|Monitor")
	static int32 GetLensCapCount(FString inputType);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap GetLensCapCountAndroid"), Category = "LensCap|Monitor")
	static void GetLensCapCountAndroid(const TArray<int32>& dataCount);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap CheckPermission"), Category = "LensCap|Monitor")
	static bool LensCapCheckPermission(const FString& Tag);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Write FPS"), Category = "LensCap|Evaluation")
	static void LensCapWriteFPS(const TArray<float>& dataToSend);

	//Andrei-added functs
	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send PassthroughCameraImageUV"), Category = "LensCap|Send")
	static void VT_Send_PassthroughCameraImageUV(TArray<float>& InUV, TArray<float>& OutUV);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send AllPlanes"), Category = "LensCap|Send")
	static void VT_Send_AllPlanes(TArray<UARPlaneGeometry*>& OutPlaneList);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send trackablePoints"), Category = "LensCap|Send")
	static void VT_Send_TrackablePoints(TArray<UARTrackedPoint*>& OutTrackablePointList);
	
	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap_GoogleARCore_Collect Image", HidePin = "OutAugmentedFaceList"), Category = "LensCap|Send")
	static void VT_Send_Image(TArray<UGoogleARCoreAugmentedImage*>& OutAugmentedImages);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send ARCoreCameraconfig"), Category = "LensCap|Send")
	static bool VT_Send_ARCoreCameraconfig(FGoogleARCoreCameraConfig& OutCurrentCameraConfig);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send ARCoreTrackingState"), Category = "LensCap|Send")
	static EGoogleARCoreTrackingState VT_Send_ARCoreTrackingState();

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send cameratexture"), Category = "LensCap|Send")
	static UTexture* VT_Send_sendCameraTexture();

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send ArCoreLineTrace"), Category = "LensCap|Send")
	static bool VT_Send_sendARCoreLineTrace(UObject* WorldContextObject, const FVector2D& ScreenPosition, TSet<EGoogleARCoreLineTraceChannel> TraceChannels, TArray<FARTraceResult>& OutHitResults);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send cameraimage intrinsics"), Category = "LensCap|Send")
	static EGoogleARCoreFunctionStatus VT_Send_sendcameraimageintrinsics(UGoogleARCoreCameraIntrinsics *&OutCameraIntrinsics);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send cameratexture intrinsics"), Category = "LensCap|Send")
	static EGoogleARCoreFunctionStatus VT_Send_sendcameratextureintrinsics(UGoogleARCoreCameraIntrinsics *&OutCameraIntrinsics);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap Send TransformARCoordinates2D"), Category = "LensCap|Send")
	static void VT_Send_sendTransformARCoordinates2D(EGoogleARCoreCoordinates2DType InputCoordinatesType, const TArray<FVector2D>& InputCoordinates, EGoogleARCoreCoordinates2DType OutputCoordinatesType, TArray<FVector2D>& OutputCoordinates);
};

