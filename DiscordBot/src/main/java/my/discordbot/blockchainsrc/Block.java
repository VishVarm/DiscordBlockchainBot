package my.discordbot.blockchainsrc;

import java.util.Date;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;


public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    //private String data; //our data will be a simple message.
    public long timeStamp; //as number of milliseconds since 1/1/1970.
    public int nonce;
    public int transactionIds = 0;

    //Block Constructor.
    public Block(String previousHash ) {
        //this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    public Block (String previousHash, int transactionId) {
        //this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.transactionIds = transactionId;
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        return calculatedhash;
    }

    public void mineBlock(int difficulty, Wallet wallet) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
            //mineCalculate();
        }
        //mineCalculate();
        rewardMine(wallet);
        //System.out.println("Block Mined!!! : " + hash);
    }

    public void rewardMine(Wallet wallet) {
        Wallet coinbase = new Wallet();
        Transaction reward = new Transaction(coinbase.publicKey, wallet.publicKey, 10f, null);
        reward.generateSignature(coinbase.privateKey);
        reward.transactionId = "" + transactionIds;
        System.out.println("Transaction ID: " + reward.transactionId);
        reward.outputs.add(new TransactionOutput(reward.reciepient, reward.transactionId)); //manually add the Transactions Output
        BlockChain.UTXOs.put(reward.outputs.get(0).id, reward.outputs.get(0));
        System.out.println("Reward.outputs.get(0).id = " + reward.outputs.get(0).id);
        System.out.println("Reward.outputs.get(0) = " + reward.outputs.get(0));
        //transactionIds++;
        addTransaction(reward);
    }

    public void mineCalculate() {
        Scanner keyboard = new Scanner(System.in);
        Random rand = new Random();
        int x1 = rand.nextInt(10);
        int x2 = rand.nextInt(10);
        System.out.println("What is " + x1 + " * " + x2 + "?");
        while (true) {
            int ans = keyboard.nextInt();
            if (ans == (x1 * x2)) {
                return;
            }
            System.out.println("Incorrect, try again");
        }

    }
    //Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if((previousHash != "0")) {
            if((transaction.processTransaction() != true)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}

