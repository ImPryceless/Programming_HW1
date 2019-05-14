import java.util.concurrent.Semaphore;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Connector
{
    public static void main(String[] args) 
    {	
	try {

		//Taking input from user and turning into integer
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter the number of students that need to be helped: ");
		int numberofStudents = Integer.parseInt(input.readLine());

		// Create semaphores.
		SignalSemaphore wakeup = new SignalSemaphore();
		//semaphore for the number of chairs in hallway
		Semaphore chairs = new Semaphore(3);
		//semaphore for TA availability 
		Semaphore available = new Semaphore(1);

		//Used to generate programming time for students
		Random studentWait = new Random();


		// Creates a thread for every student
		for (int i = 0; i < numberofStudents; i++) {
			Thread student = new Thread(new Student(studentWait.nextInt(20), wakeup, chairs, available, i + 1));
			student.start();
		}

		// Create and start Teaching Assistant Thread.
		Thread ta = new Thread(new TeachingAssistant(wakeup, chairs, available));
		ta.start();

	} catch (Exception e) {
		System.out.println("ERROR please enter a valid number: \n");
	}


	}
}

/**
	 * This semaphore implementation is used to "wakeup" the TA.
	 */
public class SignalSemaphore {

	    private boolean signal = false;

	    // Used to send the signal.
	    public synchronized void take() {
	        this.signal = true;
	        this.notify();
	    }

	    // Will wait until it receives a signal before continuing.
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


import java.util.concurrent.Semaphore;

/**
	 This is the student thread.  It will alternate between programming and seeking help from the TA.
	 */
public class Student implements Runnable{
	      
	    // Time to program before asking for help (in seconds).
	    private int programTime;
	    
	    // Student number.
	    private int studentNum;

	    // Semaphore used to wakeup TA.
	    private SignalSemaphore wakeup;

	    // Semaphore used to wait in chairs outside office.
	    private SignalSemaphore chairs;

	    // Mutex lock (binary semaphore) used to determine if TA is available.
	    private SignalSemaphore available;

	    // A reference to the current thread.
	    private Thread t;

	    // Non-default constructor.
	    public Student(int program, SignalSemaphore w, SignalSemaphore c, SignalSemaphore a, int num)
	    {
	        programTime = program;    
	        wakeup = w;
	        chairs = c;
	        available = a;
	        studentNum = num;
	        t = Thread.currentThread();
	    }

	    public Student(int nextInt, SignalSemaphore wakeup2, Semaphore chairs2, Semaphore available2, int num) {
			// TODO Auto-generated constructor stub
		}

		/**
	     * The run method will infinitely loop between programming and
	     * asking for help until the thread is interrupted.
	     */
	    @Override
	    public void run()
	    {
	        // Infinite loop.
	        while(true)
	        {
	            try
	            {
	               // Program first.
	               System.out.println("Student " + studentNum + " has started programming for " + programTime + " seconds.");
	               t.sleep(programTime * 1000);
	                
	               // Check to see if TA is available first.
	               System.out.println("Student " + studentNum + " is checking to see if TA is available.");
	               if (available.tryAcquire())
	               {
	                   try
	                   {
	                       // Wakeup the TA.
	                       wakeup.take();
	                       System.out.println("Student " + studentNum + " has woke up the TA.");
	                       System.out.println("Student " + studentNum + " has started working with the TA.");
	                       t.sleep(5000);
	                       System.out.println("Student " + studentNum + " has stopped working with the TA.");
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
	                   // Check to see if any chairs are available.
	                   System.out.println("Student " + studentNum + " could not see the TA.  Checking for available chairs.");
	                   if (chairs.tryAcquire())
	                   {
	                       try
	                       {
	                           // Wait for TA to finish with other student.
	                           System.out.println("Student " + studentNum + " is sitting outside the office.  "
	                                   + "He is #" + ((3 - chairs.availablePermits())) + " in line.");
	                           available.acquire();
	                           System.out.println("Student " + studentNum + " has started working with the TA.");
	                           t.sleep(5000);
	                           System.out.println("Student " + studentNum + " has stopped working with the TA.");
	                           available.release();
	                       }
	                       catch (InterruptedException e)
	                       {
	                           // Something bad happened.
	                           continue;
	                       }
	                   }
	                   else
	                   {
	                       System.out.println("Student " + studentNum + " could not see the TA and all chairs were taken.  Back to programming!");
	                   }
	               }
	            }
	            catch (InterruptedException e)
	            {
	                break;
	            }
		//break the infinite loop
		break;
	        }
	    }
}

import java.util.concurrent.Semaphore;

public class TeachingAssistant implements Runnable {
	
	    // Semaphore used to wakeup TA.
	    private SignalSemaphore wakeup;

	    // Semaphore used to wait in chairs outside office.
	    private SignalSemaphore chairs;

	    // Mutex lock (binary semaphore) used to determine if TA is available.
	    private SignalSemaphore available;

	    // A reference to the current thread.
	    private Thread t;
	    
	    public TeachingAssistant(SignalSemaphore w, SignalSemaphore c, SignalSemaphore a)
	    {
	        t = Thread.currentThread();
	        wakeup = w;
	        chairs = c;
	        available = a;
	    }
	    
	    public TeachingAssistant(SignalSemaphore wakeup2, Semaphore chairs2, Semaphore available2) {
			// TODO Auto-generated constructor stub
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
	                if (chairs.availablePermits() != 3)
	                {
	                    do
	                    {
	                        t.sleep(5000);
	                        chairs.release();
	                    }
	                    while (chairs.availablePermits() != 3);                   
	                }
	            }
	            catch (InterruptedException e)
	            {
	                // Something bad happened.
	                continue;
	            }
	        }
	    }
	
}


