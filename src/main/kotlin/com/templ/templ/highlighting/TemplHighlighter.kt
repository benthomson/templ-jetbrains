package com.templ.templ.highlighting

import com.goide.highlighting.GoSyntaxHighlightingColors
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType


class TemplHighlighter(lexer: TemplHighlightingLexer) : SyntaxHighlighterBase() {
    private val myLexer: TemplHighlightingLexer

    init {
        myLexer = lexer
    }

    override fun getHighlightingLexer(): Lexer {
        return myLexer
    }

    // Scopes are matched by prefix, so more specific scopes must come before the scopes they start with,
    // e.g. keyword.operator before keyword and entity.name.function.member before entity.name.function.
    override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey> {
        if (tokenType is TemplElementType) {
            val scope = tokenType.getScope().scopeName ?: ""

            if (scope.startsWith("punctuation.definition.comment")) return COMMENT
            if (scope.startsWith("comment")) return COMMENT

            if (scope.startsWith("keyword.operator")) return arrayOf(GoSyntaxHighlightingColors.OPERATOR)
            if (scope.startsWith("support.function.builtin")) return arrayOf(GoSyntaxHighlightingColors.BUILTIN_FUNCTION_CALL)
            if (scope.startsWith("entity.name.function.member")) return arrayOf(GoSyntaxHighlightingColors.EXPORTED_FUNCTION_CALL)
            if (scope.startsWith("entity.name.function")) return arrayOf(GoSyntaxHighlightingColors.LOCAL_FUNCTION_CALL)
            if (scope.startsWith("entity.name.package")) return arrayOf(GoSyntaxHighlightingColors.PACKAGE)
            if (scope.startsWith("entity.name.type")) return arrayOf(GoSyntaxHighlightingColors.TYPE_SPEC_REFERENCE)
            if (scope.startsWith("variable.other.object.property")) return arrayOf(GoSyntaxHighlightingColors.STRUCT_EXPORTED_MEMBER)
            if (scope.startsWith("variable.other.property")) return arrayOf(GoSyntaxHighlightingColors.STRUCT_EXPORTED_MEMBER)
            if (scope.startsWith("constant.numeric")) return arrayOf(GoSyntaxHighlightingColors.NUMBER)
            if (scope.startsWith("constant.language")) return arrayOf(GoSyntaxHighlightingColors.BUILTIN_CONSTANT)
            if (scope.startsWith("constant.other.placeholder")) return arrayOf(GoSyntaxHighlightingColors.VALID_STRING_ESCAPE)
            if (scope.startsWith("constant.character.escape")) return arrayOf(GoSyntaxHighlightingColors.VALID_STRING_ESCAPE)
            if (scope.startsWith("punctuation.separator.dot-access")) return arrayOf(GoSyntaxHighlightingColors.DOT)
            if (scope.startsWith("punctuation.other.comma")) return arrayOf(GoSyntaxHighlightingColors.COMMA)

            if (scope.startsWith("keyword")) return KEYWORD
            if (scope.startsWith("storage.type")) return KEYWORD

            if (scope.startsWith("string")) return STRING
            if (scope.startsWith("punctuation.definition.string")) return STRING
            if (scope.startsWith("entity.name.import")) return STRING


            if (scope.startsWith("entity.name.tag")) return arrayOf(XmlHighlighterColors.HTML_TAG_NAME)
            if (scope.startsWith("punctuation.definition.tag")) return arrayOf(XmlHighlighterColors.HTML_TAG)
            if (scope.startsWith("entity.other.attribute-name")) return arrayOf(XmlHighlighterColors.HTML_ATTRIBUTE_NAME)


            if (scope.startsWith("support.type.property-name")) return arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)
            return pack(null)
        }
        return pack(null)
    }
    private val KEYWORD = arrayOf(DefaultLanguageHighlighterColors.KEYWORD)
    private val STRING = arrayOf(DefaultLanguageHighlighterColors.STRING)
    private val COMMENT = arrayOf(DefaultLanguageHighlighterColors.LINE_COMMENT)
}
