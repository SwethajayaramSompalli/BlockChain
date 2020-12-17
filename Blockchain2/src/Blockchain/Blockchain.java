package Blockchain;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;


public class Blockchain {
	public static void main(String[] args) {
		Block[] blockObj = new Block[100];
		System.out.println("Which type of mode do you want, enter the number only \n1.Normal  \n2.Adversary");
		Scanner in=new Scanner(System.in);
	    int p=in.nextInt();
	    int i=1;
	    int flag=0;
	    try {
			FileOutputStream fos = new FileOutputStream("out.ser");
			FileInputStream fis = new FileInputStream("out.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			ObjectInputStream ois = new ObjectInputStream(fis);
			HashSHA256 hash= new HashSHA256();
		
		while(p==1)
		{
			System.out.println("What do you want to do, enter the number only \n1.Create a new Blockchain  \n2.Add a New node \n3.Broadcast the Object \n4.Exit");
		    int s= in.nextInt();
		    if(s==1)
			{   
	            flag=1;
			    System.out.println("Enter the name of new blockchain");
			    String bCName=in.next();
			    System.out.println("The Blockchain name is:"+bCName);
		    	System.out.println("Enter the transaction data");
		    	String transactionData=in.next();
			    System.out.println("The transaction data:"+transactionData);
			    System.out.println("Enter the No. of Zeros");
			    int difficulty=in.nextInt();
			    System.out.println("The No of Zeros:"+difficulty);
			    blockObj[0] = new Block(bCName,transactionData,difficulty);
			    blockObj[0].blockId=1;
			    blockObj[0].previousHash="0000000000000000000000000000000000000000000000000000000000000000";
			    blockObj[0].blockHash= hash.generateHash(blockObj[0].transactionData+blockObj[0].previousHash+blockObj[0].timeStamp+blockObj[0].nonce);
			    nonce(blockObj[0]);
			    
			}
		    else if(s==2) 
		    {
		    	if(flag==1)
		    	{
				    System.out.println("Enter the name of existing blockchain");
				    String bCName=in.next();
				    if( blockObj[0].bCName.equalsIgnoreCase(bCName))
				    {
					    System.out.println("The Blockchain name is:"+bCName);
				    	System.out.println("Enter the transaction data");
				    	String transactionData=in.next();
					    System.out.println("The transaction data:"+transactionData);
					    int difficulty=blockObj[0].difficulty;
					    System.out.println("The No of Zeros:"+difficulty);
					    blockObj[i] = new Block(bCName,transactionData,difficulty);
					    blockObj[i].blockId=i+1;
					    blockObj[i].previousHash=blockObj[i-1].blockHash;
					    blockObj[i].blockHash= hash.generateHash(blockObj[i].transactionData+blockObj[i].previousHash+blockObj[i].timeStamp+blockObj[i].nonce);
					    nonce(blockObj[i]);
					    i++;
				    }
				    else
				    {
				    	System.out.println("Wrong BLock Chain");
				    }
		    	}
		    	else
		    	{
		    		System.out.println("Create BLock Chain First");
		    	}
		    }
		    else if(s==3) 
		    {
	    		
	    		Block blocktrans = new Block(blockObj[i-1].bCName, blockObj[i-1].transactionData, blockObj[i-1].difficulty );
	    		blocktrans.blockHash= blockObj[i-1].blockHash;
	    		blocktrans.blockId= blockObj[i-1].blockId;
	    		blocktrans.timeStamp=blockObj[i-1].timeStamp;
	    		blocktrans.previousHash=blockObj[i-1].previousHash;
	    		/*System.out.println("Block"+(i));
	    		System.out.println("The transmitted Blockchain name is:"+blocktrans.bCName);
	    		System.out.println("The transmitted transaction data:"+blocktrans.transactionData);
	    		System.out.println("The transmitted No of Zeros:"+blocktrans.difficulty);
	    		System.out.println("The transmitted block ID:"+blocktrans.blockId);
	    		System.out.println("The transmitted timestamp:"+blocktrans.timeStamp);
	    		System.out.println("The transmitted Previous Hash:"+blocktrans.previousHash);
	    		System.out.println("The transmitted Current Hash:"+blocktrans.blockHash);*/
	    		
	    		TransportationLayer tl = new TransportationLayer(Integer.parseInt(args[0]));
	            tl.init(); // initializes transportation layer
	            /*if(args.length > 1){
	                Block block = new Block(args[1], 
	                                            "no hash", 
	                                            new Timestamp(System.currentTimeMillis()), 
	                                            0, 
	                                            "This is my first block", 
	                                            pid);

	                tl.broadcastBlock(block);
	            }*/
	            try{
	                Thread.sleep(3000);
	                
	                //sets exit instruction to true
	                TransportationLayer.exit = true; 
	                //Thread.sleep(200);
	                
	                
	                if(tl.connectionHandler != null){
	                    
	                    System.out.println("Join connection handler 1: ");
	                    tl.connectionHandler.join(10000); // collects connectionHandler thread
	                    System.out.println("Join connection handler 2");
	                }
	                if(tl.acknowledgmentHandler != null){
	                    System.out.println("Join ack handler 1");
	                    tl.acknowledgmentHandler.join(10000); // collects acknowledgmentHandler thread
	                    System.out.println("Join ack handler 2");
	                }
	                if(tl.blockchainHandler != null){
	                    System.out.println("Join blockchain handler 1");
	                    tl.blockchainHandler.join(10000); // collects blockchainHandler thread
	                    System.out.println("Join blockchain handler 2");
	                }
	                
	                System.out.println("Process: " + args[0] + " has completed!");
	                in = new Scanner(System.in);
	                in.next(); // dummy input so that terminal remains open
	                
	                
	            }
	            catch(Exception e){
	                e.printStackTrace();
	            }
	            

	    		
		    }
		    else if(s==4) 
		    {
		    	for(int j=0;j<i;j++)
		    	{   
		    		System.out.println("Block"+(j+1));
		    		System.out.println("The Blockchain name is:"+blockObj[j].bCName);
		    		System.out.println("The transaction data:"+blockObj[j].transactionData);
		    		System.out.println("The No of Zeros:"+blockObj[j].difficulty);
		    		System.out.println("The block ID:"+blockObj[j].blockId);
		    		System.out.println("The timestamp:"+blockObj[j].timeStamp);
		    		System.out.println("The Previous Hash:"+blockObj[j].previousHash);
		    		System.out.println("The Current Hash:"+blockObj[j].blockHash);
		    		//oos.writeObject(blockObj[j]);
		    		//System.setOut(out);
		    		
		    	}
		    	System.out.println("From File");
		    	for(int j=0;j<i;j++)
		    	{ 
		    		// write object to file
		    		oos.writeObject(blockObj[j].bCName);
		    		oos.writeObject(blockObj[j].transactionData);
		    		oos.writeObject(blockObj[j].blockId);
		    		oos.writeObject(blockObj[j].timeStamp);
		    		oos.writeObject(blockObj[j].previousHash);
		    		oos.writeObject(blockObj[j].blockHash);
		    		// read object from file
					String name = (String)ois.readObject();
					String tdata=(String) ois.readObject();
					int bId=(int) ois.readObject();
					long ts=(long) ois.readObject();
					String pH=(String) ois.readObject();
					String bH=(String) ois.readObject();
					//ois.close();
					//System.out.println("BlockChainNfrom File:" + blockObj[j].getbCName());
		    		System.out.println("The Blockchain name is:"+name);
		    		System.out.println("The transaction data:"+tdata);
		    		//System.out.println("The No of Zeros:"+blockObj[j].getDifficulty());
		    		System.out.println("The block ID:"+bId);
		    		System.out.println("The timestamp:"+ts);
		    		System.out.println("The Previous Hash:"+pH);
		    		System.out.println("The Current Hash:"+bH);
		    	}
		    	
		    	//oos.close();
		    	break;
		    }
		    else 
		    {
		    	System.out.println("Wrong Inputs");
		    }
		}
		while(p==2)
		{
			
			System.out.println("What do you want to do, enter the number only \n1.Create a new Blockchain  \n2.Add a New node \n3.Broadcast the Object \n4.Exit");
		    int s= in.nextInt();
		    if(s==1)
			{   
	            flag=1;
			    System.out.println("Enter the name of new blockchain");
			    String bCName=in.next();
			    System.out.println("The Blockchain name is:"+bCName);
		    	System.out.println("Enter the transaction data");
		    	String transactionData=in.next();
			    System.out.println("The transaction data:"+transactionData);
			    System.out.println("Enter the No. of Zeros");
			    int difficulty=in.nextInt();
			    System.out.println("The No of Zeros:"+difficulty);
			    blockObj[0] = new Block(bCName,transactionData,difficulty);
			    blockObj[0].blockId=1;
			    blockObj[0].previousHash="0000000000000000000000000000000000000000000000000000000000000000";
			    blockObj[0].blockHash= hash.generateHash(blockObj[0].transactionData+blockObj[0].previousHash+blockObj[0].timeStamp+blockObj[0].nonce);
			    nonce(blockObj[0]);
			    
			}
		    else if(s==2) 
		    {
		    	if(flag==1)
		    	{
				    System.out.println("Enter the name of existing blockchain");
				    String bCName=in.next();
				    if( blockObj[0].bCName.equalsIgnoreCase(bCName))
				    {
					    System.out.println("The Blockchain name is:"+bCName);
				    	System.out.println("Enter the transaction data");
				    	String transactionData=in.next();
					    System.out.println("The transaction data:"+transactionData);
					    int difficulty=blockObj[0].difficulty;
					    System.out.println("The No of Zeros:"+difficulty);
					    blockObj[i] = new Block(bCName,transactionData,difficulty);
					    blockObj[i].blockId=i+1;
					    blockObj[i].previousHash="0000000000000000000000000000000000000000000000000000000000000000";
					    blockObj[i].blockHash= hash.generateHash(blockObj[i].transactionData+blockObj[i].previousHash+blockObj[i].timeStamp+blockObj[i].nonce);
					    nonce(blockObj[i]);
					    i++;
				    }
				    else
				    {
				    	System.out.println("Wrong BLock Chain");
				    }
		    	}
		    	else
		    	{
		    		System.out.println("Create BLock Chain First");
		    	}
		    }
		    else if(s==3) 
		    {   	
	    		System.out.println("Block"+(i));
	    		System.out.println("The Blockchain name is:"+blockObj[i-1].bCName);
	    		System.out.println("The transaction data:"+blockObj[i-1].transactionData);
	    		System.out.println("The No of Zeros:"+blockObj[i-1].difficulty);
	    		System.out.println("The block ID:"+blockObj[i-1].blockId);
	    		System.out.println("The timestamp:"+blockObj[i-1].timeStamp);
	    		System.out.println("The Previous Hash:"+blockObj[i-1].previousHash);
	    		System.out.println("The Current Hash:"+blockObj[i-1].blockHash);
	    		//oos.writeObject(blockObj[j]);
	    		//System.setOut(out);
			    	
		    }
		    else if(s==4) 
		    {
		    	for(int j=0;j<i;j++)
		    	{   
		    		System.out.println("Block"+(j+1));
		    		System.out.println("The Blockchain name is:"+blockObj[j].bCName);
		    		System.out.println("The transaction data:"+blockObj[j].transactionData);
		    		System.out.println("The No of Zeros:"+blockObj[j].difficulty);
		    		System.out.println("The block ID:"+blockObj[j].blockId);
		    		System.out.println("The timestamp:"+blockObj[j].timeStamp);
		    		System.out.println("The Previous Hash:"+blockObj[j].previousHash);
		    		System.out.println("The Current Hash:"+blockObj[j].blockHash);
		    		//oos.writeObject(blockObj[j]);
		    		//System.setOut(out);
		    		
		    	}
		    	System.out.println("From File");
		    	for(int j=0;j<i;j++)
		    	{ 
		    		// write object to file
		    		oos.writeObject(blockObj[j].bCName);
		    		oos.writeObject(blockObj[j].transactionData);
		    		oos.writeObject(blockObj[j].blockId);
		    		oos.writeObject(blockObj[j].timeStamp);
		    		oos.writeObject(blockObj[j].previousHash);
		    		oos.writeObject(blockObj[j].blockHash);
		    		// read object from file
					String name = (String)ois.readObject();
					String tdata=(String) ois.readObject();
					int bId=(int) ois.readObject();
					long ts=(long) ois.readObject();
					String pH=(String) ois.readObject();
					String bH=(String) ois.readObject();
					//ois.close();
					//System.out.println("BlockChainNfrom File:" + blockObj[j].getbCName());
		    		System.out.println("The Blockchain name is:"+name);
		    		System.out.println("The transaction data:"+tdata);
		    		//System.out.println("The No of Zeros:"+blockObj[j].getDifficulty());
		    		System.out.println("The block ID:"+bId);
		    		System.out.println("The timestamp:"+ts);
		    		System.out.println("The Previous Hash:"+pH);
		    		System.out.println("The Current Hash:"+bH);
		    	}
		    	
		    	//oos.close();
		    	break;
		    }
		    else 
		    {
		    	System.out.println("Wrong Inputs");
		    }   
		    
		}
		in.close();
		//System.out.println("You exited");
	    }catch(Exception e){
	       	e.printStackTrace();
	    
	    }
	}	
	public static void nonce(Block block) {
		
		while(notGoldenHash(block)) {
			block.getHash();
			block.incrementNonce();
		}
		
		System.out.println( "Block"+"-"+block.blockId+" has just generated...");
		System.out.println("Hash is: "+block.getHash());
		
	}
	
	public static boolean notGoldenHash(Block block) {
		String leadingZeros = new String(new char[(int) block.difficulty]).replace('\0', '0');
		return !block.getHash().substring(0,block.difficulty).equals(leadingZeros);
	}

}



