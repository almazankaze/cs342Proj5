import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.TimerTask; 

public abstract class ServerNetwork {
	
	private ArrayList<Serializable> activeConnections = new ArrayList<>(); // used to keep track of active players and their ID's
	private ConnThread connthread = new ConnThread();
	private Consumer<Serializable> callback;
	private ArrayList<myThread> clients = new ArrayList<myThread>();
	private QuestionBank questionBank = new QuestionBank();
	
	public ServerNetwork(Consumer<Serializable> callback) {
		this.callback = callback;
		connthread.setDaemon(true);
	}
	
	// start the thread connthread
	public void startConn() throws Exception{
		connthread.start();
	}
	
	// send messages
	public void send(Serializable data) throws Exception{
		
		for(int i = 0; i < clients.size(); i++) {
			try {
				clients.get(i).out.writeObject(data);
		
			}catch(Exception e) {
				System.out.println("One of the clients closed");
			}
		}
	}
	
	// close the socket in thread connthread
	public void closeConn() throws Exception{
		connthread.socket.close();
	}
	
	// convert the arraylist of active users to a string to send to server.
	public Serializable convertToString(ArrayList<Serializable> arr)  {
		Serializable result = "";
		for(int i =0; i<arr.size(); i++) {
			result = result + (arr.get(i) + " ");
		}

		return result;

	}
	
	public void sendtoX(int clientID, ObjectOutputStream out) throws Exception {

		Serializable message = "ID " + clientID;
		if(clients.get(clientID-1) == null) {
			System.out.println("clients is empty\n");
		}
		out.writeObject(message);
	}
	
	// abstract methods
	abstract protected boolean isServer();
	abstract protected String getIP();
	abstract protected int getPort();
	
	class ConnThread extends Thread{
		private Socket socket;
		private ObjectOutputStream out;
		private int clientID = 1;
		private int i=0;
		public void run() {
			try{
				
				// if server, listen for connections from clients
				ServerSocket server = new ServerSocket(getPort());
				
				// server start message
				callback.accept("Server created \nNow waiting for clients...");
				
				while(true) {
					myThread t = new myThread(server.accept(), clientID);
					t.start();
					if(clients.size()<4) {
						
						clients.add(t);
					
					}
					callback.accept("Player " + clientID + " joined");
					if(clients.size() == 4) {
						callback.accept("There are 4 players, the game will now begin");
						while(clients.get(3).out == null) {
							Thread.sleep(1000);
						}
						
						// send message to clients that the game has started
						send("Game has started");
						//REPLACE THIS WITH FIRST QUESTION
						//clients.get(0).setCorrectAnswer("A");
						//clients.get(1).setCorrectAnswer("A");
						//clients.get(2).setCorrectAnswer("A");
						//clients.get(3).setCorrectAnswer("A");
						
						// send clients the question
						send("Q: " + questionBank.getQuestion());
						send(questionBank.getChoices());
						//clients.get(3).out.writeObject("Q: Question");
						
						
					}
					clientID++;
				}
			}
			catch(Exception e) {
				callback.accept("connection Closed");
			}
		}
	}
	
	// separate thread for the client
	class myThread extends Thread {
		
		private Socket socket;
		private ObjectOutputStream out;
		private int myID;
		private boolean inGame;
		private int points;
		private String correctAnswer;
		private String answer;
		myThread(Socket s, int i) {
			this.socket = s;
			this.myID = i;
			this.inGame = true;
		}
		
		/**
		 * @return the answer
		 */
		public String getAnswer() {
			return answer;
		}

		/**
		 * @param answer the answer to set
		 */
		public void setAnswer(String answer) {
			this.answer = answer;
		}

		public void setCorrectAnswer(String string) {
			// TODO Auto-generated method stub
			this.correctAnswer = string;
		}

		public int getPoints() {
			// TODO Auto-generated method stub
			return this.points;
		}
		public void setPoints(int points) {
			this.points = points;
		}

