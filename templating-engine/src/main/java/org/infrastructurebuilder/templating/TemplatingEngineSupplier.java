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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * The contract of this Supplier is that it must return a thread-safe (i.e new independent
 * implementation of a TemplatingEngine) from every get()
 *
 * @author mykel.alvis
 *
 */
public interface TemplatingEngineSupplier extends Supplier<TemplatingEngine> {
  Path getExecutionSource();

  String getId();

  Optional<Log> getLog();

  Optional<Path> getPrefixPath();

  Optional<MavenProject> getProject();

  Properties getProperties();

  Path getSourcePathRoot();

  Path getSourcesOutputDirectory();

  boolean isCaseSensitive();

  boolean isIncludeDotFiles();

  boolean isIncludeHiddenFiles();

  void setCaseSensitive(boolean caseSensitive);

  void setExecutionSource(Path src) throws IOException;

  void setIncludeDotFiles(boolean includeDotFiles);

  void setIncludeHiddenFiles(boolean includeHidden);

  void setLog(Log log);

  void setPrefixPath(Optional<Path> prefixPath);

  void setProject(MavenProject project);

  void setProperties(Properties properties);

  void setSourceExtensions(Collection<String> sourceExtensions);

  void setSourcePathRoot(Path sourcePathRoot) throws IOException;

  void setSourcesOutputDirectory(Path sourcesOutputDirectory);

}
