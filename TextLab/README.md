# TextLab: Text Analysis & Cleanup

## Assumptions

- A word is a sequence of letters or digits. Punctuation (including `-` and `'`) separates words, so `Java-based` is two words and `Java's` is `Java` and `s`.
- One or more consecutive `.`, `!` or `?` form one sentence boundary. A final fragment without closing punctuation counts as a sentence.
- A fragment only counts as a sentence if it contains at least one letter or digit. So a passage made only of punctuation has 0 words and 0 sentences.
- Decimals and abbreviations (for example `3.14` or `e.g.`) are treated as sentence boundaries because the brief only defines `.`, `!` and `?`.
- Tabs and line breaks count as whitespace. "Characters with whitespace" is the full length of the passage. When whitespace is cleaned, line breaks become single spaces.
- Search and replace need one word (letters or digits only). A blank word or a word with spaces or punctuation is rejected with a message.
- Text with no letters or digits is not a palindrome.
- "Longest words without duplicates" ignores case (`Java` and `java` count as one). The spelling of the first appearance is shown.
- The passage is typed on several lines, and `END` on a line by itself finishes it. The palindrome check and the two-passage comparison ask for their own text.
- The counts, search, replace and longest-words options work on the edited passage. The original passage is kept unchanged and can be shown next to the edited one with option 2.

## Tests and checklist (expected vs actual)

39 JUnit 5 tests in `src/test/java/ng/textlab/`.

| Check | Expected | Actual |
|---|---|---|
| Repeated spaces, tabs and line breaks | Collapsed into one space, leading and trailing whitespace removed, words still counted correctly | Pass |
| `Java`, `JAVA` and `java` | All counted as the same word, ignoring case | Pass |
| Passage containing `Java` and `JavaScript` | Searching `Java` does not match `JavaScript`, replacing `Java` leaves `JavaScript` alone | Pass |
| `A man, a plan, a canal: Panama!` | Recognised as a palindrome | Pass |
| Passage with punctuation but no words | 0 words, 0 sentences, no crash, not a palindrome, no longest words | Pass |
| Empty or whitespace-only input | No crash, all counts 0 | Pass |
| Stretch: compare two passages | First differing position found (or "same") | Pass |

## Sample inputs and console output

Full input is in `sample-input.txt` and full console output is in `sample-output.txt`.

| Acceptance check | Sample input | Console output |
|---|---|---|
| Repeated spaces, tabs, line breaks | option `1` with a passage containing extra spaces, a tab and two lines, then option `4` | `Java is great. JAVA is fun!! I use java and JavaScript daily` (characters with whitespace go from 65 to 60) |
| Mixed-case words | option `5`, word `java` | `The word 'java' appears 3 time(s) (whole word, ignoring case).` |
| Java vs JavaScript | option `5`, word `JavaScript` | `The word 'JavaScript' appears 1 time(s) (whole word, ignoring case).` |
| Replace whole words | option `6`, word `java`, replacement `Kotlin` | `Replaced 3 occurrence(s).` and `Kotlin is great. Kotlin is fun!! I use Kotlin and JavaScript daily` |
| Palindrome | option `7`, text `A man, a plan, a canal: Panama!` | `Yes, it is a palindrome (ignoring case, spaces and punctuation).` |
| Punctuation but no words | option `1` with `!!! ... ???`, then options `3` and `8` | Words: 0, Sentences: 0, `There are no words in the passage.` |
| Whitespace-only passage | option `1` with spaces only, then options `3`, `4`, `5`, `8` | Counts are 0, `(empty)`, `appears 0 time(s)`, `There are no words in the passage.` |
| Longest words | option `8` | `Longest word(s), 10 characters each:` then `- JavaScript` |
| Stretch: compare passages | option `9`, `hello` and `help!` | `First difference at position 4: 'l' in the first passage, but 'p' in the second.` |

## Completed stretch challenge

- Option 9 compares two passages and shows the first position where they differ.

## Known limitations

- Data is kept in memory only, so the passage is lost when the app closes.
- Sentence counting does not understand decimals, abbreviations or other special cases.
- Comparing passages is done on one line of text for each passage.
