package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.annotation.ServiceOperation;
import com.accionmfb.omnix.core.commons.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PlatformUtils {

    @ServiceOperation(description = "Get the platform of the host machine")
    public static Platform getHostPlatform(){
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return Platform.WINDOWS;
        } else if (osName.contains("mac")) {
            return Platform.MACOS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return Platform.LINUX;
        } else {
            return Platform.OTHERS;
        }
    }

    @ServiceOperation(description = "Get the current wifi name of the passed host platform")
    public static String getCurrentConnectedWifiName(Platform hostPlatform){
        if(hostPlatform == Platform.WINDOWS){
            return getCurrentConnectedWifiForWindows();
        }
        else if (hostPlatform == Platform.MACOS){
            return getCurrentConnectedWifiForMacOs();
        }
        else {
            throw new RuntimeException("No support for the current platform: " + hostPlatform.name());
        }
    }

    @ServiceOperation(description = "Get the current connected wifi name of the current host platform")
    public static String getCurrentConnectedWifiName(){
        return getCurrentConnectedWifiName(getHostPlatform());
    }

    private static String getCurrentConnectedWifiForWindows(){
        try {
            Process process = Runtime.getRuntime().exec("netsh wlan show interfaces");
            return getWifiNameOnConnection(process);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getCurrentConnectedWifiForMacOs(){
        try {
            Process process = Runtime.getRuntime().exec("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I");
            return getWifiNameOnConnection(process);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getWifiNameOnConnection(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String ssid = null;
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("SSID")) {
                ssid = line.split(":")[1].trim();
                break;
            }
        }
        reader.close();
        if (ssid != null) {
            return ssid;
        } else {
            throw new RuntimeException("No connected wifi found");
        }
    }
}
