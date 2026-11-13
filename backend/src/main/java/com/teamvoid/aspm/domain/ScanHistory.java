package com.teamvoid.aspm.domain;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class ScanHistory {

    private final int SCANS_PER_PAGE = 10;

    private User scanOwner;
    private List<Scan> scans;

    
    public ScanHistory(User scanOwner) {
        this.scanOwner = scanOwner;     //To find ScanHistory by whose scans they are.
        this.scans = new ArrayList<Scan>();
    }

    public void AddScan(Scan scan) {
        scans.add(scan);
    }

    public User getScanOwner() {
        return scanOwner;
    }

    public List<Scan> getScans() {
        return new ArrayList<Scan>(scans);
    }

    public List<Scan> getScansPage(int pageNo) {
        List<Scan> page = new ArrayList<Scan>();
        int count = scans.size();
        if (pageNo < 1)     //Not a valid page number.
            return page;
        pageNo--;       //Page 1 is the first page.  This adjusts for that.
        if (pageNo * SCANS_PER_PAGE < count){
            count -= pageNo * SCANS_PER_PAGE;
            for (int i = 0; i < count && i < SCANS_PER_PAGE; i++) {
                page.add(scans.get(pageNo * SCANS_PER_PAGE + i));
            }
        }
        return page;
    }

    public void removeScan (UUID scanId) {
        scans.removeIf(s -> s.id().equals(scanId));
    }
}
