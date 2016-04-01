package com.markLogic.weather;

/**
 * Created by ariellev on 01.04.16.
 */
public interface IPlaceBuilder {

    Place getPlace(String placeName);

    Place parse(String str);

}
