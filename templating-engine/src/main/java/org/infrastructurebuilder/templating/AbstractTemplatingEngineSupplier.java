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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

abstract public class AbstractTemplatingEngineSupplier<T extends TemplatingEngine> implements TemplatingEngineSupplier {

  private Path src;
  private Path sourcePathRoot;
  private boolean includeDotFiles;
  private boolean includeHiddenFiles;
  private boolean caseSensitive;
  private Properties properties;
  private MavenProject project;
  private Path sourcesOutputDirectory;
  private Collection<String> _sourceExtensions;
  private Log log;

  private Optional<Path> prefixPath = Optional.empty(); // defaults to nothing?

  @Override
  abstract public TemplatingEngine get();

  @Override
  public Path getExecutionSource() {
    return this.src;
  }

  @Override
  public Optional<Log> getLog() {
    return Optional.ofNullable(log);
  }

  @Override
  public Optional<Path> getPrefixPath() {
    return this.prefixPath;
  }

  @Override
  public Optional<MavenProject> getProject() {
    return Optional.ofNullable(project);
  }

  @Override
  public Properties getProperties() {
    return properties;
  }

  public Collection<String> getSourceExtensions() {
    return this._sourceExtensions;
  }

  @Override
  public Path getSourcePathRoot() {
    return sourcePathRoot;
  }

  @Override
  public Path getSourcesOutputDirectory() {
    return sourcesOutputDirectory;
  }

  @Override
  public boolean isCaseSensitive() {
    return this.caseSensitive;
  }

  @Override
  public boolean isIncludeDotFiles() {
    return this.includeDotFiles;
  }

  @Override
  public boolean isIncludeHiddenFiles() {
    return this.includeHiddenFiles;
  }

  @Override
  public void setCaseSensitive(final boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  @Override
  public void setExecutionSource(final Path src) throws IOException {
    this.src = src.toRealPath();
  }

  @Override
  public void setIncludeDotFiles(final boolean includeDotFiles) {
    this.includeDotFiles = includeDotFiles;
  }

  @Override
  public void setIncludeHiddenFiles(final boolean include) {
    this.includeHiddenFiles = include;
  }

  @Override
  public void setLog(final Log log) {
    this.log = log;
  }

  @Override
  public void setPrefixPath(final Optional<Path> prefixPath) {
    this.prefixPath = requireNonNull(prefixPath);
  }

  @Override
  public void setProject(final MavenProject project) {
    this.project = project;
  }

  @Override
  public void setProperties(final Properties properties) {
    this.properties = properties;
  }

  @Override
  public void setSourceExtensions(final Collection<String> sourceExtensions) {
    this._sourceExtensions = sourceExtensions;
  }

  @Override
  public void setSourcePathRoot(final Path sourcePathRoot) throws IOException {
    this.sourcePathRoot = sourcePathRoot.toAbsolutePath().toRealPath();
  }

  @Override
  public void setSourcesOutputDirectory(final Path sourcesOutputDirectory) {
    this.sourcesOutputDirectory = sourcesOutputDirectory;
  }
}
