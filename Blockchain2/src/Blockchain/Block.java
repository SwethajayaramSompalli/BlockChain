package Blockchain;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

import Blockchain.HashSHA256;

public class Block {

	
	public String previousHash;
    public String transactionData;
    public int blockId;
    public String bCName;
    public long timeStamp;
    public String blockHash;
    //public long blockPointer;
    public int difficulty;
    public int nonce;
    
    public Boolean ack = false;
    public int pid = 0;
    
    public Block( String bCName ,String transactionData, int difficulty ) {
        //this.previousHash = previousHash;
        this.transactionData = transactionData;
        this.blockId = blockId;
        this.bCName = bCName;
        this.timeStamp = new Date().getTime();
        this.difficulty = difficulty;
    }
    
    public Block( String bCName ,String transactionData, int difficulty, int p ) {
        //this.previousHash = previousHash;
        this.transactionData = transactionData;
        this.blockId = blockId;
        this.bCName = bCName;
        this.timeStamp = new Date().getTime();
        this.difficulty = difficulty;
        
        pid = p;
    }
    
    

    public Block( String bCName ,String transactionData, int difficulty, Boolean a, int p ) {
        //this.previousHash = previousHash;
        this.transactionData = transactionData;
        this.blockId = blockId;
        this.bCName = bCName;
        this.timeStamp = new Date().getTime();
        this.difficulty = difficulty;
        
        ack = a;
        pid = p;
    }
    
    public void printBlock() {
    	System.out.println(this.bCName);
    	System.out.println(this.blockHash);
    	System.out.println(this.blockId);
    	System.out.println(this.timeStamp);
    	System.out.println(this.previousHash);
    }
    
    public String getHash() {
		return this.blockHash;
	}


    public String getPreviousHash() {
        return previousHash;
    }

    public int getblockId() {
    	return blockId;
    }
    
    public int getDifficulty() {
    	return difficulty;
    }
    
    public String getTransaction() {
        return transactionData;
    }

    public String getbCName() {
        return bCName;
    }
    public String getBlockHash() {
        return blockHash;
    }
    public long gettimeStamp() {
        return timeStamp;
    }
	public void incrementNonce() {
		this.nonce++;
	}
	
	public void setPrevHash(String ph) {
		this.previousHash = ph;
	}
}
