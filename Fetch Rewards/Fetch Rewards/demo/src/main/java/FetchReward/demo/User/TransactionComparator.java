package FetchReward.demo.User;

import java.util.Comparator; 

/*
       The_Comparator is comparator class used for Priority Queue:
                    -> Mainly used for comparing two elements in heap.
                    -> The_Comparator compares transaction class based on "timeStamps" variable.
    
                    The_Comparator(Ta,Tb) returns  (Ta, Tb are transaction class object)
                    (a,b are timestamps of Ta, Tb class objects)
                    -> returns 1 if a > b
                    -> returns -1 if a < b
                    -> returns 0 if a == b

                    -> for example:
                        a = "2020-11-02T14:00:00"
                        b = "2020-10-31T11:00:00"
                        returns 1 as a > b
                                                    
*/ 


public class TransactionComparator implements Comparator<Transaction> {
    public int compare(Transaction a, Transaction b) { 
        if ((a.getTimestamp()).isAfter(b.getTimestamp())){
            return 1;
        }
        
        if ((a.getTimestamp()).isBefore(b.getTimestamp())){
            return -1;
        }
        
        if ((a.getTimestamp()).isEqual(b.getTimestamp())){
            return 0;
        }
        return 1;
    } 
} 