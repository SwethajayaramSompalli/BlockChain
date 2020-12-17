package Blockchain;

import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TransportationLayer {
    private static InetAddress myAddres = InetAddress.getLoopbackAddress(); // ip address of this machine
    //private static HashSet<peer> peers = new HashSet<peer>();
    private static ArrayList<Block> blockChain = new ArrayList<Block>(); // blockchain
    public static volatile ArrayList<Block> blocksBuffer = new ArrayList<Block>(); // list of blocks that recently arrived
    //acknowledgements maintainer for each block hash
    private static volatile HashMap<String, HashSet<Integer>> acks = new HashMap<String, HashSet<Integer>>();

    /**
     *
     */
    public static volatile Thread connectionHandler = null; // seperate thread for handling connections

    /**
     *
     */
    public static volatile Thread acknowledgmentHandler = null; // seperate thread for handling acknowledgments

    /**
     *
     */
    public static volatile Thread blockchainHandler = null; // seperate thread for handling blockchain itself
    private static ServerSocket peer; // Server socket to receive blocks
    
    public static int pid = 0; // default process id for the process

    public static volatile Boolean exit = false; // exit instruction set to false
    
    // hash value received from the command prompt (as we are generating dummy data for this part of the project)
    // (as up till now, integration with differect layers has not yet been achieved)
    public String hashForBlock; 
    
    
    //private static int[] ports = new int[]{4001, 4002, 4003, 4004, 4005};
    // ports that are used by each peer. Each index represents port for that process id
    private static int[] ports = new int[]{4001, 4002, 4003}; 
    
    
    // assign process id and hash for the block to be created by this node
    public TransportationLayer(int p, String h){
        //peers = getPeers();
        pid = p;
        hashForBlock = h;
//        sleepingTime = st;
    }
    public TransportationLayer(int p){
        //peers = getPeers();
        pid = p;
        //hashForBlock = h;
//        sleepingTime = st;
    }
    //private HashSet<peer> getPeers(){
        //HashSet<peer> peers = new HashSet<peer>();
        //peers.add(new peer(myAddres, ports[0]));
        //peers.add(new peer(myAddres, ports[1]));
        //peers.add(new peer(myAddres, ports[2]));
        //peers.add(new peer(myAddres, ports[3]));
        //peers.add(new peer(myAddres, ports[4]));
        
        //return peers;
    //}
    
    // initialization of the transportation layer

    /**
     *
     * @throws InterruptedException
     */
    public void init() throws InterruptedException{
        
        // display process id
        System.out.println("Process: " + pid + " started!");
        
        try{

            // initate connection handler thread
            connectionHandler = (new Thread(){

                @Override
                public void run(){
                    handleConnections();
                    return;
                }
            });

            connectionHandler.start();


            // initiate acknowledgment handler thread
            acknowledgmentHandler = (new Thread(){

                @Override
                public void run(){
                    try {
                        handleAcknowledgments();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    return;
                }
            });

            acknowledgmentHandler.start();


            // initiate blockchain handler thread
            blockchainHandler = (new Thread(){

                @Override
                public void run(){
                    try {
                        handleBlockchain();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    return;
                }
            });

            blockchainHandler.start();

            // wait till all the nodes are initialized
            //Thread.sleep(2000);
            
            // dummy block to test the transportation layer
          /*  Block block = new Block(hashForBlock, 
                                    "no hash", 
                                    new Timestamp(System.currentTimeMillis()), 
                                    0, 
                                    "This is my first block", 
                                    pid);
            
            broadcastBlock(block); // request block to be broadcasted
            */
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    // comparator that defines measure of preference between two blocks

    /**
     *
     */
    public static Comparator<Block> comp = (block1, block2) -> {
        int returnVal = Long.compare(block1.timeStamp, block2.timeStamp);
        if(returnVal == 0)
            return Integer.compare(block1.pid, block2.pid);
        return returnVal;
    };
    
    // function assign to connection handler thread
    private void handleConnections(){
        int port = ports[pid]; // port for this process id
        
        try{
            peer = new ServerSocket(port, 0, myAddres); // initate socket connection to read blocks
            // continue until both exit instruction and all received blocks have not been processed
            while(!exit || !blocksBuffer.isEmpty()){ 
                System.out.println("handle connection");
                //System.out.println("Handle Connections! Exit: " + exit + ", blocksBuffer: " + blocksBuffer.size());
                //System.out.println("HandleConnections");
                Socket peerSocket = peer.accept();// accept connection to self
                //initiate input stream
                ObjectInputStream input = new ObjectInputStream(peerSocket.getInputStream());
                //read block from the input stream
                Block recBlock = (Block)input.readObject();
                
                peerSocket.close(); // close the socket
                handleBlock(recBlock); // pass the new block to block handler
                
                // make sure the connection has been closed
                if(!peerSocket.isClosed()){
                    peerSocket.close();
                }
            }
            peer.close(); // close the server socket
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    // handles the received block 
    private static void handleBlock(Block block){
        
        try{
            // if the block has been already acknowledged
            if(block.ack){
                // if we already have an entry for the block in out acknowledgment map
                if(acks.containsKey(block.getHash())){
                    acks.get(block.getHash()).add(block.pid); // add acknowledgment for this block with process id
                }
                else{
                    // if acknowledgment map does not contain an entry for this block
                    // create one and add acknowledgment for this block with process id
                    HashSet<Integer> ackPeers = new HashSet<Integer>();
                    ackPeers.add(block.pid);
                    acks.put(block.getHash(), ackPeers);
                }
            }
            else{
                blocksBuffer.add(block); // if this block has not been acknowledged, add on to the buffer
                Collections.sort(blocksBuffer, comp); // sort the buffer of received blocks
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    // function assigned to our acknowledgment handler thread

    /**
     *
     * @throws InterruptedException
     */
    public void handleAcknowledgments() throws InterruptedException{
        
        // continue until both exit instruction and all received blocks have not been processed
        while(!exit || !blocksBuffer.isEmpty()){
            //System.out.println("handleAcknowledgments");
            Thread.sleep(500); // for syncronization
            if(!blocksBuffer.isEmpty()){ // buffer of received block is not empty
                
                
                //System.out.println("Handle Acknowledgments! Exit: " + exit + ", blocksBuffer: " + blocksBuffer.size());
            
                
                Block block = blocksBuffer.get(0); // get the block on the top
                
                if(!hasAlreadyReceived(block)){// if this block has not already been received/handled
                    
                    // create acknowledgment for this block
                	/*
                    Block ackBlock = new Block(block.getHash(), 
                                                block.getPrevHash(),
                                                block.getTimeStamp(),
                                                block.getNonce(),
                                                block.getInfo(),
                                                true,
                                                pid);*/
                    
                	Block ackBlock = new Block(block.bCName, block.transactionData, block.difficulty, block.ack, block.pid);
                	ackBlock.timeStamp = block.timeStamp;
                	ackBlock.previousHash = block.previousHash;
                	
                    broadcastBlock(ackBlock); // broadcast the acknowledgment
                    
                    // if acknowledgment mapper already contains an entry for this block
                    // add process id for this block
                    if(acks.containsKey(ackBlock.getHash())){
                        acks.get(ackBlock.getHash()).add(pid);
                    }
                    else{
                        // if not create an entry for this block and add the process id
                        HashSet<Integer> ackPeers = new HashSet<Integer>();
                        ackPeers.add(ackBlock.pid);
                        acks.put(ackBlock.getHash(), ackPeers);
                    }
                }
                
            }
        }
        
    }
    
    // check if we have already received the block or not

    /**
     *
     * @param block
     * @return
     */
    public Boolean hasAlreadyReceived(Block block){
        if(!acks.isEmpty() && acks.containsKey(block.getHash())){
            if(acks.get(block.getHash()).contains(pid)){
                return true;
            }
        }
        return false;
    }
    
    // broadcast the block

    /**
     *
     * @param block
     */
    public void broadcastBlock(Block block){
        
        try{
            
            Socket peerSocket; // socket for connecting to peers
            ObjectOutputStream output; // output stream for writing blocks
            
            // for each ports / peers
            for(int port : ports){
                
                peerSocket = new Socket(myAddres, port); // establish connection
                output = new ObjectOutputStream(peerSocket.getOutputStream()); // open output stream
                output.writeObject(block); // write block on to the stream
                output.close(); // close the output buffer
                peerSocket.close(); // close the socket connection with the peer
                
                // make sure the socket is closed
                if(!peerSocket.isClosed())
                    peerSocket.close();
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    // function assigned to blockchain handler thread
    private void handleBlockchain() throws InterruptedException{
        
        // continue until both exit instruction and all received blocks have not been processed
        while(!exit || !blocksBuffer.isEmpty()){
            //System.out.println("handleBlockChain");
            //System.out.println("Handle Blockchain! Exit: " + exit + ", blocksBuffer: " + blocksBuffer.size());
            
            Thread.sleep(100); // for synchronization purposes
            if(!blocksBuffer.isEmpty()){ // if all blocks have not been handled
                
                Block block = blocksBuffer.get(0);// get the block on the top
                
                // if we have an entry for this block in the acknowledgment map
                if(acks.containsKey(block.getHash())){
                    //System.out.println("current block im working on: " + block.getHash() + ", acks rec: " + acks.get(block.getHash()).size());
                    
                    // if we have received acknowledgment from all of the peers for this block
                    if(acks.get(block.getHash()).size() == ports.length){
                        
                        // if the block is a valid block
                        if(isValid(block)){
                            
                            // if this is not the first block
                            if(blockChain.size() != 0)
                                block.setPrevHash(blockChain.get(blockChain.size() - 1).getHash());
                            
                            // display the block
                            printBlock(block);
                            // add the block to the chain
                            blockChain.add(block);
                        }
                        else{
                            printInValidBlock(block); // display the invalid block and do not add it to the chain
                        }
                        
                        blocksBuffer.remove(0); // remove the block from received block buffer
                        //System.out.println("\n-------------------------------\nExit: " + exit + ", blocksBuffer.isEmpty: " + blocksBuffer.isEmpty());
                    }
                    
                }
                
            }
        }
        
    }
    
    // check if the block complies to the difficulty level
    private Boolean isValid(Block block){
        for(int i = 0; i < block.getDifficulty(); i++){
            if(block.getHash().charAt(i) != '0')
                return false;
        }
        return true;
    }
    
    // print a block
    private void printBlock(Block block){
  
    	System.out.println("\n----------------------------------------------------");
        System.out.println("My Process id: " + pid);
        System.out.println("Block Successfully Added!");
        
    	block.printBlock();
    	System.out.println("----------------------------------------------------\n");
/*        
        System.out.println("\n----------------------------------------------------");
        System.out.println("My Process id: " + pid);
        System.out.println("Block Successfully Added!");
        System.out.println("Block Hash: " + block.getHash());
        System.out.println("Block PrevHash: " + block.getPrevHash());
        System.out.println("Block Timestamp: " + block.getTimeStamp());
        System.out.println("Block Info: " + block.getInfo());
        System.out.println("Pid: " + block.pid);
        System.out.println("----------------------------------------------------\n");
*/    }
    
    
    // print an invalid block
    private void printInValidBlock(Block block){
        
        
        System.out.println("\n----------------------------------------------------");
        System.out.println("My Process id: " + pid);
        System.out.println("THE BLOCK WAS NOT ADDED!");
        block.printBlock();
//        System.out.println("Block Hash: " + block.getHash());
//        //System.out.println("Block PrevHash: " + block.getPrevHash());
//        System.out.println("Block Timestamp: " + block.getTimeStamp());
//        System.out.println("Block Info: " + block.getInfo());
//        System.out.println("Pid: " + block.pid);
        System.out.println("----------------------------------------------------\n");
    }

}
