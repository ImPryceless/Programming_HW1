/*
Group HW Team members:
J.P. O'Malley
Ikuseghan Pryce
Ismael Garrido Rodriguez
Shenna Marie-P Wyco
Geidy Dorviny Molina
*/


import java.util.concurrent.Semaphore;
import java.util.Random;

public class Connector
{
    public static void main(String[] args){

        // Number of possible students
        int numberOfPossibleStudents = 5;
        
        // Instantiate a new Semaphores object
        SignalSemaphore wakeup = new SignalSemaphore();
        SignalSemaphore numOfChairs = new SignalSemaphore(3);
        SignalSemaphore available = new SignalSemaphore(1);
        
        
        // Used for randomly generating program time.
        Random studentWait = new Random();
        
        // For loop creating student threads and iterating thru them
        for (int i = 0; i < numberOfPossibleStudents; i++)
        {
            Thread student = new Thread(new Student(studentWait.nextInt(20), wakeup, numOfChairs, available, i+1));
            student.start();
        }
        
        // Creating the TA Thread object
        Thread ta = new Thread(new TeachingAssistant(wakeup, numOfChairs, available));
        ta.start();
       
   
    	}
    
}

//new class
public class SignalSemaphore {
		
	    //boolean variable to signal the TA
	    private boolean signal = false;

	    public SignalSemaphore(int i) {
			// TODO Auto-generated constructor stub
		}

		public SignalSemaphore() {
			// TODO Auto-generated constructor stub
		}

		// To send a signal if true
	    public synchronized void take() {
	        this.signal = true;
	        this.notify();
	    }

	    // Will wait until it receives a signal before continuing if not throw an exception
	    public synchronized void release() throws InterruptedException{
	        while(!this.signal) wait();
	        this.signal = false;
	    }

		public boolean tryAcquire() {
			// TODO Auto-generated method stub
			return false;
		}

		public int availablePermits() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void acquire() {
			// TODO Auto-generated method stub
			
		}
}

//new class
import java.util.concurrent.Semaphore;

public class Student implements Runnable{
	      
	    // Time to program before asking for help (in seconds).
	    private int timeToProgram;
	    
	    // Student number.
	    private int numOfStudent;

	    // Semaphore uto wake up TA.
	    private SignalSemaphore wakeup;

	    // Semaphore to wait in chairs 
	    private SignalSemaphore waitingChairs;

	    // Semaphore used to determine if TA is available.
	    private SignalSemaphore available;

	    // A reference to the current thread.
	    private Thread t;

	    // Non-default constructor.
	    public Student(int startProgram, SignalSemaphore wake, SignalSemaphore chairs, SignalSemaphore a, int numStu)
	    {
	        timeToProgram = startProgram;    
	        wakeup = wake;
	        waitingChairs = chairs;
	        available = a;
	        numOfStudent = numStu;
	        t = Thread.currentThread();
	    }
	    //overloaded constructor taking 5 parameters
	    public Student(int nextInt, SignalSemaphore wakeup2, Semaphore chairs2, Semaphore available2, int num) {
			
		}

		
	    @Override
	    public void run()
	    {
	        // Infinite loop.
	        while(true)
	        {
	            try
	            {
	               // Program first.
	               System.out.println("Student " + numOfStudent + " has started programming for " + timeToProgram + " seconds.");
	               Thread.sleep(timeToProgram * 1000);
	                
	               // Check to see if TA is available first.
	               System.out.println("Student " + numOfStudent + " is checking to see if TA is available.");
	               //Wait for TA to finish with other student.
	               if (available.tryAcquire())
	               {
	                   try
	                   {
	                       // Wakeup the TA.
	                       wakeup.take();
	                       System.out.println("Student " + numOfStudent + " has woke up the TA.");
	                       System.out.println("Student " + numOfStudent + " has started working with the TA.");
	                       Thread.sleep(5000);
	                       System.out.println("Student " + numOfStudent + " has stopped working with the TA.");
	                   }
	                   catch (InterruptedException e)
	                   {
	                       // Something bad happened.
	                       continue;
	                   }
	                   finally
	                   {
	                       available.release();
	                   }
	               }
	               else
	               {
	                   // Check to see if any chairs are available 
	                   System.out.println("Student " + numOfStudent + " could not see the TA.  Checking for available chairs.");
	                   if (waitingChairs.tryAcquire())
	                   {
	                       try
	                       {
	                          
	                           System.out.println("Student " + numOfStudent + " is sitting outside the office.  "
	                                   + "He is #" + ((3 - waitingChairs.availablePermits())) + " in line.");
	                           available.acquire();
	                           System.out.println("Student " + numOfStudent + " has started working with the TA.");
	                           Thread.sleep(5000);
	                           System.out.println("Student " + numOfStudent + " has stopped working with the TA.");
	                           available.release();
	                       }
	                       catch (InterruptedException e)//catch exception else continue
	                       {
	                           continue;
	                       }
	                   }
	                   else
	                   {
	                       System.out.println("Student " + numOfStudent + " could not see the TA and all chairs were taken.  Back to programming!");
	                   }
	               }
	            }
	            catch (InterruptedException e)
	            {
	                break;
	            }
	        }
	    }
}

//new class
import java.util.concurrent.Semaphore;

public class TeachingAssistant implements Runnable {
	
	    // Semaphore to wake up TA
	    private SignalSemaphore wakeup;

	    // Semaphore to wait in chairs 
	    private SignalSemaphore waitingChairs;

	    // Semaphore to determine if TA is available
	    private SignalSemaphore available;

	    // A reference to the current thread.
	    private Thread t;
	    //overloaded constructor
	    public TeachingAssistant(SignalSemaphore wake, SignalSemaphore chairs, SignalSemaphore a)
	    {
	        t = Thread.currentThread();
	        wakeup = wake;
	        waitingChairs = chairs;
	        available = a;
	    }
	    //overloaded constructor
	    public TeachingAssistant(SignalSemaphore wakeup2, Semaphore chairs2, Semaphore available2) {
			
		}

		@Override
	    public void run()
	    {
	        while (true)
	        {
	            try
	            {
	                System.out.println("No students left.  The TA is going to nap.");
	                wakeup.release();
	                System.out.println("The TA was awoke by a student.");
	                
	                t.sleep(5000);
	                
	                // If there are other students waiting.
	                if (waitingChairs.availablePermits() != 3)
	                {
	                    do
	                    {
	                        t.sleep(5000);
	                        waitingChairs.release();
	                    }
	                    while (waitingChairs.availablePermits() != 3);                   
	                }
	            }
	            catch (InterruptedException e)
	            {
	                continue;
	            }
	        }
	    }
	
}



