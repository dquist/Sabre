package com.civfactions.sabre.snitch;

import java.util.Comparator;

public class DateComparator implements Comparator<SnitchLogEntry> {
	
	@Override
    public int compare(SnitchLogEntry o1, SnitchLogEntry o2) {
        return o2.time.compareTo(o1.time);
    }
}
