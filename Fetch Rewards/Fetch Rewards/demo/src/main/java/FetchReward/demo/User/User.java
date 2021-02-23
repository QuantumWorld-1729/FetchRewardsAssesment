package FetchReward.demo.User;

import java.time.LocalDateTime;
import java.util.PriorityQueue;

/*
       User is class consists of 4 variables:
                    name -> string, balance -> integer, 
                    balanceSheet -> PriorityQueue, remove_balanceSheet -> PriorityQueue
                    These variables can be accessed by getter and setters.

        BalanceSheet -> 
                    It is MinHeap with sorting based on timestamp. So on top of this MinHeap 
                    you will have transaction of oldest timestamp.
                    Purpose: In order to track transaction and it timestamps. 
                             Only for transactions which add amount to user balance.

        unProcessedBalanceSheet -> 
                    It is MinHeap with sorting based on timestamp. So on top of this MinHeap 
                               you will have transaction of oldest timestamp.
                               Purpose: In order to track transaction and it timestamps. 
                                        Only for transactions which decrements user balance.


        processUserTransaction -> 
                    This function process the transaction and updates the balance, balanceSheet, 
                                                                          unProcessedBalanceSheet
                    This functions routes the Transaction to corresponding function:
                        if Transaction.Points is "positive" it routes to "processTransaction" function.
                        if Transaction.Points is "negative" it routes to "unProcessedTransaction" function.



        addTransaction -> This function add Transaction amount to the user.
                           -> It "add" the transaction to balanceSheet.
                           -> It "add" the transaction amount to the "balance" variable.

        unProcessedTransaction -> This function add Transaction to unProcessedBalanceSheet to the user.

        deleteTransaction -> This function delete Transaction amount from the user.
                          -> It "deduce" the transaction amount from the balance sheet if there exists transaction 
                                  amount balance below the input timestamp.
                          -> else it add the transaction to unProcessedBalanceSheet so that it can be processed 
                                  after somethime.

        topUserTransaction -> This function return top transaction present in BalanceSheet.

                        
*/

public class User {
    private String user;
    private int balance;
    private int unProcessedBalanceSheeetCount;
    PriorityQueue <Transaction> balanceSheet =  new PriorityQueue<Transaction>(new Transaction.TransactionComparator()); 
    PriorityQueue <Transaction> unProccessedBalanceSheet = new PriorityQueue<Transaction>(new Transaction.TransactionComparator()); 

    public User(String userName) {
        this.user = userName;
        this.balance = 0;
        this.unProcessedBalanceSheeetCount = 0;
    }

    public boolean processUserTransaction(Transaction txn) { 
        if (txn.getPoints() >= 0){    // If transaction.Points is positive it add the transcations amount to user.
            return this.addTransaction(txn); 
        }

        else{                          // If transaction.Points is negative it removes the transcation amount from user.
            txn.setPoints(-txn.getPoints());
            return this.unProcessedTransaction(txn);
        }   
    }


    public boolean addTransaction(Transaction inputTxn){
        LocalDateTime inputTimeStamp = inputTxn.getTimestamp();
        Transaction topUserTxn = this.balanceSheet.peek();
        // If any transaction with same timestamp exists then updates the transaction amount by adding to it.
        if ( !this.balanceSheet.isEmpty() 
             && topUserTxn.getTimestamp().isEqual(inputTimeStamp)){

            int currPoints = topUserTxn.getPoints();
            topUserTxn.setPoints(currPoints + inputTxn.getPoints());
        }

        else{
            this.balanceSheet.add(inputTxn);
        }
        this.balance += inputTxn.getPoints();

        // After adding the transaction now processes the unProcessedBalanceSheeet
        // if (!unProccessedBalanceSheet.isEmpty()){
        //     Transaction tempTxn = unProccessedBalanceSheet.peek();
        //     unProccessedBalanceSheet.poll();
        //     this.deleteTransaction(tempTxn);
        // }
        
        return true;
    }

    public boolean unProcessedTransaction(Transaction txn){
        this.unProccessedBalanceSheet.add(txn);
        this.unProcessedBalanceSheeetCount += 1;
        return true;
    }

    public boolean clearUnProcessedTransaction(){
        int count = this.unProcessedBalanceSheeetCount;

        for(int i = 0; i < count; i++){
            this.unProcessedBalanceSheeetCount -= 1;
            Transaction tempTxn = unProccessedBalanceSheet.peek();
            unProccessedBalanceSheet.poll();
            this.deleteTransaction(tempTxn);
        }
        return unProccessedBalanceSheet.isEmpty();
    }
    
    // Reduces the balance tansaction with timestamp less than the input transactiontimestamp by Transaction amount. 
    // If there are no transactions less than the input transaction timestamp then the input Tranction 
    // is added to unProcessedBalanceSheet.
     
    public boolean deleteTransaction(Transaction txn){  
        int amount = txn.getPoints();
        LocalDateTime deleteTimeStamp = txn.getTimestamp();

        if (this.balanceSheet.isEmpty() || this.balance < amount){
            return false;
        }

        Transaction topUserTxn = this.balanceSheet.peek();
        int topUserTxnPts = topUserTxn.getPoints();
        LocalDateTime topUserTxnTimeStamp = topUserTxn.getTimestamp();
        //Reduces the balance transaction with timestamp less than the input transactiontimestamp by Transaction amount.
        
        while (amount > 0 && topUserTxnPts <= amount 
                          && topUserTxnTimeStamp.isBefore(deleteTimeStamp)) {
            this.balance = this.balance - topUserTxnPts;
            amount -= topUserTxnPts;
            this.balanceSheet.poll();

            topUserTxn = this.balanceSheet.peek();

            if (this.balanceSheet.isEmpty()){
                return (amount == 0);
            } 

            topUserTxnPts = topUserTxn.getPoints();
            topUserTxnTimeStamp = topUserTxn.getTimestamp();
        }

        
        if (!this.balanceSheet.isEmpty()
             && topUserTxnTimeStamp.isBefore(deleteTimeStamp)){
            int val = topUserTxnPts - amount;
            this.balance = this.balance - amount;
            amount = 0;
            topUserTxn.setPoints(val);
        }
        
        //If there are no transactions less than the input transaction timestamp then the input Tranction is added to unProcessedBalanceSheet.
        
        if (amount > 0){
            txn.setPoints(amount);
            this.unProccessedBalanceSheet.add(txn);
            this.unProcessedBalanceSheeetCount += 1;
        }

        return true;
    }


    public Transaction topUserTransaction() {  // Return the top most transaction in balanceSheet
        return this.balanceSheet.peek();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

 
    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }


    public static class UserBalance {
        private String payer;
        private int points;
    
        public UserBalance(String payer, int points) {
            this.setPayer(payer);
            this.setPoints(points);
        }
        public String getPayer() {
            return payer;
        }
        public void setPayer(String payer) {
            this.payer = payer;
        }
        public int getPoints() {
            return points;
        }
        public void setPoints(int points) {
            this.points = points;
        }
    
    }
    
}