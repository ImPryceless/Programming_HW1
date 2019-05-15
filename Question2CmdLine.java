/*
Group HW Team members:
J.P. O'Malley
Ikuseghan Pryce
Ismael Garrido Rodriguez
Shenna Marie-P Wyco
Geidy Dorviny Molina
*/

/* TO RUN THIS PROGRAM FROM THE COMMAND LINE 
 * 
 * 1. Navigate to the directory where the file is located
 * 2. Run the command "javac TeachingAssistant.java" to compile
 * 		a.This will create a class file called TeachingAssistant.class
 * 
 * 3. run the command "java <name-of-class> <argument-passing>". For example "java TeachingAssistant 5"
 * 

 */


import java.util.concurrent.Semaphore;
import java.util.Random;


public class TeachingAssistant {
	
	
   public static void main(String[] args) {
	   
	      try {
	         if (args.length >= 1)
	            new TeachingAssistant(Integer.parseInt(args[0]));
	         else {
	            System.out.println("Please enter number of students to assist as argument. Ex java TeachingAssistant 3");
	            System.exit(0);
	         }
	      } catch (Exception e) {
	         System.out.println("Error parsing number of students.");
	            System.exit(0);
	      }

	      
	   }
   
   public TeachingAssistant(int numberofStudents) {
	   
		// Create semaphores.
		SignalSemaphore WakeUp = new SignalSemaphore();
		//semaphore for the number of chairs in office
		Semaphore seats = new Semaphore(3);
		//semaphore for TA availability 
		Semaphore TAavailable = new Semaphore(1);
		
		
		//Generates waiting time for students
		Random watingTime = new Random();

		// Creates a thread for every student
		for (int i = 0; i < numberofStudents; i++) {
			Thread student = new Thread(new Student(watingTime.nextInt(20), WakeUp, seats, TAavailable, i + 1));
			student.start();
		}

		// Create and start Teaching Assistant Thread.
		Thread TeacherAssisant = new Thread(new TeacherAssistant(WakeUp, seats, TAavailable));
		TeacherAssisant.start();
		
	   }
	


//This signal semaphore is what it's used to wake up the TA
class SignalSemaphore {
	private boolean WakeUpSignal = false;

	// Used to send the signal.
	public synchronized void take() {
		this.WakeUpSignal = true;
		this.notify();
	}

	// Will wait until it receives a signal before continuing.
	public synchronized void release() throws InterruptedException {
		while (!this.WakeUpSignal)
			wait();
		this.WakeUpSignal = false;
	}
}

//The student thread alternates between seeking help from the TA, and programming by themselves

class Student implements Runnable {
	
	/* ----- Variables ------- */
	
	// Time student waits/program before asking TA for help .
	private int waitingTime;

	// Student number.
	private int studentNumber;

   
	/* ----- Semaphores ------- */
	
	// Semaphore used to WakeUp TA.
	private SignalSemaphore WakeUp;

	// Semaphore used to wait in chairs outside office.
	private Semaphore seats;

	//Determines if the TA is available
	private Semaphore TAavailable;
	
	/* ----- Thread ------- */

	// A reference to the current thread.
	private Thread temp;

	/* ----- Constructor ------- */
	
	public Student(int wait, SignalSemaphore w, Semaphore s, Semaphore a, int num) {
		waitingTime = wait;
		WakeUp = w;
		seats = s;
		TAavailable = a;
		studentNumber = num;
		temp = Thread.currentThread();
	}


	//Main thread of execution 
	@Override
	public void run() {
		// Infinite loop.
		while (true) {
			try {
				// Program first.
				System.out
						.println("Student " + studentNumber + " has been waiting for " + waitingTime + " seconds.");
				temp.sleep(waitingTime * 1000);

				// Check to see if TA is TAavailable first.
				System.out.println("Student " + studentNumber + " is checking to see if TA is TAavailable.");
				if (TAavailable.tryAcquire()) {
					try {
						// Wakeup the TA.
						WakeUp.take();
						System.out.println("Student " + studentNumber + " has woke up the TA.");
						System.out.println("Student " + studentNumber + " has started working with the TA.");
						temp.sleep(5000);
						System.out.println("Student " + studentNumber + " has stopped working with the TA.");
					} catch (InterruptedException e) {
						// Something bad happened.
						continue;
					} finally {
						TAavailable.release();
					}
				} else {
					// Check to see if any chairs are TAavailable.
					System.out
							.println("Student " + studentNumber + " TA is busy. Check if there are seats available");
					if (seats.tryAcquire()) {
						try {
							// Wait for TA to finish with other student.
							System.out.println("Student " + studentNumber + " is sitting outside the office. " + "He is #"
									+ ((3 - seats.availablePermits())) + " in line.");
							TAavailable.acquire();
							System.out.println("Student " + studentNumber + " has started working with the TA.");
							temp.sleep(5000);
							System.out.println("Student " + studentNumber + " has finished working with the TA.");
							TAavailable.release();
						} catch (InterruptedException e) {
							continue;
						}
					} else {
						System.out.println("Student " + studentNumber
								+ " No seats available, come back later!");
					}
				}
			} catch (InterruptedException e) {
				break;
			}
			
			//uncomment next line if you wish to execute the loop only once
			//break;
		
		}
	}
}


class TeacherAssistant implements Runnable {
	// Semaphore used to WakeUp TA.
	private SignalSemaphore WakeUp;

	// Semaphore used to wait in chairs outside office.
	private Semaphore seats;

	//Determines if the TA is available
	private Semaphore TAavailable;

	// Holds a reference to the current thread executing 
	private Thread temp;

	/* ----- Constructor ------- */
	
	public TeacherAssistant (SignalSemaphore w, Semaphore s, Semaphore a) {
		temp = Thread.currentThread();
		WakeUp = w;
		seats = s;
		TAavailable = a;
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("No students. The TA will take a nap.");
				WakeUp.release();
				System.out.println("Student has awaken the TA");

				temp.sleep(5000);

				// If there are other students waiting.
				if (seats.availablePermits() != 3) {
					do {
						temp.sleep(5000);
						seats.release();
					} while (seats.availablePermits() != 3);
				}
			} catch (InterruptedException e) {
				continue;
			}
		}
	}
 }

}
