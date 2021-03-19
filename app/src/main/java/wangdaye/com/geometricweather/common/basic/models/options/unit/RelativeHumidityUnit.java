package wangdaye.com.geometricweather.common.basic.models.options.unit;

public enum RelativeHumidityUnit {

    PERCENT("%");

    private final String unitAbbreviation;

    RelativeHumidityUnit(String abbreviation) {
        unitAbbreviation = abbreviation;
    }

    public String getRelativeHumidityText(float percent) {
        return UnitUtils.formatInt((int) percent) + "\u202f" +  unitAbbreviation;
    }
}
