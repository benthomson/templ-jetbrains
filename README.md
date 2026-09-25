# Templ Jetbrains

<!-- Plugin description -->
Support for the [Templ Programming Language](https://templ.guide/)

## Features
- Basic LSP Support
- HTML editing support
<!-- Plugin description end -->

## Prerequisites
- [gopls](https://pkg.go.dev/golang.org/x/tools/gopls)
- [templ](https://templ.guide/quick-start/installation/)

## Building and installing in GoLand

You need JDK 21 or newer. GoLand ships one (the JetBrains Runtime), so you don't need to install Java separately:

```
export JAVA_HOME=/Applications/GoLand.app/Contents/jbr/Contents/Home   # macOS
```

On Linux the runtime is in `<GoLand install dir>/jbr`. On Windows it is in `<GoLand install dir>\jbr`.

1. If you changed anything under `./grammars`, rebundle it first (see [bundle-vscode](#bundle-vscode)). The plugin loads the grammars from `src/main/resources/tm-bundle.zip`, not from `./grammars`.
2. Build the plugin:

   ```
   ./gradlew buildPlugin
   ```

   This writes `build/distributions/templ-jetbrains-<version>.zip`.
3. In GoLand, open **Settings → Plugins**, click the gear icon, choose **Install Plugin from Disk…** and select the zip. Don't unzip it first.
4. Restart GoLand when prompted.

The local build has the same plugin ID as the Marketplace release, so it replaces the release. If the Marketplace has a newer version, GoLand will offer to update, and updating overwrites your local build. The build targets GoLand 2026.2 and later (`pluginSinceBuild` in `gradle.properties`).

To try your changes without touching your main GoLand install, run `./gradlew runIde`. This opens a sandboxed GoLand with the plugin loaded.

## Tasks

### bundle-vscode

Directory: ./grammars

```
zip -rqq ../tm-bundle.zip *
mv ../tm-bundle.zip ../src/main/resources/
```

### Fix syntax highlighting

Highlighting comes from the TextMate grammars in `./grammars/Syntaxes`. The platform's TextMate lexer tokenises the file, and `TemplHighlighter` maps the innermost scope name of each token to an IDE colour. Two things to know when you edit the grammars:

- Every `begin`/`end` rule needs a `contentName`. Without one, the text between the delimiters gets a scope with no name and shows up uncoloured.
- `TemplHighlighter` only colours a fixed set of scopes. For example, the only `variable.*` scopes it colours are `variable.other.property` and `variable.other.object.property`. A scope it doesn't map is shown in the default text colour.

After editing, rebundle ([bundle-vscode](#bundle-vscode)), rebuild and reinstall.

### Fix PSI parsing and lexing issues

This project uses [Grammar-Kit](https://github.com/JetBrains/Grammar-Kit) for code generating the parser and lexer. A good starting point to understand how this works is following the Grammar-Kit documentation and the official [Custom Language Support Tutorial](https://plugins.jetbrains.com/docs/intellij/custom-language-support-tutorial.html). Another useful thing to know is that the HTML support is implemented with [TemplFileViewProvider](./src/main/kotlin/com/templ/templ/file/TemplFileViewProvider.kt) implementing the TemplateLanguageFileViewProvider following the [custom templating language plugin tutorial](https://intellij-support.jetbrains.com/hc/en-us/community/posts/206765105-Tutorial-Custom-templating-language-plugin). We use `HTML_FRAGMENT` Lexer tokens for the HTML parts of the templ files.

If this plugin does not parse Templ files correctly, a good starting point is to look at the output of "View PSI Structure of Current File..."-action to see how the file is parsed. If the Lexical token is incorrect at the point of the parsing error, you need to fix the lexer.

1. Edit the [_TemplLexer.flex](./src/main/grammar/_TemplLexer.flex) file. You might find the [JFlex manual](https://jflex.de/manual.html) useful.
2. Run JFlex Generator action to generate the _TemplLexer.java file.
3. Start the IDE with the `Run Plugin` configuration to see if the lexer works as expected. You can also run the [lexer test](./src/test/kotlin/com/templ/templ/parsing/TemplLexerTest.kt) and preferably add a new test case for the bug.

If the lexer token is correct, you need to modify the grammar which generates the parser code.

1. Edit the [TemplParser.bnf](./src/main/grammar/Templ.bnf) file.
2. Run the `Generate Parser Code` action to generate the parser code. This also generates the PSI classes but does not remove the old ones, so you might want to remove the `./src/main/gen/com/templ/templ/psi` directory before running this action.
3. Start the IDE with the `Run Plugin` configuration to see if the parser works as expected. You can also run the [parser test](./src/test/kotlin/com/templ/templ/parsing/TemplParsingTest.kt) and preferably add a new test case for the bug.
