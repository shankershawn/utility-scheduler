package com.shankarsan.utilityscheduler.service.common;

public interface UtilityCommonService {

    void test();

    default void test1() {
        System.out.println("inside test1");
    }
}
