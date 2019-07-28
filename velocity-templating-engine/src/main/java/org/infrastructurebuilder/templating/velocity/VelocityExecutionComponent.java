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
package org.infrastructurebuilder.templating.velocity;

import static org.infrastructurebuilder.util.IBUtils.readFile;
import static org.infrastructurebuilder.util.IBUtils.writeString;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.infrastructurebuilder.templating.AbstractTemplatingEngine;

public class VelocityExecutionComponent extends AbstractTemplatingEngine<VelocityEngine> {

  static final VelocityContext createContext(final Optional<MavenProject> project, final Properties properties) {
    final VelocityContext context = new VelocityContext();
    context.put("project", project.orElse(null));

    Properties oo;
    oo = project.isPresent() ? project.get().getProperties() : new Properties();
    for (final Map.Entry<Object, Object> e : oo.entrySet()) {
      final String propName = ((String) e.getKey()).replace('.', '_'), propValue = (String) e.getValue();
      context.put(propName, propValue);
    }

    if (properties != null) {
      final Enumeration<?> enumeration = properties.propertyNames();
      while (enumeration.hasMoreElements()) {
        final String propName = (String) enumeration.nextElement();
        final String propValue = properties.getProperty(propName);
        context.put(propName, propValue);
      }
    }
    return context;
  }

  public VelocityExecutionComponent(final Path src, final Path sourcePathRoot, final boolean includeDotFiles,
      final Optional<Log> log, final Optional<Collection<String>> sourceExtensions, final Path sourceOutputDir,
      final MavenProject project, final boolean includeHiddenFiles, final boolean caseSensitive,
      final Optional<Path> prefixPath) {
    super(src, sourcePathRoot, includeDotFiles, log, sourceExtensions, sourceOutputDir, project,
        includeHiddenFiles, caseSensitive, prefixPath);
  }

  @Override
  public final VelocityEngine createEngine(final Path sourcePathRoot) throws Exception {
    final VelocityEngine ve = new VelocityEngine();
    ve.setProperty("velocimacro.inline.replace_global", "true");
    ve.setProperty("velocimacro.inline.local_scope", "false");
    ve.setProperty("velocimacro.context.localscope", "false");
    ve.setProperty("runtime.strict_mode.enable", "false");
    ve.setProperty("resource.loader.file.path", sourcePathRoot.toString());
    ve.init();
    return ve;
  }

  @Override
  public void writeTemplate(final VelocityEngine engine, final String canoTemplate, final Path outFile)
      throws Exception {
    final VelocityContext _context = createContext(getProject(), getProperties());
    try (StringWriter out = new StringWriter()) {
      final Path p = getSourcePathRoot();
      final boolean quoteComments = true;
      if (quoteComments) {
        final String quoted = quoteSharpsInComments(readFile(p.resolve(canoTemplate)));
        engine.evaluate(_context, out, "velocity", quoted);
      } else {
        final org.apache.velocity.Template template = engine.getTemplate(canoTemplate);
        template.merge(_context, out);
      }

      Files.createDirectories(outFile.getParent());
      writeString(outFile, unquoteSharpsInComments(out.toString()));
    }

  }

}
