/**
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

import static org.infrastructurebuilder.templating.DummyAbstractMavenBackedPropertiesSupplier.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.UUID;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

public class AbstractMavenBackedPropertiesSupplierTest {

  private DummyAbstractMavenBackedPropertiesSupplier amp;
  private MavenProject mp;
  private Properties props;
  private Model mm;
  private Properties current;
  private int mphash;
  private String x;

  @Before
  public void setUp() throws Exception {
    x = UUID.randomUUID().toString();
    props = new Properties();
    props.setProperty(TYPE, "jeff");
    mm = new Model();
    mm.setArtifactId("abc");
    mm.setGroupId("def");
    mm.setVersion("1.0");
    mm.setProperties(props);
    mp = new MavenProject(mm);
    mphash = mp.hashCode();
    current = new Properties();
    current.setProperty(x, "B");
    amp = new DummyAbstractMavenBackedPropertiesSupplier();
    amp.setMavenProject(mp);
    amp.setCurrentPropertiesValues(current);
  }

  @Test
  public void testGetCurrentProperties() {
    final Properties p = amp.getCurrentProperties();
    assertTrue(p.containsKey(x));
  }

  @Test
  public void testGetProject() {
    assertEquals(amp.getProject().get(), mp);
  }

  @Test
  public void testSetMavenProject() {
    final MavenProject m2 = new MavenProject(new Model());
    assertNotEquals(mp, m2);
  }

}
