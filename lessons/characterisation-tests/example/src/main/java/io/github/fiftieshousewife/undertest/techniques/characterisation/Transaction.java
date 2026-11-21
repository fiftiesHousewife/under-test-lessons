package io.github.fiftieshousewife.undertest.techniques.characterisation;

/** One line on a bank statement: when, what, and the amount in whole pounds (negative for money out). */
public record Transaction(String date, String description, int amount) {
}
