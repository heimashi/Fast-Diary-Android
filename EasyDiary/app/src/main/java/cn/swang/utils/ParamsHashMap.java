package cn.swang.utils;

import java.util.HashMap;

/**
 * Created by sw on 2015/9/9.
 */
public class ParamsHashMap extends HashMap{

    public ParamsHashMap with(String key, String value){
        put(key,value);
        return this;
    }
}
