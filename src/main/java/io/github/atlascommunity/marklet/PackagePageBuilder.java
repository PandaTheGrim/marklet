package io.github.atlascommunity.marklet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Supplier;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

import io.github.atlascommunity.marklet.constants.Filenames;
import io.github.atlascommunity.marklet.constants.Labels;

/**
 * Builder that aims to create documentation page for a given ``package``. Such documentation
 * consists in a package description followed by type listing over following categories :
 *
 * <p>* Classes * Interfaces * Enumerations * Annotations
 *
 * @author fv
 */
public final class PackagePageBuilder extends MarkletDocumentBuilder {

  /** Target package that page is built from. * */
  private final PackageDoc packageDoc;

  /**
   * Default constructor.
   *
   * @param packageDoc Target package that page is built from.
   * @param options doclet options
   */
  private PackagePageBuilder(final PackageDoc packageDoc, final MarkletOptions options) {

    super(packageDoc, options);
    this.packageDoc = packageDoc;
  }

  /**
   * Appends package header to the current document . Which consists in the package name, and the
   * package text description.
   */
  private void header() {

    header(1);
    text(Labels.PACKAGE);
    character(' ');
    text(packageDoc.name());
    newLine();
    description(packageDoc);
    newLine();
  }

  /**
   * Appends a class based index to the current document, namely list each type in a markdown table.
   * Such type could be either class, interface, or enumeration.
   *
   * @param label Label of the type categories.
   * @param classSupplier Type supplier.
   */
  private void classIndex(final String label, final Supplier<ClassDoc[]> classSupplier) {

    final ClassDoc[] classDocs = classSupplier.get();
    if (classDocs.length > 0) {
      header(2);
      text(label);
      newLine();
      tableHeader(Labels.NAME, "Description");
      Arrays.stream(classDocs).forEach(this::classRow);
      newLine();
    }
  }

  /**
   * Appends a class link row to the current index built in the current document.
   *
   * @param classDoc Class to append link from.
   */
  private void classRow(final ClassDoc classDoc) {

    startTableRow();
    classLink(packageDoc, classDoc);
    cell();
    text(classDoc.commentText().replace("\\\\n", " ").replaceFirst("\\..*", "."));
    endTableRow();
    newLine();
  }

  /**
   * Main package building process, build listing for the following type category :
   *
   * <p>* Classes * Interfaces * Enumerations * Annotations
   */
  private void indexes() {

    classIndex(Labels.ANNOTATIONS, packageDoc::annotationTypes);
    classIndex(Labels.ENUMERATIONS, packageDoc::enums);
    classIndex(Labels.INTERFACES, packageDoc::interfaces);
    classIndex(Labels.CLASSES, packageDoc::allClasses);
  }

  /**
   * Builds and writes the documentation file associated to the given ``packageDoc`` into the
   * directory denoted by the given ``directoryPath``.
   *
   * @param packageDoc Package to generated documentation for.
   * @param directoryPath Path of the directory to write documentation in.
   * @throws IOException If any error occurs while writing package page.
   */
  public static void build(
      final PackageDoc packageDoc, final Path directoryPath, final MarkletOptions options)
      throws IOException {

    final PackagePageBuilder packageBuilder = new PackagePageBuilder(packageDoc, options);
    packageBuilder.header();
    packageBuilder.indexes();

    // if we need to override main project README.md file
    if (options.isOverwriteProjectReadme()) {
      String root = new File("").getAbsolutePath();
      packageBuilder.build(Paths.get(root, Filenames.README_FILE), options);
    } else {
      packageBuilder.build(directoryPath.resolve(Filenames.README_FILE), options);
    }
  }
}
