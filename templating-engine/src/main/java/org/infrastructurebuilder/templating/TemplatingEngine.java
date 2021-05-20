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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.maven.project.MavenProject;

public interface TemplatingEngine {
  public static final String EXECUTION_IDENTIFIER = "execution_identifier";

  public static List<String> getPropertyNames(final Map<String,Object> base) {
    final List<String> l = new ArrayList<>();
    base.keySet().stream().forEach(l::add);
    return l;
  }

  @SafeVarargs
  public static Map<String,Object> mergeProperties(final Map<String,Object>... props) {
    final Map<String,Object> p = new HashMap<>();
    for (final Map<String,Object> a : props) {
      for (final String n : a.keySet()) {
        p.put(n, a.get(n));
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

  /**
   * Map String--Object and likely to be turned into String, Object.toString() map later
   * @return
   */
  Map<String,Object> getProperties();

  Path getSourcePathRoot();

  boolean isCaseSensitive();

  boolean isIncludeDotFiles();

  boolean isIncludeHiddenFiles();

}