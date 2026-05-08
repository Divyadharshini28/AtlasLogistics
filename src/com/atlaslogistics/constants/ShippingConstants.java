package com.atlaslogistics.constants;

//this class contains constants that are used throughout the package
//imported to every class present in the package
public class ShippingConstants {
    public static final double BIKE_MAX_KG       = 5.0;
    public static final double VAN_MAX_KG        = 500.0;
    public static final double PLANE_MAX_KG      = 5000.0;
    public static final double BASE_RATE_PER_KM  = 0.08;   //rs per km per kg
    public static final String DATA_FILE         = "atlas_data.ser";
    public static final String APP_TITLE         = "AtlasLogistics — Control Panel";

    private ShippingConstants() {}
}
