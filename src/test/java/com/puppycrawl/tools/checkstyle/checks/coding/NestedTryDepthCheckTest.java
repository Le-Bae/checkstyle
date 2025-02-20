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

package com.puppycrawl.tools.checkstyle.checks.coding;

import static com.puppycrawl.tools.checkstyle.checks.coding.NestedTryDepthCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;

public class NestedTryDepthCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/checks/coding/nestedtrydepth";
    }

    @Test
    public void testDefault() throws Exception {

        final String[] expected = {
            "29:17: " + getCheckMessage(MSG_KEY, 2, 1),
            "40:17: " + getCheckMessage(MSG_KEY, 2, 1),
            "41:21: " + getCheckMessage(MSG_KEY, 3, 1),
        };

        verifyWithInlineConfigParser(
                getPath("InputNestedTryDepth.java"), expected);
    }

    @Test
    public void testCustomizedDepth() throws Exception {

        final String[] expected = {
            "41:21: " + getCheckMessage(MSG_KEY, 3, 2),
        };

        verifyWithInlineConfigParser(
                getPath("InputNestedTryDepthMax.java"), expected);
    }

    @Test
    public void testTokensNotNull() {
        final NestedTryDepthCheck check = new NestedTryDepthCheck();
        assertNotNull(check.getAcceptableTokens(), "Acceptable tokens should not be null");
        assertNotNull(check.getDefaultTokens(), "Default tokens should not be null");
        assertNotNull(check.getRequiredTokens(), "Required tokens should not be null");
    }

}
