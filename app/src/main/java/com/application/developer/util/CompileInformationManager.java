package com.application.developer.util;

public class CompileInformationManager {
    private static CompileInformationManager instance;

    private CompileInformationManager() {
        System.out.println("Singleton has loaded");
    }

    public static CompileInformationManager getInstance() {
        if (instance == null) {
            synchronized (CompileInformationManager.class) {
                if (instance == null) {
                    instance = new CompileInformationManager();
                }
            }
        }
        return instance;
    }


    private String mCompileErrorMsg;

    public CompileInformationManager setCompileErrorMsg(String msg) {
        this.mCompileErrorMsg = msg;
        return instance;
    }

    public String getCompileErrorMsg() {
        return mCompileErrorMsg;
    }
}
