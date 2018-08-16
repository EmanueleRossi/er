package org.erossi.er.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Catalog {
    private File destination;
    public File getDestination() { return destination; }
    public void setDestination(File destination) { this.destination = destination; }
    private List<BackupItem> items;    
    public List<BackupItem> getItems() { return items; }
    public void setItems(List<BackupItem> items) { this.items = items; }
    private List<String> exclusions;
    public List<String> getExclusions() { return exclusions; }
    public void setExclusions(List<String> exclusions) { this.exclusions = exclusions; }
    
    public Catalog() {
        items = new ArrayList<BackupItem>();
        exclusions = new ArrayList<String>();
    }
}