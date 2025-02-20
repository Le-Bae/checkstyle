////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2021 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle;

import static com.puppycrawl.tools.checkstyle.JavadocDetailNodeParser.MSG_JAVADOC_MISSED_HTML_CLOSE;
import static com.puppycrawl.tools.checkstyle.JavadocDetailNodeParser.MSG_JAVADOC_PARSE_RULE_ERROR;
import static com.puppycrawl.tools.checkstyle.JavadocDetailNodeParser.MSG_JAVADOC_WRONG_SINGLETON_TAG;
import static com.puppycrawl.tools.checkstyle.internal.utils.TestUtil.isUtilsClassHasPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.JavadocDetailNodeParser.ParseErrorMessage;
import com.puppycrawl.tools.checkstyle.api.Violation;
import com.puppycrawl.tools.checkstyle.internal.utils.TestUtil;

public class DetailNodeTreeStringPrinterTest extends AbstractTreeTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/detailnodetreestringprinter";
    }

    @Test
    public void testIsProperUtilsClass() throws ReflectiveOperationException {
        assertTrue(isUtilsClassHasPrivateConstructor(DetailNodeTreeStringPrinter.class, true),
                "Constructor is not private");
    }

    @Test
    public void testParseFile() throws Exception {
        verifyJavadocTree(getPath("ExpectedDetailNodeTreeStringPrinterJavadocComment.txt"),
                getPath("InputDetailNodeTreeStringPrinterJavadocComment.javadoc"));
    }

    @Test
    public void testParseFileWithError() throws Exception {
        final File file = new File(
                getPath("InputDetailNodeTreeStringPrinterJavadocWithError.javadoc"));
        try {
            DetailNodeTreeStringPrinter.printFileAst(file);
            fail("Javadoc parser didn't fail on missing end tag");
        }
        catch (IllegalArgumentException ex) {
            final String expected = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                    "getParseErrorMessage",
                    new ParseErrorMessage(0, MSG_JAVADOC_MISSED_HTML_CLOSE, 1, "qwe"));
            assertEquals(expected, ex.getMessage(),
                    "Generated and expected parse error messages don't match");
        }
    }

    @Test
    public void testNoUnnecessaryTextInJavadocAst() throws Exception {
        verifyJavadocTree(
                getPath("ExpectedDetailNodeTreeStringPrinterNoUnnecessaryTextInJavadocAst.txt"),
                getPath("InputDetailNodeTreeStringPrinterNoUnnecessaryTextInJavadocAst.javadoc"));
    }

    @Test
    public void testMissedHtmlTagParseErrorMessage() throws Exception {
        final String actual = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                "getParseErrorMessage",
                new ParseErrorMessage(35, MSG_JAVADOC_MISSED_HTML_CLOSE, 7, "xyz"));
        final Violation violation = new Violation(
                35,
                "com.puppycrawl.tools.checkstyle.checks.javadoc.messages",
                MSG_JAVADOC_MISSED_HTML_CLOSE,
                new Object[] {7, "xyz"},
                "",
                DetailNodeTreeStringPrinter.class,
                null);
        final String expected = "[ERROR:35] " + violation.getViolation();
        assertEquals(expected, actual,
                "Javadoc parse error violation for missed HTML tag doesn't meet expectations");
    }

    @Test
    public void testParseErrorMessage() throws Exception {
        final String actual = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                "getParseErrorMessage",
                new ParseErrorMessage(10, MSG_JAVADOC_PARSE_RULE_ERROR,
                        9, "no viable alternative at input ' xyz'", "SOME_JAVADOC_ELEMENT"));
        final Violation violation = new Violation(
                10,
                "com.puppycrawl.tools.checkstyle.checks.javadoc.messages",
                MSG_JAVADOC_PARSE_RULE_ERROR,
                new Object[] {9, "no viable alternative at input ' xyz'", "SOME_JAVADOC_ELEMENT"},
                "",
                DetailNodeTreeStringPrinter.class,
                null);
        final String expected = "[ERROR:10] " + violation.getViolation();
        assertEquals(expected, actual, "Javadoc parse error violation doesn't meet expectations");
    }

    @Test
    public void testWrongSingletonParseErrorMessage() throws Exception {
        final String actual = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                "getParseErrorMessage",
                new ParseErrorMessage(100, MSG_JAVADOC_WRONG_SINGLETON_TAG,
                        9, "tag"));
        final Violation violation = new Violation(
                100,
                "com.puppycrawl.tools.checkstyle.checks.javadoc.messages",
                MSG_JAVADOC_WRONG_SINGLETON_TAG,
                new Object[] {9, "tag"},
                "",
                DetailNodeTreeStringPrinter.class,
                null);
        final String expected = "[ERROR:100] " + violation.getViolation();
        assertEquals(expected, actual,
                "Javadoc parse error violation for void elements with close tag "
                    + "doesn't meet expectations");
    }

    @Test
    public void testUnescapedJavaCodeWithGenericsInJavadoc() throws Exception {
        final File file = new File(
                getPath("InputDetailNodeTreeStringPrinter"
                        + "UnescapedJavaCodeWithGenericsInJavadoc.javadoc"));
        try {
            DetailNodeTreeStringPrinter.printFileAst(file);
            fail("Exception is expected");
        }
        catch (IllegalArgumentException ex) {
            final String expected = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                    "getParseErrorMessage",
                    new ParseErrorMessage(35, MSG_JAVADOC_MISSED_HTML_CLOSE, 7, "parsing"));
            assertEquals(expected, ex.getMessage(),
                    "Generated and expected parse error messages don't match");
        }
    }

    @Test
    public void testNoViableAltException() throws Exception {
        final File file = new File(
                getPath("InputDetailNodeTreeStringPrinterNoViableAltException.javadoc"));
        try {
            DetailNodeTreeStringPrinter.printFileAst(file);
            fail("Exception is expected");
        }
        catch (IllegalArgumentException ex) {
            final String expected = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                    "getParseErrorMessage",
                    new ParseErrorMessage(0, MSG_JAVADOC_PARSE_RULE_ERROR,
                            9, "no viable alternative at input '<<'", "HTML_ELEMENT"));
            assertEquals(expected, ex.getMessage(),
                    "Generated and expected parse error messages don't match");
        }
    }

    @Test
    public void testHtmlTagCloseBeforeTagOpen() throws Exception {
        final File file = new File(
                getPath("InputDetailNodeTreeStringPrinterHtmlTagCloseBeforeTagOpen.javadoc"));
        try {
            DetailNodeTreeStringPrinter.printFileAst(file);
            fail("Exception is expected");
        }
        catch (IllegalArgumentException ex) {
            final String expected = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                    "getParseErrorMessage",
                    new ParseErrorMessage(0, MSG_JAVADOC_PARSE_RULE_ERROR,
                            4, "no viable alternative at input '</tag'", "HTML_ELEMENT"));
            assertEquals(expected, ex.getMessage(),
                    "Generated and expected parse error messages don't match");
        }
    }

    @Test
    public void testWrongHtmlTagOrder() throws Exception {
        final File file = new File(
                getPath("InputDetailNodeTreeStringPrinterWrongHtmlTagOrder.javadoc"));
        try {
            DetailNodeTreeStringPrinter.printFileAst(file);
            fail("Exception is expected");
        }
        catch (IllegalArgumentException ex) {
            final String expected = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                    "getParseErrorMessage",
                    new ParseErrorMessage(0, MSG_JAVADOC_MISSED_HTML_CLOSE, 10, "tag2"));
            assertEquals(expected, ex.getMessage(),
                    "Generated and expected parse error messages don't match");
        }
    }

    @Test
    public void testOmittedStartTagForHtmlElement() throws Exception {
        final File file = new File(
                getPath("InputDetailNodeTreeStringPrinterOmittedStartTagForHtmlElement.javadoc"));
        try {
            DetailNodeTreeStringPrinter.printFileAst(file);
            fail("Exception is expected");
        }
        catch (IllegalArgumentException ex) {
            final String expected = TestUtil.invokeStaticMethod(DetailNodeTreeStringPrinter.class,
                    "getParseErrorMessage",
                    new ParseErrorMessage(0, MSG_JAVADOC_MISSED_HTML_CLOSE, 3, "a"));
            assertEquals(expected, ex.getMessage(),
                    "Generated and expected parse error messages don't match");
        }
    }

}
