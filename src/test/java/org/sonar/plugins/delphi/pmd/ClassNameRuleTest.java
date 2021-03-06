/*
 * Sonar Delphi Plugin
 * Copyright (C) 2015 Fabricio Colombo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.delphi.pmd;

import org.junit.Ignore;
import org.junit.Test;
import org.sonar.api.issue.Issue;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ClassNameRuleTest extends BasePmdRuleTest {

  @Test
  public void testValidRule() {
    DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
    builder.appendDecl("type");
    builder.appendDecl("  TMyClass = class");
    builder.appendDecl("  end;");

    analyse(builder);

    assertThat(issues, is(empty()));
  }

  @Test
  public void classNameWithoutPrefixShouldAddIssue() {
    DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
    builder.appendDecl("type");
    builder.appendDecl("  MyClass = class");
    builder.appendDecl("  end;");

    analyse(builder);

    assertThat(issues, hasSize(1));
    Issue issue = issues.get(0);
    assertThat(issue.ruleKey().rule(), equalTo("Class Name Rule"));
    assertThat(issue.line(), is(builder.getOffsetDecl() + 2));
  }

  @Test
  public void classNameDoNotStartsWithCapitalLetterShouldAddIssue() {
    DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
    builder.appendDecl("type");
    builder.appendDecl("  TmyClass = class");
    builder.appendDecl("  end;");

    analyse(builder);

    assertThat(issues, hasSize(1));
    Issue issue = issues.get(0);
    assertThat(issue.ruleKey().rule(), equalTo("Class Name Rule"));
    assertThat(issue.line(), is(builder.getOffsetDecl() + 2));
  }

  @Test
  public void testAvoidFalsePositive() {
    DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
    builder.appendDecl("type");
    builder.appendDecl("  TestClass = class");
    builder.appendDecl("  end;");

    analyse(builder);

    assertThat(issues, hasSize(1));
    Issue issue = issues.get(0);
    assertThat(issue.ruleKey().rule(), equalTo("Class Name Rule"));
    assertThat(issue.line(), is(builder.getOffsetDecl() + 2));
  }

  @Test
  @Ignore("One Class Per File Rule has a bug, and don't handle nested types")
  // TODO Fix One Class Per File Rule
  public void testNestedType() {
    DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
    builder.appendDecl("type");
    builder.appendDecl("  TTestClass = class");
    builder.appendDecl("  strict private");
    builder.appendDecl("    type");
    builder.appendDecl("      TValidClassName = class");
    builder.appendDecl("      end;");
    builder.appendDecl("      BadClassName = class");
    builder.appendDecl("      end;");
    builder.appendDecl("  end;");

    analyse(builder);

    assertThat(issues.toString(), issues, hasSize(1));
    Issue issue = issues.get(0);
    assertThat(issue.ruleKey().rule(), equalTo("Class Name Rule"));
    assertThat(issue.line(), is(builder.getOffsetDecl() + 7));
  }

  @Test
  public void acceptCapitalLetterEforExceptionClasses() {
    DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
    builder.appendDecl("type");
    builder.appendDecl("  EMyCustomException = class(Exception)");
    builder.appendDecl("  end;");

    analyse(builder);

    assertThat(issues, is(empty()));
  }
}
