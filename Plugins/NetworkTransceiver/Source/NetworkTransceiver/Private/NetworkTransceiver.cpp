// Copyright (c) 2018 Isara Technologies. All Rights Reserved.

#include "NetworkTransceiverPrivatePCH.h"

DEFINE_LOG_CATEGORY(LogNetworkTransceiver);

#define LOCTEXT_NAMESPACE "NetworkTransceiver"

class FNetworkTransceiver : public INetworkTransceiver
{
	virtual void StartupModule() override;
	virtual void ShutdownModule() override;
};

IMPLEMENT_MODULE( FNetworkTransceiver, NetworkTransceiver)

void FNetworkTransceiver::StartupModule()
{
#if PLATFORM_ANDROID
	UNetworkTransceiverFunctions::InitJavaFunctions();
#endif
}


void FNetworkTransceiver::ShutdownModule()
{
	// This function may be called during shutdown to clean up your module.  For modules that support dynamic reloading,
	// we call this function before unloading the module.
}

#undef LOCTEXT_NAMESPACE
