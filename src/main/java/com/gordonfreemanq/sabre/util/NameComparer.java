package com.gordonfreemanq.sabre.util;

import java.util.Comparator;

public class NameComparer implements Comparator<INamed> {

	@Override
	public int compare(INamed arg0, INamed arg1) {
		return arg0.getName().compareToIgnoreCase(arg1.getName());
	}
}
