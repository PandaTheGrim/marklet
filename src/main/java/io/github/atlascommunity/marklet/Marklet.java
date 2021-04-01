package io.github.atlascommunity.marklet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Marklet entry point. This class declares the {@link #start(RootDoc)} method required by the
 * doclet API in order to be called by the javadoc tool.
 *
 * @author fv
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Marklet {

  /** Command line options that have been parsed. * */
  private final MarkletOptions options;

  /** Documentation root provided by the doclet API. * */
  private final RootDoc root;

  /**
   * To document.
   *
   * @param option To document.
   * @return To document.
   */
  public static int optionLength(String option) {

    return MarkletOptions.optionLength(option);
  }

  /**
   * To document.
   *
   * @param options Options from command line.
   * @param reporter Reporter instance to use in case of error.
   * @return <tt>true</tt> if given set of options are valid, <tt>false</tt> otherwise.
   */
  public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {

    return MarkletOptions.validOptions(options, reporter);
  }

  /** @return LanguageVersion supported. */
  public static LanguageVersion languageVersion() {

    return LanguageVersion.JAVA_1_5;
  }

  /**
   * **Doclet** entry point. Parses user provided options and starts a **Marklet** execution.
   *
   * @param root Doclet API root.
   * @return ``true`` if the generation went well, ``false`` otherwise.
   */
  public static boolean start(final RootDoc root) {
    final MarkletOptions options = MarkletOptions.parse(root);
    final Marklet marklet = new Marklet(options, root);
    boolean result = false;
    try {
      result = marklet.start();
    } catch (final Exception e) {
      root.printError("An exception has been caught during generation (see stack trace below).");
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Builds and retrieves the path for the directory associated to the package with the given
   * <tt>name</tt>.
   *
   * @param packageName Name of the package to get directory for.
   * @return Built path.
   */
  private Path getPackageDirectory(final String packageName) {

    final String directory = packageName.replace('.', '/');

    return Paths.get(options.getOutputDirectory(), directory);
  }

  /**
   * Generates package documentation for the given ``packageDoc``.
   *
   * @param packageDoc Package to generate documentation for.
   * @throws IOException If any error occurs while creating file or directories.
   */
  private void generatePackage(final PackageDoc packageDoc) throws IOException {

    final String name = packageDoc.name();
    root.printNotice("Generates package documentation for " + name);
    if (!name.isEmpty()) {
      final Path directoryPath = getPackageDirectory(name);
      if (!Files.exists(directoryPath)) {
        Files.createDirectories(directoryPath);
      }
      PackagePageBuilder.build(packageDoc, directoryPath, options);
    }
  }

  /**
   * Generates documentation file for each package.
   *
   * @throws IOException If any error occurs during generation process.
   */
  private void buildPackages() throws IOException {
    // TODO : Consider method root.specifiedPackages();
    final Set<PackageDoc> packages = new HashSet<>(); // TODO : Ensures
    for (final ClassDoc classDoc : root.classes()) {
      final PackageDoc packageDoc = classDoc.containingPackage();
      if (!packages.contains(packageDoc)) {
        packages.add(packageDoc);
        generatePackage(packageDoc);
      }
    }
  }

  /**
   * Generates documentation file for each classes, enumerations, interfaces, or annotations.
   *
   * @throws IOException If any error occurs during generation process.
   */
  private void buildClasses() throws IOException {

    for (final ClassDoc classDoc : root.classes()) {
      final PackageDoc packageDoc = classDoc.containingPackage();
      final String packageName = packageDoc.name();
      final Path packageDirectory = getPackageDirectory(packageName);
      root.printNotice("Generates documentation for " + classDoc.name());
      ClassPageBuilder.build(classDoc, packageDirectory, options);
    }
  }

  /** @return <tt>true</tt> if generation was successfull, <tt>false</tt> otherwise. */
  private boolean start() {

    try {
      final Path outputDirectory = Paths.get(options.getOutputDirectory());
      root.printNotice("Target output directory : " + outputDirectory.toAbsolutePath().toString());
      if (!Files.exists(outputDirectory)) Files.createDirectories(outputDirectory);
      buildPackages();
      buildClasses();
    } catch (final IOException e) {
      root.printError(e.getMessage());
      return false;
    }
    return true;
  }
}
