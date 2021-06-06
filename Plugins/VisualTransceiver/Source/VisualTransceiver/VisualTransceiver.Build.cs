/* Copyright 2017-2019 HowToCompute. All Rights Reserved.
* You may use, distribute and modify this code under the
* terms of the MIT license.
*
* You should have received a copy of the MIT license with
* this file. If not, please visit: https://github.com/How2Compute/Socketer
*/

using UnrealBuildTool;
using System.IO;

public class VisualTransceiver : ModuleRules
{
	public VisualTransceiver(ReadOnlyTargetRules Target) : base(Target)
	{

        PublicIncludePaths.AddRange(
            new string[] {
                "VisualTransceiver/Public",
                // ... add public include paths required here ...
            }
            );

        PrivateIncludePaths.AddRange(
            new string[] {
                "VisualTransceiver/Private",
                // ... add other private include paths required here ...
            }
            );

        PublicDependencyModuleNames.AddRange(
            new string[] {
                "Engine",
                "Core",
                "CoreUObject",
                "GoogleARCoreBase",
                "GoogleARCoreSDK" ,
                "AugmentedReality",
                "RSA"
            }
            );

        PrivateIncludePathModuleNames.AddRange(
            new string[] {
                "Settings",
                "Launch",
                "Engine",
                "Core",
                "CoreUObject",
                "GoogleARCoreBase",
                "GoogleARCoreSDK",
                "AugmentedReality",
                "RSA"
            }
            );


        if (Target.Platform == UnrealTargetPlatform.IOS) {

        } else if (Target.Platform == UnrealTargetPlatform.Android) {
            string PluginPath = Utils.MakePathRelativeTo(ModuleDirectory, Target.RelativeEnginePath);
            AdditionalPropertiesForReceipt.Add(new ReceiptProperty("AndroidPlugin", Path.Combine(PluginPath, "VisualTransceiver_APL.xml")));
        }
    }
}
