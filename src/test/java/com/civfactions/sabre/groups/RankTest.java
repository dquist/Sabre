package com.civfactions.sabre.groups;

import static org.junit.Assert.*;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class RankTest {

	@Test
	public void test() {
		Throwable e = null;
		try { Rank.fromString(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}

}
