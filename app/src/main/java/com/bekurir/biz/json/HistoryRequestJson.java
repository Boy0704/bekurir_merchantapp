package com.bekurir.biz.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ourdevelops Team on 10/13/2019.
 */

public class HistoryRequestJson {

    @SerializedName("phone_number")
    @Expose
    private String notelepon;

    @SerializedName("idmerchant")
    @Expose
    private String idmerchant;

    @SerializedName("day")
    @Expose
    private String day;

    public String getNotelepon() {
        return notelepon;
    }

    public void setNotelepon(String notelepon) {
        this.notelepon = notelepon;
    }

    public String getIdmerchant() {
        return idmerchant;
    }

    public void setIdmerchant(String idmerchant) {
        this.idmerchant = idmerchant;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
