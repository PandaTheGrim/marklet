# Package io.github.atlascommunity.marklet
**Marklet** is a custom Java Doclet which aims to generate a
 Javadoc in a markdown format which is ready to use in GitHub.
## Classes
| Name | Description |
| --- | --- |
| [PackagePageBuilder](PackagePageBuilder.md) | Builder that aims to create documentation page for a given ``package``.
 consists in a package description followed by type listing over following categories :

 * Classes * Interfaces * Enumerations * Annotations |
| [MarkletOptions](MarkletOptions.md) | Class that reads and stores provided options for javadoc execution.
 are :

 * `-d` specifies the output directory (default: `javadocs`) * `-e` specifies the file ending
 for files to be created (default `md`) * `-l` specifies the file ending used in internal links
 (default `md`)

 > The default options are ideal if you want to serve the documentation using GitHub's >
 built-in README rendering. If you are using a tool like Slate, change the options as follows: ```
 $ javadoc -doclet fr.faylixe.marklet.Marklet -e html.md -l html … ``` |
| [MarkletDocumentBuilder](MarkletDocumentBuilder.md) | Custom {@link MarkdownDocumentBuilder} implementation that aims to be used for building Marklet
 generated document. |
| [Marklet](Marklet.md) | Marklet entry point.
 doclet API in order to be called by the javadoc tool. |
| [MarkdownDocumentBuilder](MarkdownDocumentBuilder.md) | This class aims to build Markdown document.
 instance which will contains our document content. |
| [ClassPageBuilder](ClassPageBuilder.md) | Builder that aims to create documentation page for a given ``class``.
 javadoc generation, it will contains a class summary, followed by details about class field,
 constructor, and methods. |


