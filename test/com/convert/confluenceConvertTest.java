package com.convert;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class confluenceConvertTest extends ConfluenceConvert {

    @Test
    public void testConvert() throws IOException{
	String expectedResult = FileUtils.readFileToString(new File("test/com/convert/test.mw")).trim();
	String input = FileUtils.readFileToString(new File("test/com/convert/test.confluence")).trim();
	assertEquals("Converted Text does not match the expected result",expectedResult, ConfluenceConvert.convert(input).trim());
    }
}
