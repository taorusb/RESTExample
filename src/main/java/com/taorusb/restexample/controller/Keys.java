package com.taorusb.restexample.controller;

public enum Keys {

    URI_USER_ID(1), URI_COLLECTIONS(2), URI_COLLECTIONS_WITH_ID(3);

    private final int reqNum;

    Keys(final int reqNum) {
        this.reqNum = reqNum;
    }

    public int getReqNum() {
        return reqNum;
    }
}
