package com.shankarsan.seat.availability.service.common;

public interface UtilityCommonService {

    void test();

    default void test1() {
        System.out.println("inside test1");
    }
}
