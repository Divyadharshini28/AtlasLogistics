package com.atlaslogistics.ui;
public interface AppListener {
    void refreshAll();
    void log(String message);
    void showInfo(String message);
    void showError(String message);
    void showWarn(String message);
}
