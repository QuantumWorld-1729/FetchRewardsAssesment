package FetchReward.demo.User;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//It main API gateway which routes 
//for 3 API calls that is processTransaction, withdrawal, balance. 

@RestController
public class Controller {
    private TransactionEngine transactionEngine;

    @Autowired 
	public Controller(TransactionEngine transactionEngine) {
		this.transactionEngine = transactionEngine;
	} 

    // For adding transactiom
    @RequestMapping(method = RequestMethod.POST, path = "/api/processTransaction")
    public void processTransacation(@RequestBody Transaction txn){

        this.transactionEngine.processTransaction(txn);
    }

    // add withdrawal of amount from all users
    @RequestMapping(method = RequestMethod.POST, path = "/api/withDrawal")
    public ArrayList<User.UserBalance> deleteAmount(@RequestBody Transaction txn){
        // In the delete amount we are ignoring  payer and timestamp.
        ArrayList<User.UserBalance> deletedTxns = this.transactionEngine.withDrawal(txn.getPoints());    
        return deletedTxns;
    }    

    // getting balances for all users
    @RequestMapping(method = RequestMethod.GET, path = "/api/balance")
    public ArrayList<User.UserBalance> getBalances(){
        ArrayList<User.UserBalance> balanceList = this.transactionEngine.getBalances();
        return balanceList;
    }

}