		public void run() {
			try(
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){
				
				this.out = out;
				socket.setTcpNoDelay(true);
				
				//sets the ID-1 index of the player to true, indicating they are connected
				activeConnections.add("true");
				//inform the client of their id
				sendtoX(myID,out);
				send(convertToString(activeConnections));
				System.out.println("\nSent activeConnections with a size of: " + activeConnections.size());
				//out.writeObject("A: Yessir");
				
				while(true) {
					if(clients.size()>3) {
						System.out.println("here?");
						if(clients.get(0).inGame == false) {
							clients.get(0).setAnswer("No Answer");
							System.out.println("set 0 to no answer");
						}
						if(clients.get(1).inGame == false) {
							clients.get(1).setAnswer("No Answer");
							System.out.println("set 1 to no answer");
						}
						if(clients.get(2).inGame == false) {
							clients.get(0).setAnswer("No Answer");
							System.out.println("set 2 to no answer");
						}
						if(clients.get(3).inGame == false) {
							clients.get(3).setAnswer("No Answer");
							System.out.println("set 3 to no answer");
						}
						if(clients.get(0).getAnswer()!=null && clients.get(1).getAnswer()!=null && clients.get(2).getAnswer()!=null
								&&clients.get(3).getAnswer()!=null ) {
							
							//GET RANDOM QUESTION AND DO ALL this logic
							send("Q: " + questionBank.getQuestion());
							send(questionBank.getChoices());
							//clients.get(0).setCorrectAnswer("A");
							//clients.get(1).setCorrectAnswer("A");
							//clients.get(2).setCorrectAnswer("A");
							//clients.get(3).setCorrectAnswer("A");
							clients.get(0).setAnswer(null);
							clients.get(1).setAnswer(null);
							clients.get(2).setAnswer(null);
							clients.get(3).setAnswer(null);
							
						}
						
						//check if one of them won
						else {
							for(int i = 0; i< clients.size(); i++) {
								if(clients.get(i).getPoints() == 3) {
									clients.get(i).out.writeObject("YOU WON");
									for(int j = 0; j<clients.size();j++) {
										if(i!=j) {
											clients.get(i).out.writeObject("YOU LOST");
										}
									}
								}
								
								
							}
							
						}
					}
					
					Serializable data = (Serializable) in.readObject();
					
					// show server what client picked
					callback.accept("Player " + myID + " picked " + data);
					this.setAnswer(data.toString().intern());
					if(getAnswer()!=null&&getAnswer().equals(questionBank.getAnswer())) {
						out.writeObject("Good job,you got it correct");
						//System.out.println("here");
					}
					else if (getAnswer()!=null && getAnswer() != questionBank.getAnswer()) {
						out.writeObject("Incorrect!");
					}
					//check timer here if it equals 10 check, otherwise dont do anything
					//System.out.println(inGame);
					//out.writeObject("You have 10 seconds to submit an answer");
					//out.writeObject("new question");
					
					
					// if this client is currently playing a game, send choice to server
				}
						
			}
			catch(Exception e) {
				// send an updated list to the remaining players connected,
				// one without this player
				try {
					activeConnections.set(myID,"false");
				}catch(Exception f) {
					System.out.println("Socket was already closed");
				}

				try {
					send(convertToString(activeConnections)); }
				catch(Exception f) {
					
					f.printStackTrace();
					
				}
				callback.accept("Player " + myID + " disconnected");
				clients.get(myID-1).inGame = false;
				System.out.println("set to null");
			}
		}
	}
}
 
/*
while(clients.get(i).getPoints()<4) {
	//new question data structure
	//get new question
	//set up possible answers
	//get answer
	//wait 10 seconds for answer even if they already submitted an answer(makes it easier)
	//start server timer here too
	//update everyone
	clients.get(i).setCorrectAnswer("A");
	//send("You have 10 seconds to submit an answer");
	//send("new question");
	clients.get(i).inGame = true;
	//Thread.sleep(10000);
	//Maybe send a message like startTimer?
}*/
