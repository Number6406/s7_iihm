/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloader.ui;

import downloader.fc.Downloader;

/**
 *
 * @author abonnena & bonhourg
 */
public class DownloadThread extends Thread {
 
    Downloader downloader;
    String filename;

    public DownloadThread(Downloader dl) {
        this.downloader = dl;
    }
    
    
    @Override
    public void run() {
        downloader.execute();
    }
    
    public String getFileName() {
        return this.filename;
    }
    
}
