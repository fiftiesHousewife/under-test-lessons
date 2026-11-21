package io.github.fiftieshousewife.undertest.techniques.characterisation;

import java.util.List;

/** Prints a bank statement for an account. */
public interface Statement {

    String print(String account, List<Transaction> transactions);
}
