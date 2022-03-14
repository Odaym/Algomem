import kotlinx.ast.common.AstResult
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.print
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType
import kotlinx.ast.grammar.kotlin.common.summary
import kotlinx.ast.grammar.kotlin.target.antlr.kotlin.KotlinGrammarAntlrKotlinParser
import org.antlr.v4.kotlinruntime.misc.ParseCancellationException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    println("Enter the path for your algorithm's source file:")

    val path = readLine()

    if (path != null) {
        val file = AstSource.File(path)

        try {
            KotlinGrammarAntlrKotlinParser.parseKotlinFile(file)

            try {
                println("\n✓ - Valid Kotlin source file. Great, then let us begin.\n")

                val lines = Files.readAllLines(Paths.get(path))

                println("Start by entering the complete function signature.")

                lines.forEach { line ->
                    if (line.trim().startsWith("//")) {
                        println("\n${line.trim()}")
                        return@forEach
                    }

                    val input = readLine()

                    val valid = validateLine(line, input)

                    if (valid) {
                        println("\n✓ --- Correct ---")
                    } else {
                        println("\n✘ - Wrong.\nExpected: ${line}\nActual: $input")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (ex: Exception) {
            when (ex) {
                is java.nio.file.NoSuchFileException -> {
                    println("✘ - File not found. Make sure to place the source file inside this project's root directory.")
                }
                is ParseCancellationException -> {
                    println("✘ - Invalid Kotlin source file.")
                }
            }
        }
    }
}

fun validateLine(expected: String, actual: String?) = expected.trim() == actual?.trim()

fun parseFile() {
    val sourceFile1 = AstSource.File("file1.kt")
    val sourceFile2 = AstSource.File("file2.kt")

    val kotlinFile1 = KotlinGrammarAntlrKotlinParser.parseKotlinFile(sourceFile1)
    val kotlinFile2 = KotlinGrammarAntlrKotlinParser.parseKotlinFile(sourceFile2)

    var file1: AstResult<Unit, List<Ast>>? = null
    var file2: AstResult<Unit, List<Ast>>? = null

    try {
        kotlinFile1.summary(false)
            .onSuccess { astList ->
//                astList.forEach(Ast::print)
                file1 = astList.summary(false)
            }

        kotlinFile2.summary(false)
            .onSuccess { astList ->
                file2 = astList.summary(false)
            }

        if (file1?.get() == file2?.get()) {
            println("The files are identical")
        } else {
            println("The files are not equal")
        }
    } catch (pce: ParseCancellationException) {
        println("That's not quite right, try again")
    }
}

fun parseRaw() {
    val code1 = """

    package foo

    fun bar() {
        // Print hello
        println("Hello, World!")
        
        for (i in 0..1){
            println("Hello")
        }
    }

    fun baz() = println("Hello, again!")
""".trimIndent()

    val code2 = """

    package foo

    fun bar() {
        // Print hello
        println("Hello, World!")
        
        for (i in 0..1){
            println("Hello")
        }
    }

    fun baz() = println("Hello, again!")
""".trimIndent()
    val kotlinFile = KotlinGrammarAntlrKotlinParser.parse(
        AstSource.String(
            content = code1,
            description = ""
        ),
        listOf(KotlinGrammarParserType.kotlinFile)
    )

    try {
        kotlinFile.summary(false)
            .onSuccess { astList ->
//                astList.forEach(Ast::print)
                println("Extra printing --> ${astList[0].summary(false)}")
            }.onFailure { errors ->
                errors.forEach(::println)
            }
    } catch (pce: ParseCancellationException) {
        println("That's not quite right, try again")
    }
}

fun parseUserInput() {
    var line = readLine()

    while (line != null) {
        val source = AstSource.String(
            content = line,
            description = ""
        )

        try {
            val kotlinFile = KotlinGrammarAntlrKotlinParser.parseKotlinFile(source)

            kotlinFile.summary(attachRawAst = false)
                .onSuccess { astList ->
                    astList.forEach(Ast::print)
                    line = readLine()
                }.onFailure { errors ->
                    errors.forEach(::println)
                }
        } catch (pce: ParseCancellationException) {
            println("That's not quite right, try again")

            line = readLine()
        }
    }
}