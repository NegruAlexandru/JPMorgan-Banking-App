# Proiect Etapa 2 - J. POO Morgan Chase & Co.
**Autor**: *Negru Alexandru*

---

## Descriere Generala

Proiectul reprezinta o aplicatie bancara simpla care permite utilizatorilor sa isi creeze conturi, sa isi gestioneze cardurile si sa efectueze plati. In cadrul aplicatiei se pot efectua urmatoarele operatii:

1. Crearea unui cont bancar
2. Adaugarea de fonduri in cont
3. Crearea unui card bancar
4. Efectuarea unei plati online
5. Trimiterea de bani catre un alt utilizator
6. Adaugarea de dobanda in cont
7. Schimbarea ratei dobanzii
8. Setarea unui alias pentru un cont
9. Setarea unui sold minim pentru un cont
10. Stergerea unui cont
11. Stergerea unui card
12. Verificarea statusului unui card
13. Crearea unui card de tip one-time
14. Efectuarea unui raport al cheltuielilor
15. Efectuarea unui raport al tranzactiilor
16. Efectuarea unui split payment
17. Acceptarea unui split payment
18. Refuzarea unui split payment
19. Efectuarea unui raport al afacerilor / cheltuielilor
20. Schimbarea limitelor de cheltuieli
21. Schimbarea limitelor de depunere
22. Retragerea de bani din cont
23. Retragerea de bani din contul de economii
24. Upgradarea planului de cont
25. Adaugarea unui nou asociat de afaceri
26. Printarea utilizatorilor
27. Printarea tranzactiilor

---

## Structura Proiectului
### Ierarhie de Clase

Clasele sunt organizate in pachete in functie de functionalitatea lor. Acestea sunt:
- Clasele din pachetul `debug` sunt clasele care implementeaza functionalitati de debug.
- Clasele din pachetul `userOperations` sunt clasele care implementeaza operatiile pe care le poate efectua un utilizator.
- Clasele din pachetul `baseClasses` sunt clasele care implementeaza input-ul necesar pentru aplicatie.
- Clasele din pachetul `logicHandlers` sunt clasele care implementeaza logica aplicatiei.
- Clasele din pachetul `payment` sunt clasele care implementeaza logica platilor.
- Clasele din pachetul `userFacilities` sunt clasele care implementeaza facilitatile pe care le are un utilizator.
- Clasele din pachetul `checker` sunt clasele care implementeaza checker-ul pentru tema.
- Clasele din pachetul `fileio` sunt clasele care implementeaza citirea si scrierea in fisiere.
- Clasele din pachetul `main` sunt clasele care implementeaza metoda main a aplicatiei.
- Clasele din pachetul `utils` sunt clasele care implementeaza interfete si metode utile pentru aplicatie.


### Ierarhie de Pachete

Logica aplicatiei bancare se afla in pachetul `org.poo` construit astfel:
```
+---appFunctionality
|   +---commands
|   |       AcceptSplitPaymentCommand.java
|   |       AddAccountCommand.java
|   |       AddFundsCommand.java
|   |       AddInterestCommand.java
|   |       AddNewBusinessAssociateCommand.java
|   |       BusinessReportCommand.java
|   |       CashWithdrawalCommand.java
|   |       ChangeDepositLimitCommand.java
|   |       ChangeInterestRateCommand.java
|   |       ChangeSpendingLimitCommand.java
|   |       CheckCardStatusCommand.java
|   |       CreateCardCommand.java
|   |       CreateOneTimeCardCommand.java
|   |       DeleteAccountCommand.java
|   |       DeleteCardCommand.java
|   |       PayOnlineCommand.java
|   |       PrintTransactionsCommand.java
|   |       PrintUsersCommand.java
|   |       RejectSplitPaymentCommand.java
|   |       ReportCommand.java
|   |       SendMoneyCommand.java
|   |       SetAliasCommand.java
|   |       SetMinimumBalanceCommand.java
|   |       SpendingsReportCommand.java
|   |       SplitPaymentCommand.java
|   |       UpgradePlanCommand.java
|   |       WithdrawSavingsCommand.java
|   |       
|   +---debug
|   |       PrintTransactions.java
|   |       PrintUsers.java
|   |       
|   \---userOperations
|           AcceptSplitPayment.java
|           AddAccount.java
|           AddFunds.java
|           AddInterest.java
|           AddNewBusinessAssociate.java
|           BusinessReport.java
|           CashWithdrawal.java
|           ChangeDepositLimit.java
|           ChangeInterestRate.java
|           ChangeSpendingLimit.java
|           CheckCardStatus.java
|           CreateCard.java
|           CreateOneTimeCard.java
|           DeleteAccount.java
|           DeleteCard.java
|           PayOnline.java
|           RejectSplitPayment.java
|           Report.java
|           SendMoney.java
|           SetAlias.java
|           SetMinimumBalance.java
|           SpendingsReport.java
|           SplitPayment.java
|           UpgradePlan.java
|           WithdrawSavings.java
|
+---baseClasses
|       Commerciant.java
|       Transaction.java
|       User.java
|
+---logicHandlers
|       CommandExecutor.java
|       CommandFactory.java
|       CommandHandler.java
|       DB.java
|       TransactionHandler.java
|
+---payment
|       AccountHandler.java
|       CashbackNrOfTransactions.java
|       CashbackSpendingTreshold.java
|       Context.java
|       Discount.java
|       ExchangeRate.java
|       PaymentHandler.java
|
\---userFacilities
        Account.java
        BusinessAccount.java
        Card.java
        OneTimeCard.java
        SavingsAccount.java

\---utils
        AccountInterface.java       
        AccountVisitor.java
        CardInterface.java
        CardVisitor.java
        CashbackStrategy.java
        Command.java
        Operation.java
        RequestSP.java


```
---
## Implementare

