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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.Optional;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.util.PropertiesSupplier;

/**
 * This is the abstract root of a PropertieSupplier that generates a new Properties set
 * based on some runtime data.
 *
 * This component may be capable of post-configuration after creation.  The templating mojo
 * recognizes the type and configures it post-injection
 *
 * Utilization of this class requires that you create a concrete instance of this,
 * make it Named and set the code for that as a dependency of the execution of the mojo
 * @author mykel.alvis
 *
 */
abstract public class AbstractMavenBackedPropertiesSupplier implements PropertiesSupplier {

  private Optional<MavenProject> project;
  private final Properties currentProperties = new Properties();

  public final Properties getCurrentProperties() {
    return currentProperties;
  }

  public final Optional<MavenProject> getProject() {
    return project;
  }

  public final AbstractMavenBackedPropertiesSupplier setCurrentPropertiesValues(final Properties current) {
    currentProperties.putAll(requireNonNull(current));
    return this;
  }

  public AbstractMavenBackedPropertiesSupplier setMavenProject(final MavenProject project) {
    this.project = ofNullable(project).map(MavenProject::new);
    return this;
  }
}
