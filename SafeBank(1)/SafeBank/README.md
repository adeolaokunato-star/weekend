# SafeBank Transaction Simulator

## Tests and checklist (expected vs actual)

26 JUnit 5 tests in `src/test/java/ng/safebank/`.

| Check | Expected | Actual |
|---|---|---|
| Deposit of -500 | Rejected, balance and history unchanged | Pass |
| Withdrawal exceeding the balance | Rejected, balance and history unchanged | Pass |
| Withdraw ₦120,000 from ₦200,000 | Rejected (limit is ₦100,000), balance stays ₦200,000 | Pass |
| Non-numeric input (`abc`) | Helpful message, app keeps running | Pass |
| Success immediately after failure | ₦50,000 withdrawal works after the failed ₦120,000 one | Pass |

## Sample inputs and console output

Full input is in `sample-input.txt` and full console output is in `sample-output.txt`.

| Acceptance check | Sample input | Console output |
|---|---|---|
| Deposit of -500 | option `2`, account `SB1001`, amount `-500` | `Deposit failed: Amount must be greater than zero. You entered -500.` |
| Withdrawal exceeding balance | option `3`, account `SB1002`, amount `5000` | `Withdrawal failed: Insufficient funds. Available balance is ₦1,000.00 but you asked for ₦5,000.00.` |
| ₦120,000 from ₦200,000 | option `3`, account `SB1001`, amount `120000` | `Withdrawal failed: Withdrawal limit exceeded. The single-withdrawal limit is ₦100,000.00 but you asked for ₦120,000.00.` |
| Non-numeric input | option `2`, account `SB1001`, amount `abc` | `Deposit failed: 'abc' is not a valid number. Please type digits only, for example 5000 or 2500.50.` |
| Success after failure | option `3`, account `SB1001`, amount `50000` | `Withdrawal successful. New balance: ₦150,000.00` |

## Completed stretch challenge

- Transfers between two accounts. The whole transfer is validated before either balance changes.

## Known limitations

- Data is kept in memory only, so it is lost when the app closes.
- The account number must be typed for every operation (no login).