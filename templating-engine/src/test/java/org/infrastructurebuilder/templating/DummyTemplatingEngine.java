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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.util.IBUtils;

public class DummyTemplatingEngine extends AbstractTemplatingEngine<DummyPassThru> implements TemplatingEngine {

  public DummyTemplatingEngine(final Path src, final Path sourcePathRoot, final boolean includeDotFiles, final Log log,
      final Collection<String> sourceExtensions, final Path sourceOutputDir, final MavenProject project,
      final boolean includeHiddenFiles, final boolean caseSensitive, final Optional<Path> prefixPath,
      final Supplier<Properties> ps) {
    super(src, sourcePathRoot, includeDotFiles, Optional.ofNullable(log), Optional.ofNullable(sourceExtensions),
        sourceOutputDir, project, includeHiddenFiles, caseSensitive, prefixPath, ps);
  }

  @Override
  public DummyPassThru createEngine(final Path sourcePathRoot) throws Exception {
    return new DummyPassThru(getExecutionSource(), sourcePathRoot, isIncludeDotFiles(), Optional.of(getLog()),
        Optional.of(getSourceExtensions()), getSourcesOutputDirectory().toFile(),
        getProject().orElse(new MavenProject()), isIncludeHiddenFiles(), isCaseSensitive(), getPrefixPath());
  }

  @Override
  public void writeTemplate(final DummyPassThru engine, final String canoTemplate, final Path outFile)
      throws Exception {
    Files.createDirectories(outFile.getParent());
    IBUtils.writeString(Objects.requireNonNull(outFile), canoTemplate);
  }
}
