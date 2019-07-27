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
import static org.infrastructurebuilder.templating.TemplatingEngine.getPropertyNames;
import static org.infrastructurebuilder.templating.TemplatingEngine.mergeProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.infrastructurebuilder.util.config.StringListSupplier;
import org.infrastructurebuilder.util.files.DefaultIBDirScannerSupplier;
import org.infrastructurebuilder.util.files.IBDirScannerSupplier;

abstract public class AbstractTemplatingEngine<T> implements TemplatingEngine {

  public final static List<String> SCM_NAMES = Arrays.asList(".svn", ".git", "cvs");

  private static final Collection<String> DEFAULT_SOURCE_LIST = new ArrayList<>(
      java.util.Arrays.asList("java", "kt", "scala", "groovy", "clj"));

  public static final boolean endsWith(final File f, final Collection<String> extensions) {
    final String s = f.getAbsolutePath().toLowerCase();
    if (extensions != null) {
      for (final String e : extensions) {
        if (s.endsWith(e))
          return true;
      }
    }
    return false;
  }

  public static String getDisplayedContext(final Optional<MavenProject> project, final Properties properties) {
    final Properties p = new Properties();
    // FIXME This isn't quite right
    if (project.isPresent()) {
      p.putAll(project.get().getProperties());
    }
    final Properties gp = mergeProperties(p, properties);
    final TreeMap<String, String> m = new TreeMap<>();
    getPropertyNames(gp).stream().sorted().forEach(n -> m.put(n, gp.getProperty(n)));

    return String.join("\n", m.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.toList()).toArray(new String[0]));
  }

  public static final Path getOutputFile(final Path vmFile, final Path sources, final Path outputDirectory)
      throws IOException {
    final String canoRoot = sources.toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
    final String abs = vmFile.toAbsolutePath().toString();
    String rel = abs.substring(canoRoot.length() + File.separator.length());
    final String relLow = rel.toLowerCase();
    for (final String suf : new String[] { ".vm", ".velo", ".velocity" }) {
      if (relLow.endsWith(suf)) {
        rel = rel.substring(0, rel.length() - suf.length());
        break;
      }
    }
    return outputDirectory.resolve(rel);
  }

  public static final boolean isSCMDir(final File f) {
    return SCM_NAMES.contains(Optional.ofNullable(f).map(File::getName).map(String::toLowerCase).orElse("_FALSE"));
  }

  public static void listVeloFiles(final File f, final Set<File> out, final boolean includeDotFiles,
      final boolean includeHidden) throws IOException {
    if (f.isHidden() && !includeHidden)
      return;

    final String n = f.getName().toLowerCase();
    if (f.isDirectory()) {
      if (isSCMDir(f))
        return;
      for (final File ff : f.listFiles()) {
        listVeloFiles(ff.getAbsoluteFile(), out, includeDotFiles, includeHidden);
      }
    } else if (f.isFile()) {
      if (includeDotFiles || !n.startsWith(".")) {
        out.add(f);
      }
    }
  }

  public static final String prependDot(String s) {
    if (s != null && !s.startsWith(".")) {
      s = "." + s;
    }
    return s;
  }

  public static final String processComments(final String source, final Function<String, String> processor) {
    final Pattern p = Pattern.compile("(?m)(?s)/\\*\\*?.*?\\*/");
    final Matcher m = p.matcher(source);
    final StringBuffer sb = new StringBuffer();
    while (m.find()) {
      final String comment = m.group();
      final String replacement = processor.apply(comment);
      m.appendReplacement(sb, "");
      sb.append(replacement);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static final String quoteSharpsInComments(final String source) {
    return processComments(source, f -> f.replaceAll("(?<=\\w)#", "\\\\#"));
  }

  public static final String unquoteSharpsInComments(final String source) {
    return processComments(source, f -> f.replaceAll("\\\\#", "#"));
  }

  private final Path executionSource;

  private final String sourcePathRoot;

  private final boolean includeDotFiles;

  private final Log log;

  private final Collection<String> _sourceExtensions;

  private final Path sourcesOutputDirectory;

  private final MavenProject project;

  private final Properties properties = new Properties();

  private final boolean includeHiddenFiles;

  private final boolean caseSensitive;

  private final Optional<Path> prefixPath;

  public AbstractTemplatingEngine(final File src, final String sourcePathRoot,
      // Incldue dot files
      final boolean includeDotFiles,
      // Logging
      final Optional<Log> log,
      // These files are called "sources"
      final Optional<Collection<String>> sourceExtensions,
      // Target location
      final Path sourceOutputDir,
      // Maven project for properties
      final MavenProject project,
      // ALSO includeehidden?
      final boolean includeHiddenFiles,
      // Match case sensitive
      final boolean caseSensitive,
      // Prepend this to the output path
      final Optional<Path> prefixPath) {
    super();
    this.executionSource = requireNonNull(src).toPath();
    this.sourcePathRoot = requireNonNull(sourcePathRoot);
    this.includeDotFiles = includeDotFiles;
    this.log = requireNonNull(log).orElse(new DefaultLog(new ConsoleLogger(0, "name")));
    this._sourceExtensions = requireNonNull(sourceExtensions).orElse(DEFAULT_SOURCE_LIST).stream()
        .map(AbstractTemplatingEngine::prependDot).collect(Collectors.toSet());
    this.sourcesOutputDirectory = requireNonNull(sourceOutputDir);
    this.project = requireNonNull(project);
    this.includeHiddenFiles = includeHiddenFiles;
    this.caseSensitive = caseSensitive;
    this.prefixPath = requireNonNull(prefixPath);
    this.prefixPath.ifPresent(pp -> {
      if (pp.isAbsolute())
        throw new TemplatingEngineException("Prefix path " + pp + " is not relative");
    });
  }

  abstract public T createEngine(String sourcePathRoot) throws Exception;

  @Override
  public Optional<String> execute() throws TemplatingEngineException {
    final StringListSupplier includes = () -> Arrays.asList("**/*");
    final StringListSupplier excludes = () -> new ArrayList<>(SCM_NAMES);
    final IBDirScannerSupplier ss = new DefaultIBDirScannerSupplier(
        //
        () -> getExecutionSource(),
        //
        includes,
        //
        excludes,
        // You can't process directories
        () -> true,
        // Add the other stuff
        () -> !isIncludeHiddenFiles(),
        //
        () -> !isIncludeDotFiles(),
        //
        () -> isCaseSensitive());
    final Set<Path> paths = ss.get().scan()
        // Fetch the "included" files
        .get(true)
        //
        .stream()
        //
        .filter(f -> !f.endsWith("~"))
        //
        .filter(f -> !f.endsWith(".bak"))
        //
        .map(Path::toAbsolutePath)
        //
        .collect(Collectors.toSet());
    getLog().debug("Found " + paths.size() + " files in '" + getExecutionSource() + "'...");
    Properties p = new Properties();

    if (getProject().isPresent()) {
      p = getProject().get().getProperties();
    } else {
      p = new Properties();
    }
    p.setProperty("LB", "${");
    p.setProperty("RB", "}");
    getLog().debug("Got Maven getProperties() : " + p);
    getLog().debug("Got getProperties() : " + getProperties());

    if (paths.isEmpty())
      return Optional.empty();

    final String displayedContext = getDisplayedContext(getProject(), getProperties());

    for (final Path file : paths) {
      try {
        final Path outputDirectory = getOutputDirectoryForFile(file);

        final Path outFile = getOutputFile(file, getExecutionSource(), outputDirectory);
        getLog().debug("Executing template '" + file + "'...");

        String originalFile = file.toAbsolutePath().toString();
        originalFile = originalFile.substring(getSourcePathRoot().length());
        if (originalFile.startsWith(File.separator)) {
          originalFile = originalFile.substring(File.separator.length());
        }

        final T engine = createEngine(getSourcePathRoot());
        writeTemplate(engine, originalFile, outFile.toFile());

      } catch (final Exception ex) {
        throw new TemplatingEngineException("Failed to execute template '" + file + "'", ex);
      }
    }

    return Optional.ofNullable(displayedContext);
  }

  @Override
  public Path getExecutionSource() {
    return this.executionSource;
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
  public String getSourcePathRoot() {
    return sourcePathRoot;
  }

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

  abstract public void writeTemplate(T engine, String canoTemplate, File outFile) throws Exception;

  protected Log getLog() {
    return this.log;
  }

  protected Path getOutputDirectoryForFile(final Path file) {
    return this.sourcesOutputDirectory;
  }

}