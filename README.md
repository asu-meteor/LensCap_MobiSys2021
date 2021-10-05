Total 35 functions in .h,

Done:

-2void UGoogleARCoreSessionFunctionLibrary::GetPassthroughCameraImageUV(const TArray<float>& InUV, TArray<float>& OutUV)
-3void UGoogleARCoreSessionFunctionLibrary::GetAllPlanes(TArray<UARPlaneGeometry*>& OutPlaneList)
-4void UGoogleARCoreSessionFunctionLibrary::GetAllTrackablePoints(TArray<UARTrackedPoint*>& OutTrackablePointList)
-6void UGoogleARCoreSessionFunctionLibrary::GetAllAugmentedFaces(TArray<UGoogleARCoreAugmentedFace*>& OutAugmentedFaceList)
-1bool UGoogleARCoreSessionFunctionLibrary::GetARCoreCameraConfig(FGoogleARCoreCameraConfig& OutCurrentCameraConfig)
-5void UGoogleARCoreSessionFunctionLibrary::GetAllAugmentedImages(TArray<UGoogleARCoreAugmentedImage*>& OutAugmentedImageList)
-10void UGoogleARCoreFrameFunctionLibrary::GetPose(FTransform& LastePose)
-18void UGoogleARCoreFrameFunctionLibrary::GetLightEstimation(FGoogleARCoreLightEstimate& LightEstimation)
-19EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::GetPointCloud(UGoogleARCorePointCloud*& OutLatestPointCloud)
-23EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::AcquireCameraImage(UGoogleARCoreCameraImage *&OutLatestCameraImage)
-8EGoogleARCoreTrackingState UGoogleARCoreFrameFunctionLibrary::GetTrackingState()
-25EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::GetCameraImageIntrinsics(UGoogleARCoreCameraIntrinsics *&OutCameraIntrinsics)
-26EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::GetCameraTextureIntrinsics(UGoogleARCoreCameraIntrinsics *&OutCameraIntrinsics)

Did not implement,(either duplicate or redundant):

-14void UGoogleARCoreFrameFunctionLibrary::GetUpdatedPlanes(TArray<UARPlaneGeometry*>& OutPlaneList)
-15void UGoogleARCoreFrameFunctionLibrary::GetUpdatedTrackablePoints(TArray<UARTrackedPoint*>& OutTrackablePointList)
-16void UGoogleARCoreFrameFunctionLibrary::GetUpdatedAugmentedImages(TArray<UGoogleARCoreAugmentedImage*>& OutImageList)
-17void UGoogleARCoreFrameFunctionLibrary::GetUpdatedAugmentedFaces(TArray<UGoogleARCoreAugmentedFace*>& OutFaceList)
-20EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::AcquirePointCloud(UGoogleARCorePointCloud*& OutLatestPointCloud)
-9EGoogleARCoreTrackingFailureReason UGoogleARCoreFrameFunctionLibrary::GetTrackingFailureReason()
-13void UGoogleARCoreFrameFunctionLibrary::GetUpdatedARPins(TArray<UARPin*>& OutAnchorList)
21EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::GetCameraMetadata(const ACameraMetadata*& OutCameraMetadata)
-12bool UGoogleARCoreFrameFunctionLibrary::ARCoreLineTraceRay(UObject* WorldContextObject, const FVector& Start, const FVector& End, TSet<EGoogleARCoreLineTraceChannel> TraceChannels, TArray<FARTraceResult>& OutHitResults)
-7UARCandidateImage* UGoogleARCoreSessionFunctionLibrary::AddRuntimeCandidateImageFromRawbytes(UARSessionConfig* SessionConfig, const TArray<uint8>& ImageGrayscalePixels,int ImageWidth, int ImageHeight, FString FriendlyName, float PhysicalWidth, UTexture2D* CandidateTexture /*= nullptr*/)
-22UTexture* UGoogleARCoreFrameFunctionLibrary::GetCameraTexture()

Work in progress:

-24void UGoogleARCoreFrameFunctionLibrary::TransformARCoordinates2D(EGoogleARCoreCoordinates2DType InputCoordinatesType, const TArray<FVector2D>& InputCoordinates, EGoogleARCoreCoordinates2DType OutputCoordinatesType, TArray<FVector2D>& OutputCoordinates)
-11bool UGoogleARCoreFrameFunctionLibrary::ARCoreLineTrace(UObject* WorldContextObject, const FVector2D& ScreenPosition, TSet<EGoogleARCoreLineTraceChannel> TraceChannels, TArray<FARTraceResult>& OutHitResults)


