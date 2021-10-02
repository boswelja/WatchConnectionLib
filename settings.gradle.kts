include(
    ":common:common",
    ":common:internal",
    ":mobile:mobile-core",
    ":mobile:platform-wearos",
    ":mobile:platform-tizen",
    ":wear",
    ":serializers",
    ":samples:mobile",
    ":samples:wearos"
)

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
