package com.convert;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class confluenceConvertTest extends confluenceConvert {

    @Test
    public void testConvert() throws IOException{
	confluenceConvert tester = new confluenceConvert();
	String expectedResult = confluenceConvert.readFile("test/com/convert/test.mw").trim();
	String input = confluenceConvert.readFile("test/com/convert/test.confluence").trim();
	assertEquals("Converted Text",expectedResult, confluenceConvert.convert(input).trim());
    }

    @Test
    public void testGetFileList() {
	fail("Not yet implemented");	
    }

    @Test
    public void testGetConfluence() {
	fail("Not yet implemented");
    }

    @Test
    public void testMain() {
	fail("Not yet implemented");
    }

}
