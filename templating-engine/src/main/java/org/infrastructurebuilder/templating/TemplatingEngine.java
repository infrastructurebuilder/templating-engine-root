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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.maven.project.MavenProject;

public interface TemplatingEngine {
  public static final String EXECUTION_IDENTIFIER = "execution_identifier";

  public static List<String> getPropertyNames(final Properties base) {
    final List<String> l = new ArrayList<>();
    final Enumeration<?> n = base.propertyNames();
    while (n.hasMoreElements()) {
      l.add((String) n.nextElement());
    }
    return l;
  }

  public static Properties mergeProperties(final Properties... props) {
    final Properties p = new Properties();
    for (final Properties a : props) {
      for (final String n : getPropertyNames(a)) {
        p.setProperty(n, a.getProperty(n));
      }
    }
    return p;
  }

  public static boolean noComment(final String s) {
    return !(s.trim().length() == 0 || s.startsWith("#") || s.startsWith("//"));
  }

  public static String trimToString(final Object o) {
    return o.toString().trim();
  }

  Optional<String> execute() throws TemplatingEngineException;

  Path getExecutionSource();

  /**
   * This value, if present, must represent a <b><i>relative path</i></b> to
   * @return
   */
  Optional<Path> getPrefixPath();

  Optional<MavenProject> getProject();

  Properties getProperties();

  String getSourcePathRoot();

  boolean isCaseSensitive();

  boolean isIncludeDotFiles();

  boolean isIncludeHiddenFiles();

}