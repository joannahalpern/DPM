import lejos.nxt.*;

/**
  * A simple concurrency test using buttons and the NXT display @author ff
  * Example from class 
  */
public class CT2 extends Thread {

/* Class variables - global and persistent */
          
          private static int myMutex=0;
          
/* Instance variables for this class */
          
          private int active;
          private int count;
          private int status;
          private int disp_area;
          private int last_press;

/* Constructor for this class */
      	
          CT2(int disp_area) {
        		  this.disp_area = disp_area;
          }
          /* Create an object that can be used for synchronization across threads. */

          static class theLock extends Object {
                  }
                  static public theLock lockObject = new theLock();
                  

/* Main method - initialize the LCD display */

  public static void main(String[] args)
      {
        int i;

        /* Set up the display area  */
        LCD.clear();                                  /* Erase screen  */
        for (i=0; i<100; i++){                        /* Draw seperator */
            LCD.setPixel(i,32,1);
        }
        LCD.drawString("Task 1",0,0,false);           /* Task 1 ID */
        LCD.drawString("Task 2",0,7,false);           /* Task 2 ID */
        LCD.drawString("C1=",8,2,false);              /* Task 1 counter */
        LCD.drawString("C2=",8,5,false);              /* Task 1 counter */
        /* Create instances of two threads to manage button monitoring. */

        CT2 Thread1 = new CT2(0);
        CT2 Thread2 = new CT2(1);
        Thread1.start();                               /* Upper display */
        Thread2.start();                               /* Lower display */
      }
  
   public void run()
      {
        boolean blocked;
        
        active=0;                             /* Initialize on instance */
        count=0;
	last_press=0;
       
        while (true) {      
          synchronized(lockObject) {
            status = Button.readButtons();    /* Read current button state */
          }
          if (status==last_press)             /* Crude press determination */
	      status=0;
	  else
	      last_press=status;
          /* Case 1: currently inactive, in top panel, left arrow button pressed.
           */       
                    if ((active==0)&&(disp_area==0)&&(status==2)) {
                      blocked=true;
                      while (blocked){                  /* Implements mutex wait */
                        synchronized(lockObject){
                          if (myMutex == 0) {
                            myMutex=1;
                            blocked=false;
                          }
                        }
                      }
                      active=1;                         /* Change state */
                      LCD.drawString("(*)",0,2,false);  /* Upper indicator on */
                      LCD.drawString("   ",0,5,false);  /* Lower indicator off */                 
                    }
           
                    /* Case 2:  currently inactive, in bottom panel, right arrow putton pressed.        
                     */
                              
                              else if ((active==0)&&(disp_area==1)&&(status==4)) {
                                blocked=true;
                                while (blocked){                  /* Implements mutex wait */
                                  synchronized(lockObject){
                                    if (myMutex == 0) {
                                      myMutex=1;
                                      blocked=false;
                                    }
                                  }
                                }
                                active=1;                         /* Change state */
                                LCD.drawString("(*)",0,5,false);  /* Upper indicator on */
                                LCD.drawString("   ",0,2,false);  /* Lower indicator off */                 
                              }
                     
                    /* Case 3:  currently active, in top panel, left arrow button pressed.
                     */
                              
                              else if ((active==1)&&(disp_area==0)&&(status==2)) {
                                synchronized(lockObject){
                                	myMutex=0;                    /* Clear mutex */
                                }
                                LCD.drawString("   ",0,2,false);  /* Upper indicator off */
                                active=0;                         /* Change state */
                              }

                    /* Case 4:  currently active, in bottom panel, right arrow button pressed.
                     */
                     
                              else if ((active==1)&&(disp_area==1)&&(status==4)) {
                                synchronized(lockObject){         /* Clear mutex */
                                    myMutex=0;
                                }
                                LCD.drawString("   ",0,5,false);  /* Lower indicator off */
                                active=0;                         /* Change state */
                              }

                    /* Case 5:  currently active, in top panel, enter button pressed.         
                     */
                              
                              else if ((active==1)&&(disp_area==0)&&(status==1)) {
                                count++;                          /* Increment the count */
                                LCD.drawInt(count,4,12,2);        /* And display */
                              }
                              
                    /* Case 6:  currently active, in bottom panel, enter button pressed.          
                     */
                              
                              else if ((active==1)&&(disp_area==1)&&(status==1)) {
                                count++;                          /* Increment the count */
                                LCD.drawInt(count,4,12,5);        /* And display */
                              }

                    /* Escape Key pressed - kill the thread and return
                     */
                             else if (status==8){
                            	NXT.shutDown();
                              }        
                            }
                          }
                    }
