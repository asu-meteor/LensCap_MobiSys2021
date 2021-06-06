// Copyright (c) 2018 Isara Technologies. All Rights Reserved.

#include "VisualTransceiverPrivatePCH.h"

DEFINE_LOG_CATEGORY(LogVisualTransceiver);

#define LOCTEXT_NAMESPACE "VisualTransceiver"

class FVisualTransceiver : public IVisualTransceiver
{
	virtual void StartupModule() override;
	virtual void ShutdownModule() override;
};

IMPLEMENT_MODULE( FVisualTransceiver, VisualTransceiver)

void FVisualTransceiver::StartupModule()
{
#if PLATFORM_ANDROID
	UVisualTransceiverFunctions::InitJavaFunctions();
#endif
}


void FVisualTransceiver::ShutdownModule()
{
	// This function may be called during shutdown to clean up your module.  For modules that support dynamic reloading,
	// we call this function before unloading the module.
}

#undef LOCTEXT_NAMESPACE
