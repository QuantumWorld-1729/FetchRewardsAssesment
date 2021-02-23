package FetchReward.demo.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;
/*
TransactionEngine is class which consists of 2 variables 
                    userDict -> hashMap which holds user -> User class key value-pair
                    minHeapTransaction -> It is MinHeap with sorting based on timestamp. So on top of this MinHeap 
                    you will have transaction of oldest timestamp. 
                    Purpose: To track the oldest transactions.

                2 functions:
                processTranscation -> Processes the transaction to corresponding user.
                                   -> Also create a new user.
                withDrawal -> to withdrawal oldest amount amount from all users. 
                getBalances -> return balances of All users.
                clearAllUnProcessedTransaction -> clears all the unProcessed transactions in all users
*/
@Service
public class TransactionEngine {
    private HashMap<String, User> userDict = new HashMap<>();
    private PriorityQueue <Transaction> minHeapTransaction =  new PriorityQueue<Transaction>(new Transaction.TransactionComparator());
    
    // Processes the transaction to corresponding user.
    public boolean processTransaction(Transaction txn){
        String userName = txn.getName();

        if(!userDict.containsKey(userName)){ 
            userDict.put(userName, new User(userName));
        }
        User user = userDict.get(userName);

        if (txn.getPoints() >= 0){
            minHeapTransaction.add(txn);
        }

        return user.processUserTransaction(txn);   
   } 


   public void clearAllUnProcessedTransaction(){
    Iterator<String> userDictIterator = this.userDict.keySet().iterator(); 
    while (userDictIterator.hasNext()) {
        String username = userDictIterator.next();
        User user = userDict.get(username); 
        user.clearUnProcessedTransaction();
    } 
   }

   // to withdrawal oldest amount amount from all users.
   // reduceBalance array track user who's amount is deducted. 
   public ArrayList<User.UserBalance> withDrawal(int amount){
    ArrayList<User.UserBalance> reduceBalance = new ArrayList<User.UserBalance>();
    clearAllUnProcessedTransaction();
    
    while(amount > 0 && !minHeapTransaction.isEmpty()){
        
        Transaction topTxn = minHeapTransaction.peek();
        LocalDateTime top = topTxn.getTimestamp();
        String name = topTxn.getName();

        // get top transaction of a user in order 
        // to validate transaction timestamps are same in userheap and TransactionEngineheap.
        User user = userDict.get(name);
        Transaction topUserTxn = user.topUserTransaction();
        LocalDateTime topUserTxnDateTime = topUserTxn.getTimestamp();
        int topUserTxnPts = topUserTxn.getPoints();

         if(topUserTxnDateTime.isEqual(top)){
             int curr_amount = topUserTxnPts;
             
             //remove the amount the amount of correponding user
             if (amount >= curr_amount){
                 Transaction tempTransaction = new Transaction(name, -curr_amount, LocalDateTime.MAX);
                 user.processUserTransaction(tempTransaction);
                 user.clearUnProcessedTransaction();
                 minHeapTransaction.poll();
                 reduceBalance.add(new User.UserBalance(name, -curr_amount));
                 amount -= curr_amount;
                }

             else{
                 Transaction tempTransaction = new Transaction(name, -amount, LocalDateTime.MAX);
                 user.processUserTransaction(tempTransaction);
                 user.clearUnProcessedTransaction();
                 minHeapTransaction.poll();
                 minHeapTransaction.add(topTxn);
                 reduceBalance.add(new User.UserBalance(name, -amount));
                 amount = 0;
                }
            } 
            else{
                minHeapTransaction.poll();
            }
        }
        clearAllUnProcessedTransaction();
    return reduceBalance;
    }


    public ArrayList<User.UserBalance> getBalances(){
        ArrayList<User.UserBalance> balanceList = new ArrayList<User.UserBalance>();
      
        Iterator<String> userDictIterator = this.userDict.keySet().iterator(); 
        while (userDictIterator.hasNext()) {
            String username = userDictIterator.next();
            User user = userDict.get(username); 
            int points = user.getBalance();
            String payer = user.getUser(); 
            balanceList.add(new User.UserBalance(payer, points));
        } 
        return balanceList;
    }
}



