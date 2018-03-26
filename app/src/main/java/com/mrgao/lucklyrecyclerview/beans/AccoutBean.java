package com.mrgao.lucklyrecyclerview.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Package:    com.chenwei.lucklynote.modules.main.model.beans
 * Create Date:2018/3/7
 * Project Name:LucklyChenAccount
 * Description:
 */

public class AccoutBean implements Parcelable {
    String id;
    int type;//0:收入,1:支出
    String money;//钱的数量
    String moneyType;//账户钱的来源
    String accountType;//账户类型：比如支付宝
    String time;//时间
    String people;//成员
    String descript;//备注
    int imageResourceId;

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.money);
        dest.writeString(this.moneyType);
        dest.writeString(this.accountType);
        dest.writeString(this.time);
        dest.writeString(this.people);
        dest.writeString(this.descript);
        dest.writeInt(this.imageResourceId);
    }

    public AccoutBean() {
    }

    protected AccoutBean(Parcel in) {
        this.id = in.readString();
        this.type = in.readInt();
        this.money = in.readString();
        this.moneyType = in.readString();
        this.accountType = in.readString();
        this.time = in.readString();
        this.people = in.readString();
        this.descript = in.readString();
        this.imageResourceId = in.readInt();
    }

    public static final Creator<AccoutBean> CREATOR = new Creator<AccoutBean>() {
        @Override
        public AccoutBean createFromParcel(Parcel source) {
            return new AccoutBean(source);
        }

        @Override
        public AccoutBean[] newArray(int size) {
            return new AccoutBean[size];
        }
    };
}
