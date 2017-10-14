package com.example.parktaeim.cameraforblind.Model;

/**
 * Created by parktaeim on 2017. 10. 14..
 */

public class SettingsItem {
    private int icon;
    private String title;

    public SettingsItem(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public SettingsItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
