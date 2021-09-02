/*
 *  Copyright 2021-present the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.neiljbrown.examples.java17.textblocks;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Provides examples of the use of Text Blocks, a new language feature that was finalised in JDK 15 (Sept 2020),
 * implemented as a JUnit test case.
 * <p>
 * Text Blocks provide Java developers with another way to declare a String literal in Java source code with the aim
 * of making it simpler to declare a multi-line string (aka block of text), that is also easier to read and maintain.
 * They achieve this by removing the need to a) declare and concatenate (append) separate strings for each line; b)
 * escape literal double quotes, and c) explicitly specify line separators / new line chars (\n)
 *
 * <h2>Further Reading</h2>
 * 1) <a href="https://openjdk.java.net/jeps/378">JEP 378: Text Blocks</a> - The final Java Enhancement Proposal (JEP)
 * for Text Blocks, describes the goals and motivation for the feature, plus a detailed description of how it should
 * work (its design) including examples.
 */
public class TextBlocksExamplesTest {

  /**
   * Provides an example of how to use a Text Block to declare a multi-line string which uses a new line (\n) separator
   * for each line of the text block (the default behaviour), and includes a trailing new line.
   * <p>
   * This example uses a Text Block to implement a multi-line SQL query string - something that is commonly required
   * when implementing DAOs, especially when using plain JDBC. A similar, common example, would be to declare a JSON
   * string over multiple lines.
   * <p>
   * In this example, each new line in the Text Block results in a new line (\n line feed char) in the
   * resulting string. This is the default way Text Blocks handle new lines. However, it is possible to suppress
   * these new lines. For details see example {@link #test_suppressNewLines()}.
   * <p>
   * In this example, the Text Block (specifically the closing delimiter) is declared in a way which ensures that any
   * leading whitespace (indentation) in the block is considered incidental (only exists to match the indentation of
   * the Java source code) and is not retained in the resulting string. For more details of this Text Block behaviour
   * see later example in method {@link #test_indentation()}.
   */
  @Test
  public void test_createLineFeedDelimitedString_withTrailingNewLine() {
    // Pre JDK 15 example of how a multi-line string might have been written, in this case to create a readable SQL
    // query string -
    // - each component of the SQL string is implemented as a separate string, which need to be concatenated
    // - line feeds are used to delimit each line of the string
    final String expectedString =
      "SELECT \"first_name\", \"last_name\"\n" +
      "FROM \"user\"\n" +
      "WHERE \"city\" = 'London'\n" +
      "ORDER BY \"last_name\";\n";

    // From JDK 15 onwards, a Text Block can be used to declare an equivalent multi-line string, as follows -

    // A text block's opening delimiter is sequence of three double quotes followed by zero or more white spaces, and a
    // _mandatory_ new line. (It's a compilation error if the new line isn't present).
    final String sqlString = """
      SELECT "first_name", "last_name"
      FROM "user"
      WHERE "city" = 'London'
      ORDER BY "last_name";
      """;
    // A text block's closing delimiter is also three double quotes. Its vertical and horizontal positioning relative
    // to the content influences both whether a trailing new line is included, and whether any leading whitespace
    // (indentation) is consider incidental removed from the resulting string, or not. In the above example, a new
    // line is included, with no leading whitespace.

    assertThat(sqlString).isEqualTo(expectedString);
  }

  /**
   * Provides an example of how to use a Text Block to declare a string (over multiple lines in source code) that
   * doesn't include any new line (aka line-feed) characters, i.e. suppress the default behaviour of inserting a new
   * line for each line in the block.
   * <p>
   * Text Block support for suppressing (or escaping) new lines allows them to be used to declare single-line strings
   * across multiple lines in source code. This can be useful to soft wrap a string to improve readability e.g. when
   * the string is very long, or as in this example to delineate it's logical components, e.g. parts of an SQL string.
   */
  @Test
  public void test_suppressNewLines() {
    // Pre JDK 15 example of how a string with no line breaks might have been declared (soft-wrapped) over multiple
    // lines, e.g. to create a readable SQL query (or when the line is very long).
    final String expectedString =
      "SELECT \"first_name\", \"last_name\" " +
      "FROM \"user\" " +
      "WHERE \"city\" = 'London' " +
      "ORDER BY \"last_name\";";

    // From JDK 15 onwards, a Text Block can be used to declare an equivalent string, as follows -
    // A block can use a "\<line-terminator>" escape sequence to suppress default inclusion of new line (\n) chars.
    final String sqlString = """
      SELECT "first_name", "last_name" \
      FROM "user" \
      WHERE "city" = 'London' \
      ORDER BY "last_name";""";
    // This example also places the text block's closing delimiter on same line as content to suppress trailing new line

    assertThat(sqlString).isEqualTo(expectedString);
  }

  /**
   * Provides an example of how to control how much of the leading whitespace (indentation) in the lines of a Text Block
   * is included in the resulting string, as opposed to being classified as just incidental (part of the source code
   * formatting) and excluded from the resulting string.
   * <p>
   * The method the compiler uses to work out how much leading whitespace in a text block should be included in the
   * resulting string is referred to as the 're-indentation algorithm', which is described in full in
   * <a href="https://openjdk.java.net/jeps/378">JEP 378: Text Blocks</a>. In summary, the compiler classifies
   * incidental whitespace as the minimum no. of whitespace chars (indentation) across all non-empty lines of the
   * block, including the line containing the closing delimiter. It then removes it by shifting every line of the
   * block to the left that no. of chars.
   * <p>
   * The easiest way to control the amount of indentation in the resulting string is to place the closing delimiter
   * on its own line and left align it relative to the other lines of the text block.
   */
  @Test
  public void test_indentation() {
    // In this example the min no. of leading whitespace chars across all text block lines, classified as incidental is
    // 12. (The closing delimiter is on the same line as the last line of block content so doesn't have an impact).
    final String jsonString1 = """
            {
              "id": 1,
              "firstName": "Neil",
              "lastName": "Brown"
            }""";

    // As a result there is no leading whitespace (indentation) in each line of the resulting string
    assertThat(jsonString1).isEqualTo(
      "{\n  \"id\": 1,\n  \"firstName\": \"Neil\",\n  \"lastName\": \"Brown\"\n}"
    );

    // In this example the min no. of leading whitespace chars across all text block lines, classified as incidental is
    // 10, due to negative left alignment of the closing delimiter relative to the rest of the text block content
    final String jsonString2 = """
            {
              "id": 1,
              "firstName": "Neil",
              "lastName": "Brown"
            }\
          """;

    // As a result there are 2 (12 minus 10) leading whitespace chars retained in each line of the resulting string
    assertThat(jsonString2).isEqualTo(
      "  {\n    \"id\": 1,\n    \"firstName\": \"Neil\",\n    \"lastName\": \"Brown\"\n  }"
    );
  }

  /**
   * Provides an example of how to use a Text Block to add trailing spaces to the line(s) of the resulting string,
   * aka space padding alignment.
   * <p>
   * By default the compiler ignores all trailing spaces in the lines of a Text Block - they won't appear in the
   * resulting string. However, you can add them using the \s escape sequence.
   */
  @Test
  public void test_insertTrailingSpaces() {
    // When the \s escape sequence is used in a text block, in addition to inserting a single space, for convenience it
    // also preserves any trailing spaces that occur before it. So, in the following example all lines of the
    // resulting string are space padded to 8 chars in length
    final var phoneticsString = """
      alpha  \s
      beta   \s
      charlie\s
      """;

    assertThat(phoneticsString).isEqualTo(
      "alpha   \n" +
      "beta    \n" +
      "charlie \n"
    );
  }
}