### Probleme intalnite
1. **Problema**: Implementarea sistemului de acceptare si refuzare a unui split payment.
-  **Solutie**: Utilizarea cozilor pentru a stoca cererile de split payment si a le procesa in ordinea in care au fost primite.

2. **Problema**: Verificarea varstei unui utilizator pentru a-i permite sa efectueze operatia de retragere a banilor din contul de economii.
-  **Solutie**: Folosirea pachetului `java.time` pentru a calcula varsta utilizatorului si a-i permite sa efectueze operatia daca aceasta este mai mare de 21 ani.

3. **Problema**: Alegerea strategiei corecte de calculare a cashback-ului in functie de comerciant.
-  **Solutie**: Utilizarea design pattern-ului `Strategy` pentru a alege strategia corecta dintre `CashbackNrOfTransactions` si `CashbackSpendingTreshold` in functie de comerciant.

4. **Problema**: Diferentierea intre tranzactiile valide pentru `Business Report` si celelalte.
-  **Solutie**: Crearea unei liste de tranzactii care sa contina doar tranzactiile valide pentru `Business Report`.
---

### Design Patterns
1. **Strategy**: Am folosit design pattern-ul `Strategy` pentru a alege strategia corecta de calculare a cashback-ului in functie de comerciant.
- CashbackNrOfTransactions, CashbackSpendingTreshold, Context din pachetul `payment`.
- CashbackStrategy din pachetul `utils`.
2. **Command**: Am folosit design pattern-ul `Command` pentru a implementa comenzile pe care le poate efectua un utilizator si intregul proces prin care trece comanda, de la citirea input-ului pana la executia comenzii.
- CommandExecutor, CommandFactory, CommandHandler din pachetele `logicHandlers`.
- Comenzile din pachetul `appFunctionality.commands`.
3. **Factory**: Am folosit design pattern-ul `Factory` pentru a crea comenzile in functie de input-ul utilizatorului.
- CommandFactory din pachetul `logicHandlers`.
4. **Builder**: Am folosit design pattern-ul `Builder` pentru a construi tranzactiile in mod eficient.
- Transaction din pachetul `baseClasses`.
5. **Visitor**: Am folosit design pattern-ul `Visitor` pentru a vizita tranzactiile si utilizatorii in momentul in care se efectueaza `Print Transactions` si `Print Users`.
- AccountHandler din pachetul `payment`, care mosteneste interfetele `AccountVisitor` si `CardVisitor`.
- Toate clasele din pachetul `userFacilities`, care implementeaza metoda de acceptare a unui `AccountVisitor` sau `CardVisitor`.

## Feedback
Consider ca proiectul a avut o tematica interesanta si un nivel bun de relevanta in domeniul de software engineering. Totusi am intampinat probleme din cauza numarul mare de schimbari aparute in structura proiectului si pe checker.

---