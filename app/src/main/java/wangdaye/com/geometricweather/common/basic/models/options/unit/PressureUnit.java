package wangdaye.com.geometricweather.common.basic.models.options.unit;

import android.content.Context;

import wangdaye.com.geometricweather.R;

public enum PressureUnit {

    MB("mb", 0, 1f),
    KPA("kpa", 1, 0.1f),
    HPA("hpa", 2, 1f),
    ATM("atm", 3, 0.0009869f),
    MMHG("mmhg", 4, 0.75006f),
    INHG("inhg", 5, 0.02953f),
    KGFPSQCM("kgfpsqcm", 6, 0.00102f);

    private final String unitId;
    private final int unitArrayIndex;
    private final float unitFactor; // actual pressure = pressure(mb) * factor.

    PressureUnit(String id, int arrayIndex, float factor) {
        unitId = id;
        unitArrayIndex = arrayIndex;
        unitFactor = factor;
    }

    public String getUnitId() {
        return unitId;
    }

    public float getPressure(float mb) {
        return mb * unitFactor;
    }

    public String getPressureText(Context context, float mb) {
        return UnitUtils.formatFloat(mb * unitFactor) + "\u202f" +  getAbbreviation(context);
    }

    public String getAbbreviation(Context context) {
        return context.getResources().getStringArray(R.array.pressure_units)[unitArrayIndex];
    }

    public String getPressureVoice(Context context, float mb) {
        return UnitUtils.formatFloat(mb * unitFactor)
                + "\u202f" +  context.getResources().getStringArray(R.array.pressure_unit_voices)[unitArrayIndex];
    }

    public static PressureUnit getInstance(String value) {
        switch (value) {
            case "kpa":
                return KPA;

            case "hpa":
                return HPA;

            case "atm":
                return ATM;

            case "mmhg":
                return MMHG;

            case "inhg":
                return INHG;

            case "kgfpsqcm":
                return KGFPSQCM;

            default:
                return MB;
        }
    }
}
