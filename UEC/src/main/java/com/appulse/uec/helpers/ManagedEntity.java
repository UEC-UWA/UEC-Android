package com.appulse.uec.helpers;

import java.util.HashMap;

/**
 * Created by Matt on 29/12/2013.
 */
public class ManagedEntity {

    private HashMap<String,Object> values;

    private String entity;

    public ManagedEntity(String entity) {
        values = new HashMap<String, Object>();
        this.entity = entity;
    }

    public void setId(Integer id) {
        values.put("id",id);
    }

    public Integer getId() {
        return (Integer) values.get("id");
    }

    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    public Object getValue(String key) {
        if (values.containsKey(key)) {
            return values.get(key);
        }
        return "";
    }
}
