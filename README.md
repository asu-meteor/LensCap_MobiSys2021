Total 35 functions in .h,

Done:

1. 2void UGoogleARCoreSessionFunctionLibrary::GetPassthroughCameraImageUV(const TArray<float>& InUV, TArray<float>& OutUV)
   - Returns: Float array=[InUv,OutUv]
2. void UGoogleARCoreSessionFunctionLibrary::GetAllPlanes(TArray<UARPlaneGeometry*>& OutPlaneList)
   - Returns: Float array=[planeCenter,PlaneExtent,boundrypolygoninlocalspace] for each plane
3. 4void UGoogleARCoreSessionFunctionLibrary::GetAllTrackablePoints(TArray<UARTrackedPoint*>& OutTrackablePointList)
   - Returns: Float array=[Xlocation,Ylocation,Zlocation,Xrotation,Yrotation,Zrotation,Xscale3d,Yscale3d,Zscale3d] for each point
4. 6void UGoogleARCoreSessionFunctionLibrary::GetAllAugmentedFaces(TArray<UGoogleARCoreAugmentedFace*>& OutAugmentedFaceList)
   - Already implemented from before
5. 1bool UGoogleARCoreSessionFunctionLibrary::GetARCoreCameraConfig(FGoogleARCoreCameraConfig& OutCurrentCameraConfig)
   - Returns: String array=[True or False]
6. 5void UGoogleARCoreSessionFunctionLibrary::GetAllAugmentedImages(TArray<UGoogleARCoreAugmentedImage*>& OutAugmentedImageList)
   - Returns: Float array=[Extent,X,Y,Z] for each image
7. 10void UGoogleARCoreFrameFunctionLibrary::GetPose(FTransform& LastePose)
   - Already implemented from before
8. 18void UGoogleARCoreFrameFunctionLibrary::GetLightEstimation(FGoogleARCoreLightEstimate& LightEstimation)
   - Already implemented from before
9. 19EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::GetPointCloud(UGoogleARCorePointCloud*& OutLatestPointCloud)
   - Already implemented from before
10. 23EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::AcquireCameraImage(UGoogleARCoreCameraImage *&OutLatestCameraImage)
11. 8EGoogleARCoreTrackingState UGoogleARCoreFrameFunctionLibrary::GetTrackingState()
   - Returns: Int array=[enum of teacking state]
12. 25EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::GetCameraImageIntrinsics(UGoogleARCoreCameraIntrinsics *&OutCameraIntrinsics)
   - Returns: Int array=[enum of cameraimageintrinsics]
13. 26EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::GetCameraTextureIntrinsics(UGoogleARCoreCameraIntrinsics *&OutCameraIntrinsics)
   - Returns: Int array=[enum of texture cameraimageintrinsics]

Did not implement,(either duplicate or redundant):

1. 14void UGoogleARCoreFrameFunctionLibrary::GetUpdatedPlanes(TArray<UARPlaneGeometry*>& OutPlaneList)
2. 15void UGoogleARCoreFrameFunctionLibrary::GetUpdatedTrackablePoints(TArray<UARTrackedPoint*>& OutTrackablePointList)
3. 16void UGoogleARCoreFrameFunctionLibrary::GetUpdatedAugmentedImages(TArray<UGoogleARCoreAugmentedImage*>& OutImageList)
4. 17void UGoogleARCoreFrameFunctionLibrary::GetUpdatedAugmentedFaces(TArray<UGoogleARCoreAugmentedFace*>& OutFaceList)
5. 20EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::AcquirePointCloud(UGoogleARCorePointCloud*& OutLatestPointCloud)
6.9EGoogleARCoreTrackingFailureReason UGoogleARCoreFrameFunctionLibrary::GetTrackingFailureReason()
7. 13void UGoogleARCoreFrameFunctionLibrary::GetUpdatedARPins(TArray<UARPin*>& OutAnchorList)
8. 21EGoogleARCoreFunctionStatus UGoogleARCoreFrameFunctionLibrary::GetCameraMetadata(const ACameraMetadata*& OutCameraMetadata)
9. 12bool UGoogleARCoreFrameFunctionLibrary::ARCoreLineTraceRay(UObject* WorldContextObject, const FVector& Start, const FVector& End, TSet<EGoogleARCoreLineTraceChannel> TraceChannels, TArray<FARTraceResult>& OutHitResults)
10. 7UARCandidateImage* UGoogleARCoreSessionFunctionLibrary::AddRuntimeCandidateImageFromRawbytes(UARSessionConfig* SessionConfig, const TArray<uint8>& ImageGrayscalePixels,int ImageWidth, int ImageHeight, FString FriendlyName, float PhysicalWidth, UTexture2D* CandidateTexture /*= nullptr*/)
11. 22UTexture* UGoogleARCoreFrameFunctionLibrary::GetCameraTexture()

Work in progress:

1. 24void UGoogleARCoreFrameFunctionLibrary::TransformARCoordinates2D(EGoogleARCoreCoordinates2DType InputCoordinatesType, const TArray<FVector2D>& InputCoordinates, EGoogleARCoreCoordinates2DType OutputCoordinatesType, TArray<FVector2D>& OutputCoordinates)
2. 11bool UGoogleARCoreFrameFunctionLibrary::ARCoreLineTrace(UObject* WorldContextObject, const FVector2D& ScreenPosition, TSet<EGoogleARCoreLineTraceChannel> TraceChannels, TArray<FARTraceResult>& OutHitResults)


