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

import java.util.Map;
import java.util.Optional;

import org.apache.maven.project.MavenProject;

public class DummyAbstractMavenBackedPropertiesSupplier extends AbstractMavenBackedPropertiesSupplier {

  public static final String MAVEN_PROJECT = "MAVEN_PROJECT";
  public static final String TYPE = "TYPE";
  public static final String DUMMY = "DUMMY";
  private Optional<MavenProject> mp;

  @Override
  public Map<String, Object> get() {
    mp = getProject();
    final Map<String, Object> cp = getCurrentProperties();
    cp.put(TYPE, DUMMY);
    cp.put(MAVEN_PROJECT, mp);
    return cp;
  }

}
