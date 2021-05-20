/*
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infrastructurebuilder.templating;

import static org.infrastructurebuilder.templating.AbstractTemplatingEngine.quoteSharpsInComments;
import static org.infrastructurebuilder.templating.AbstractTemplatingEngine.unquoteSharpsInComments;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 *
 * @author ochafik
 */
public class UtilsTest {
  @Test
  public void testExceptions() {
    assertNotNull(new TemplatingEngineException());
    assertNotNull(new TemplatingEngineException("String"));
    assertNotNull(new TemplatingEngineException(new RuntimeException("String")));
    assertNotNull(new TemplatingEngineException("String", new RuntimeException("String")));
  }

  @Test
  public void testQuote() {
    assertEquals(quoteSharpsInComments("#"), "#");
    assertEquals("a#\n/*\nb\\#c*/", quoteSharpsInComments("a#\n/*\nb#c*/"));
    assertEquals("a#\n/*\n#b*/", quoteSharpsInComments("a#\n/*\n#b*/"));
    assertEquals("a#\n/*\n#c*/", unquoteSharpsInComments("a#\n/*\n\\#c*/"));
  }
}