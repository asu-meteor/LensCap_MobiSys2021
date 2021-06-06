// Copyright (c) 2018 Isara Technologies. All Rights Reserved.

#pragma once

#include "NetworkTransceiverFunctions.generated.h"

UCLASS(NotBlueprintable)
class UNetworkTransceiverFunctions : public UObject {
	GENERATED_BODY()
	
public:

#if PLATFORM_ANDROID
	static void InitJavaFunctions();
#endif

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap NetworkTransceiver Send JNI"), Category = "LensCap|JNI")
	static void NetworkTransceiverSend(const TArray<float>& dataToSend);

	UFUNCTION(BlueprintCallable, meta = (Keywords = "LensCap", DisplayName = "LensCap NetworkTransceiver Receive JNI"), Category = "LensCap|JNI")
	static void NetworkTransceiverReceive(const TArray<float>& dataToReceive);
};
