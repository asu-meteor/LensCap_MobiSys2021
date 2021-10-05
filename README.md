LensCap_MobiSys2021
Source Code for compiling LensCap plugin for Unreal Engine projects

Table of Contents
Overview of LensCap
System Requirements
Configuring Unreal Engine for LensCap
1.1 Setting up LensCap source files in Unreal Engine Source
1.2 Creating a Unreal Project with LensCap plugins
Configuring LensCap for stand-alone Android project in Unreal Engine
1.1 Opening the Android project
1.2 Enabling LensCap in Android
1.3 App usage in Android
Revision History
Development Notes
Overview
This Project contains all the necessary source code to create a split-process (LensCap) application using Unreal Engine, via plugins, and how to enable the LensCap functionality in stand-alone Android projects

System Requirements
General Requirements:
Unreal Engine v4.24.3 built from source
Microsoft Visual Studio 2019 v16.6.0
Android Requirements:
Android Studio v3.6.1
SDK Android 9.0 (Pie)
NDK r14b (it can be installed via CodeWorks, instructions on how to install it are availavble here)
Configure Unreal
In order to get the LensCap plugin to correctly build in Unreal Engine, some modifications and additions must be made to the source code of the Engine itself. Creating a new project with the LensCap plugins also requires additional steps.

1.1 Setup LensCap source files in Unreal
Make a blank folder named LensCap the following path of your Unreal Engine source code: UnrealEngine\Engine\Source\ThirdParty Copy the four libraries found under engineComponents into the newly created LensCap folder.

1.2 Creating Unreal project with LensCap
There are a few additional steps in building an Unreal Engine project with LensCap plugins correctly:

Open Up the new project creator in Unreal and select new project
Use the Handheld AR Template
In the options switch build for Desktop/Console to Mobile/Tablet and create the project
Close the project, and navigate to its source code, in its main folder copy the Plugins folder found in this repo.
Open the project in Unreal Engine again, under settings->plugins->Project(at very bottom of left-hand-side) enable the two plugins LensCap Network Transceiver module and Lenscap VisualTransceiver module, Do Not Press Restart afterwards, just close the settings window when done.
Under Settings-> Project Settings-> Platforms -> Android SDK, ensure that the Location of Android NDK is pointing to the r14b NDK required and that the NDK API Level is set as android-19.
Create an empty C++ class, File->New C++ class-> none. The name you give it does not matter.
Also under file, click on Refresh Visual Studio Project or Generate Visual Studio Project if the latter is not available
Close Unreal Engine and open up the project's visual studio solution
Then under solution explorer right-click your project select Project Only->Build only [Your Project Name]
Once it has built, open up the project in Unreal Engine and launch it to the desired device.
The application will then launch on the device
Configure Android
In order to enable LensCap functionality the Android source code needs some changes to it, and therfore must be edited, the easiest way to do this is through Android Studio and the following instructions assume that Android Studiois being used and that the Unreal Project has already been created and built.

1.1 Opening Android project
The Android source code project can be opened by opening the following file path in Android Studio: [Your Project]\Intermediate\Android\APK\Gradle. It is also recommended that the view of the file explorer is switch to Project, for the sake of making the files easier to access.

1.2 Enabling LensCap in Android
There are a few additional steps in enabling LensCap in Android Studio, due to the limitations of automation in Unreal Engine:

Comment out ndk version line in local.properties
In gradle.properties change the SDK versions to:
MIN_SDK_VERSION=26
TARGET_SDK_VERSION=28
In settings.gradle add the following anywhere in the file:
include ':app-networks',':VisualTransceiver',':NetworkTransceiver'
include ':SharedMemLib'
The project build.gradle should be changed to look accordingly:
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    ext.kotlin_version = '1.3.61'
    ext.kotlin_version = '1.3.21'
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
    apply from: 'buildscriptAdditions.gradle', to: buildscript
}

apply from: 'baseBuildAdditions.gradle'

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
In the apps build.gradle the following dependency should be added:
implementation project(path: ':VisualTransceiver')
In the apps gameactivity.java (found under gradle->app->src->main->java->com->epicgames.ue4) add the following to its imports:
import edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver;
Uncomment the following lines for the LensCap variables and functions:
private VisualLensCapTransceiver lensCap; (line: 565)
StartLensCap() (line: 637-657)
In line 540 the networks gameactivity (found under gradle->app-network->src->main->java->com->epicgames.ue4) the old package name has to be replaced with the package name of your new project:
val launchIntent = packageManager.getLaunchIntentForPackage("com.meteor.ARtest_2")
com.meteor.ARtest_2 should be replaced with own package name 9. In line 88 of the NetworkTransceiver's transmitter service (found under gradle->NetworkTransciever->main->java.edu.ame.asu.meteor.lenscap->networktransceiver) the old package name has to be replaced with the package name of your new project:

ioServiceIntent.setPackage("com.meteor.ARtest_2")
com.meteor.ARtest_2 should be replaced with own package name

App Usage in Android
Launching the application with LensCap also requires some additional steps:

First the visual process (app-app) has to be launched, and the Start AR button must be pressed prior to launching the network.
Then the network process (app-networks) can be launched, and the start AR button can be pressed on it to begin your LensCap experience
The overlay of the network process can be dismissed at any time by pressing the back key on the device.
Revision History
6/6/2021- Added Initial source code
6/11/2021- Added additional NDK information and cleaned up naming conventions

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
    - Already implemented from before
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


