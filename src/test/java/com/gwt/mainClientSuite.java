package com.gwt;

import com.gwt.client.mainClientTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class mainClientSuite extends GWTTestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for gwt");
    suite.addTestSuite(mainClientTest.class);
    return suite;
  }
}
