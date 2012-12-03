//Jorge Munoz

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BallGame extends JPanel{

public Rectangle screen, bounds;// brickbounds; // The screen area and boundary.
public JFrame frame; // A JFrame to put the graphics into.
public GameTimerTask gameTask; // The TimerTask that runs the game.
public GameBall ball; // The game ball, a subclass of Rectangle.
private Brick brick; // A brick for the ball to interact with.
private Brick brick2;
private Brick brick3;
private Brick winbrick; //The winning condition
static double g=9.81; //Gravity constant
static final double dt=0.04; //time step
static int xother=-1000;
static int yother=-1000;
static int Stage=1;
static String time;
static Toolkit toolkit =  Toolkit.getDefaultToolkit ();
static Dimension dim = toolkit.getScreenSize();
static final double ke=8.99E9;
static boolean restart=true;
static boolean Continue=false;
double xVel; // The ball's velocity.
double yVel; 
int InitialCond=0; //Variable is used for the progression of the initial conditions
double EnergyScale=0; //EnergyScale is used to prevent particles from gaining energy 
String xVelstr; //JPanel reads in strings, and these are the temporary variable names of the strings
String yVelstr;
boolean mouseDown;
int gamecounter=0; //Used to trigger winning/losing conditions
double MaxTime=30.0; //number of seconds to allow simulation to run
static double[] E = new double[2]; //Electric field
static double B; //Magnetic field constant
boolean winningCond=false;
boolean losingCond=false;
int field; //Field numbers correspond to different field types
double MaxEnergy=0; // The initial energy of the ball KE+PE (to limit the particle going faster than this after collisions)
double[] Bforce= new double[3];

// Create a constructor method:
  public BallGame(){
    super();
    screen = new Rectangle(0, 0, dim.width,dim.height-dim.height/20);
    gamecounter=0;
    bounds = new Rectangle(0, 0, dim.width,dim.height-dim.height/20); // Give some temporary values.
    frame = new JFrame("Stage "+Stage);
    gameTask = new GameTimerTask();
    brick = new Brick();
    brick2= new Brick();
    brick3= new Brick();
    winbrick= new Brick();
    winbrick.setColor(200,0,0);
    ball = new GameBall();
    while(InitialCond!=1); //Infinite loop until the user selects the conditions from JPanel.
    xVel= Double.parseDouble(xVelstr); //convert to a double and then make sure the velocity is under the max limit.
    if(xVel>0.04*screen.width)xVel=0.04*screen.width;
    if(xVel<-0.04*screen.width)xVel=-0.04*screen.width;
    yVel= -Double.parseDouble(yVelstr);
    if(yVel>0.04*screen.width)yVel=0.04*screen.width;
    if(yVel<-0.04*screen.width)yVel=-0.04*screen.width;
    
    g=9.81;
    E[0]=6E4; //could prompt user for E direction
    E[1]=6E4;
    B = -5E3; //could prompt user for B direction (+ in or - out).
}

  // Create an inner TimerTask class that has access to the
  // members of the BallGame.
  class GameTimerTask extends TimerTask{
 
    public void run(){
    	if(winningCond==false && losingCond==false){
    		ball.move();
    		frame.repaint();
    	}
    	else if(losingCond==true && gamecounter==0){// YOU LOSE
    		//final JFrame framex = new JFrame("Level Failed");
    		frame.setSize(dim.width,dim.height-dim.height/20);
    		frame.getContentPane().removeAll();
    		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		JLabel Game= new JLabel("Game", JLabel.CENTER);
    		Game.setFont(new Font("Serif", Font.BOLD, (int)(dim.width/6)));
    		Game.setForeground(Color.red);
    		Game.setSize(screen.width, (int)(3*screen.height/8));
    		Game.setLocation(0,0);
    		JLabel Over= new JLabel("Over", JLabel.CENTER);
    		Over.setFont(new Font("Serif", Font.BOLD, (int)(dim.width/6)));    		
    		Over.setForeground(Color.red);
    		Over.setSize(screen.width, (int)(3*screen.height/8));
    		Over.setLocation(0,(int)(3*screen.height/8)-screen.height/20);
    		Container con = frame.getContentPane();
    		con.setBackground(Color.black);
    	    JButton Restart= new JButton("Restart");
    	    Restart.setSize(screen.width, (int)(screen.height/8));
    	    Restart.setLocation(0, (int)(3*screen.height/4)-screen.height/20);
    	    JButton EndGame= new JButton("End Game");
    	    EndGame.setSize((int)(screen.width), (int)(screen.height/8));
    	    EndGame.setLocation(0,(int)(7*screen.height/8)-screen.height/20);
    	    
    	    Restart.addActionListener(new ActionListener() {
    	  	  
    	        public void actionPerformed(ActionEvent e)
    	        {
    	            restart=true;
    	            frame.getContentPane().removeAll();
    	     		frame.setVisible(false);
    	            Continue=true;
    	     		//framex.setVisible(false);
    	        }
    	 });
    	    EndGame.addActionListener(new ActionListener() {
      	  	  
    	        public void actionPerformed(ActionEvent e)
    	        {
    	     		System.exit(0);
    	        }
    	 });
    		frame.add(Restart);
    		frame.add(EndGame);
    		frame.add(Game);
    		frame.add(Over);
    		frame.setVisible(true);
     		while(!Continue);
     		//Continue=false;
    		gamecounter++;
    	}
    	else if(winningCond==true && gamecounter==0){// YOU WIN
    		//final JFrame framex = new JFrame("Level Complete");
    		frame.setSize(dim.width,dim.height-50);
    		frame.getContentPane().removeAll();
    		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		JLabel Game= new JLabel("Stage "+Stage, JLabel.CENTER);
    		Game.setFont(new Font("Serif", Font.BOLD, (int)(dim.width/6)));
    		Game.setSize(screen.width, (int)(3*screen.height/8));
    		Game.setLocation(0,0);
    		Game.setForeground(Color.blue);
    		JLabel Over= new JLabel("Complete", JLabel.CENTER);
    		Over.setFont(new Font("Serif", Font.BOLD, (int)(dim.width/6)));
    		Over.setForeground(Color.blue);
    		Over.setSize(screen.width, (int)(3*screen.height/8));
    		Over.setLocation(0,(int)(3*screen.height/8));
    	    JButton Restart= new JButton("Restart");
    	    Restart.setSize(screen.width, (int)(screen.height/8));
    	    Restart.setLocation(0, (int)(3*screen.height/4)-screen.height/20);
    	    JButton EndGame= new JButton("End Game");
    	    EndGame.setSize((int)(screen.width/2), (int)(screen.height/8));
    	    EndGame.setLocation(0,(int)(7*screen.height/8)-screen.height/20);
    	    JButton NextLevel= new JButton("Next Level");
    	    NextLevel.setSize((int)(screen.width/2), (int)(screen.height/8));
    	    NextLevel.setLocation((int)(screen.width/2),(int)(7*screen.height/8)-screen.height/20);
    	    Restart.addActionListener(new ActionListener() {
    	  	  
    	        public void actionPerformed(ActionEvent e)
    	        {
    	            restart=true;
    	            Continue=true;
    	            frame.getContentPane().removeAll();
    	     		frame.setVisible(false);
    	     		//framex.setVisible(false);
    	        }
    	 });
    	    EndGame.addActionListener(new ActionListener() {
      	  	  
    	        public void actionPerformed(ActionEvent e)
    	        {
    	     		System.exit(0);
    	        }
    	 });
    	    NextLevel.addActionListener(new ActionListener() {
      	  	  
    	        public void actionPerformed(ActionEvent e)
    	        {
    	        	Stage++;
    	            restart=true;
    	            Continue=true;
    	            frame.getContentPane().removeAll();
    	     		frame.setVisible(false);
    	     		//framex.setVisible(false);
    	        }
    	 });
    		frame.add(Restart);
    		frame.add(NextLevel);
    		frame.add(EndGame);
    		frame.add(Game);
    		frame.add(Over);
    		frame.setVisible(true); 
    		gamecounter++;
     		while(!Continue);
     		//Continue=false;
    	}
    }
  }

  // Create GameBall class that has our game logic in it.
  class GameBall extends Rectangle{

	double q; //charge
	double qother;
	long date1; //initialization of the ball
	long date2; //end time of the ball
    Color ballColor; // The color of the ball

    public GameBall(){
      super(100, 20, 15, 15); // Initial position and size of the ball
      Initialize();
      ballColor=new Color(0, 0, 128); //Blue ball
    }
    
	private void Initialize() { 
		//final JFrame Instructions = new JFrame("Field Type");
		if(Stage==1){
		frame.setLayout(new GridLayout(0, 1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 	    JButton Continue = new JButton("Press to select intital conditions");
 	    
 	   Continue.addActionListener(new ActionListener() {  
           public void actionPerformed(ActionEvent e)
           {
        	   //Instructions.setVisible(false);
        	   frame.setVisible(false);
        	   frame.getContentPane().removeAll();
        	   Fieldtype();
           }
    });
 	    frame.setFont(new Font("Serif", Font.BOLD, 20));
	    frame.add(new JLabel("You have only 30 seconds to reach the objective after the ball has been launched."), JLabel.CENTER);
	    frame.add(new JLabel("An image of the next stage is shown in the background."), JLabel.CENTER);
	    frame.add(new JLabel("You will get to choose the particle type, field type, and the initial velocities of the particle."), JLabel.CENTER);
	    frame.add(new JLabel("Clicking the mouse creates a positive point charge to simulate the Coulomb force."));
	    frame.add(new JLabel("By destroying the blocks, ADDITION OF OBJECTIVE"));
	    frame.add(new JLabel("The objective of the game is to get the particle into the RED ZONE."), JLabel.CENTER);
	    
	    frame.add(Continue);
	    frame.setSize(screen.width, screen.height/2);
	    frame.setVisible(true);
		}
		else{
			frame.setLayout(new GridLayout(0, 1));
			Fieldtype();
		}
 	   
 	}
	
	public void Fieldtype(){ //Prompt the user to select a field type
    	    //final JFrame frame = new JFrame("Field Type");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	    JButton Gravity= new JButton("Gravity- The force that we experience on a day to day basis, pulls things down and keeps you from flying off of the surface of the Earth.");
    	    JButton Efield = new JButton("Electric Field- Charged particles experience a force when they are in an electric field. Electrons move against the E-field and protons move with the E-field.");
    	    JButton Bfield= new JButton("Magnetic Field- Charged particles experience a force when MOVING in a magnetic field, for simplicity in this 2D game, we will only consider the magnetic fields pointing in and out of the screen.");
    	    JButton GEfield= new JButton("Gravitational and Electric Fields!");
    	    JButton GBfield= new JButton("Gravitational and Magnetic!");
    	    JButton EBfield= new JButton("Electric and Magnetic!");  
    	    JButton Allfield= new JButton("ALL THE FIELDS!!!");
    	    
    	    Gravity.addActionListener(new ActionListener() {
  	    	  
	            public void actionPerformed(ActionEvent e)
	            {
	                //Execute when button is pressed
	            	field =1;
	                frame.getContentPane().removeAll();
	                frame.setVisible(false);
	                frame.getContentPane().removeAll();
	                Particletype();
	            }
	     });
    	    Efield.addActionListener(new ActionListener() {
  	    	  
	            public void actionPerformed(ActionEvent e)
	            {
	                //Execute when button is pressed
	            	field =2;
	                frame.getContentPane().removeAll();
	                frame.setVisible(false);
	                frame.getContentPane().removeAll();
	                Particletype();
	            }
	     });
    	    Bfield.addActionListener(new ActionListener() {
  	    	  
	            public void actionPerformed(ActionEvent e)
	            {
	                //Execute when button is pressed
	            	field =3;
	                frame.getContentPane().removeAll();
	                frame.setVisible(false);
	                frame.getContentPane().removeAll();
	                Particletype();
	            }
	     });
    	    GEfield.addActionListener(new ActionListener() {
    	    	  
	            public void actionPerformed(ActionEvent e)
	            {
	                //Execute when button is pressed
	            	field =12;
	                frame.getContentPane().removeAll();
	                frame.setVisible(false);
	                frame.getContentPane().removeAll();
	                Particletype();
	            }
	     });
    	    GBfield.addActionListener(new ActionListener() {
    	    	  
	            public void actionPerformed(ActionEvent e)
	            {
	                //Execute when button is pressed
	            	field =13;
	                frame.getContentPane().removeAll();
	                frame.setVisible(false);
	                frame.getContentPane().removeAll();
	                Particletype();
	            }
	     });
    	    EBfield.addActionListener(new ActionListener() {
  	    	  
	            public void actionPerformed(ActionEvent e)
	            {
	                //Execute when button is pressed
	            	field =23;
	                frame.getContentPane().removeAll();
	                frame.setVisible(false);
	                frame.getContentPane().removeAll();
	                Particletype();
	            }
	     });
    	    Allfield.addActionListener(new ActionListener() {
    	    	  
	            public void actionPerformed(ActionEvent e)
	            {
	            	field =123;
	                frame.getContentPane().removeAll();
	                frame.setVisible(false);
	                frame.getContentPane().removeAll();
	                Particletype();
	            }
	     });
    	    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	    frame.add(new JLabel("Select your field type!", JLabel.CENTER));
    	    frame.add(Gravity);
    	    frame.add(Efield);
    	    frame.add(Bfield);
    	    frame.add(GEfield);
    	    frame.add(GBfield);
    	    frame.add(EBfield);
    	    frame.add(Allfield);
    	    frame.setSize(screen.width, screen.height/2);
    	    frame.setVisible(true);
    	  }
          
	public void Particletype(){ //Prompt the user to select a particle type
		//final JFrame frames = new JFrame("Particle Type");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JButton Normal= new JButton("No charge- The particle will have no charge and will not be affected by electric fields or magnetic fields.");
	    JButton Electron = new JButton("Pseudo Electron- Electrons have a NEGATIVE charge of about 1.6*10^-19 coulombs.");
	    JButton Proton= new JButton("Psuedo Proton- Protons have a POSITIVE charge of about 1.6*10^-19 coulombs.");
	    Normal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
            	q=0;
                frame.getContentPane().removeAll();
                frame.setVisible(false);
                InitVel();
            }
     });
	    Electron.addActionListener(new ActionListener() {	    	  
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
            	q=-1.6E-4;
                frame.getContentPane().removeAll();
                frame.setVisible(false);
                InitVel();
            }
     });
	    Proton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
            	q=1.6E-4;
                frame.getContentPane().removeAll();
                frame.setVisible(false);
                InitVel();
            }
     });
	    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(new JLabel("Select your particle type!", JLabel.CENTER));
	    frame.add(Normal);
	    frame.add(Electron);
	    frame.add(Proton);
	    frame.setSize(screen.width, screen.height/2);
	    frame.setVisible(true);
	    
	    //while(InitialCond==1);
	}
	
	public void InitVel(){ //Prompt the user for initial velocities.
		  xVelstr = JOptionPane.showInputDialog("Initial x velocity (Max = absolute value of "+(int)(0.04*screen.width)+": ");
		  if(xVelstr != null){
		   yVelstr = JOptionPane.showInputDialog("Initial y velocity (Max= absolute value of "+(int)(0.04*screen.width)+": ");
		  	if(yVelstr !=null){
		  		InitialCond=1;
		  	    java.util.Date startime= new java.util.Date();
		  	    date1=startime.getTime();
		  	}
		  }
		  
	}
	
    // Instance methods for GameBall
    public double Forcex(double x, double y, double vx, double vy){// ask for position for future implementations
    	if(field==1){
    		E[0]=E[1]=0;B=0;
    	}
    	if(field==2){
    		g=0;B=0;
    	}
    	if(field==3){
    		g=0;E[0]=0;E[1]=0;
    	}
    	if(field==12)B=0;
    	if(field==13){
    		E[0]=E[1]=0;
    	}
    	if(field==23)g=0;
    	else ;
    	
    	if(((x-xother)*(x-xother)+(y-yother)*(y-yother))<1){
    		return q*E[0]+Bforce[0]*q+ke*q*qother*(x-xother)/1;	
    	}
    	else{
    		Bforce=	cross(vx,vy,0,0,0,B);
    	return q*E[0]+Bforce[0]*q+ke*q*qother*(x-xother)/((x-xother)*(x-xother)+(y-yother)*(y-yother));	
    	}
    }
    
    public double Forcey(double x, double y, double vx, double vy){// ask for position for future implementations
    	if(field==1){
    		E[0]=E[1]=0;B=0;
    	}
    	if(field==2){
    		g=0;B=0;
    	}
    	if(field==3){
    		g=0;E[0]=0;E[1]=0;
    	}
    	if(field==12)B=0;
    	if(field==13){
    		E[0]=E[1]=0;
    	}
    	if(field==23)g=0;
    	else ;
    	if(((x-xother)*(x-xother)+(y-yother)*(y-yother))<1){
    		return q*E[0]+Bforce[0]*q+ke*q*qother*(y-yother)/1;	
    	}
    	else{
    		Bforce=	cross(vx,vy,0,0,0,B);
    		return q*E[1]+Bforce[1]*q+g+ke*q*qother*(y-yother)/((x-xother)*(x-xother)+(y-yother)*(y-yother));
    	}
    }  
    public void move(){
    	
    	//System.out.println(Math.sqrt(xVel*xVel+yVel*yVel));
      frame.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				// Do nothing
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// Do nothing
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// Do nothing
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				qother=1.6E-4;
				xother=e.getX();
				yother=e.getY();
			}



			@Override
			public void mouseReleased(MouseEvent e) {
				//System.out.println("Released");
				qother=0;
				xother=-1000;// place the image far away when not pressed.
				yother=-1000;
			}
      });
      frame.addMouseMotionListener(new MouseMotionListener(){
    	  	@Override
		    public void mouseDragged(MouseEvent e) {
				qother=1.6E-4;
				xother=e.getX();
				yother=e.getY();
		     }

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// Nothing
			}
      });
      //Calculate new yVel (influenced by gravity)
      if(MaxEnergy==0){
    	  if(field==1){
    		  MaxEnergy=ball.height*g+0.5*(xVel*xVel+yVel*yVel); //m=1 mgh + 1/2 mv^2
    	  }
    	  if(field==2){
    		  if(q<0){
    			  MaxEnergy=Math.sqrt(E[0]*(screen.width-x)*(screen.width-x)+E[1]*(screen.height-y)*(screen.height-y)) +0.5*(xVel*xVel+yVel*yVel); // E*distance from the corner + 1/2 mv^2
    		  }
    		  if(q>0){
    			  MaxEnergy=Math.sqrt(E[0]*x*x+E[1]*y*y) +0.5*(xVel*xVel+yVel*yVel); // E*distance from the corner + 1/2 mv^2
    		  }
    	  }
    	  if(field==3){
    		  MaxEnergy=x+0.5*(xVel*xVel+yVel*yVel); // 1/2 m v^2
    	  }
    	  if(field==12){
    		  if(q<0){
    			  MaxEnergy=Math.sqrt(E[0]*(screen.width-x)*(screen.width-x)+E[1]*(screen.height-y)*(screen.height-y)) +0.5*(xVel*xVel+yVel*yVel)+ball.height*g; // distance from the corner + 1/2 mv^2
    		  }
    		  if(q>0){
    			  MaxEnergy=Math.sqrt(E[0]*x*x+E[1]*y*y) +0.5*(xVel*xVel+yVel*yVel)+ball.height*g; // distance from the corner + 1/2 mv^2
    		  }
    	  }
    	  if (field==13){
    		  MaxEnergy=ball.height*g+0.5*(xVel*xVel+yVel*yVel);
    	  }
    	  if (field ==23){
    		  if(q<0){
    			  MaxEnergy=Math.sqrt(E[0]*(screen.width-x)*(screen.width-x)+E[1]*(screen.height-y)*(screen.height-y)) +0.5*(xVel*xVel+yVel*yVel); // distance from the corner + 1/2 mv^2
    		  }
    		  if(q>0){
    			  MaxEnergy=Math.sqrt(E[0]*x*x+E[1]*y*y) +0.5*(xVel*xVel+yVel*yVel); // distance from the corner + 1/2 mv^2
    		  }
    	  }
    	  if (field ==123){
    		  if(q<0){
    			  MaxEnergy=ball.height*g+Math.sqrt(E[0]*(screen.width-x)*(screen.width-x)+E[1]*(screen.height-y)*(screen.height-y)) +0.5*(xVel*xVel+yVel*yVel); // distance from the corner + 1/2 mv^2
    		  }
    		  if(q>0){
    			  MaxEnergy=ball.height*g+Math.sqrt(E[0]*x*x+E[1]*y*y) +0.5*(xVel*xVel+yVel*yVel); // distance from the corner + 1/2 mv^2
    		  }
    	  }
      }
      Goal();
      double temp=yVel;
      yVel=yVel+Forcey(x,y,xVel,yVel)*dt; // F/m*dt
      xVel=xVel+Forcex(x,y,xVel,temp)*dt;
      // Move the ball according to the game rules.
      x+=xVel/8; // Move horizontally.
      y+=yVel/8; // Move vertically.
      // Detect edges and bounce if necessary.
      // Check for intersection with Brick, the range has to be adjusted later to be more accurate
      BoundaryCollision();
      if(brick.HP>0) Collision(brick);
      if(brick2.HP>0) Collision(brick2);
      if(brick3.HP>0) Collision(brick3);
      /*
       * Here we would insert the multiple bricks that are being used in the stage.
       * Collision(brick2);
       * Collision(brick3);
       * etc.
       */
      
      
      java.util.Date endtime= new java.util.Date();
      date2=endtime.getTime();
      
      if(date2-date1>=MaxTime*1000){
    	 losingCond=true;
      }
      time= new DecimalFormat("#.##").format(MaxTime-((date2-date1)/1000.0));
    }
    
    public void Goal(){//if the ball is in the winning position
    	if(y+0.5*ball.height>winbrick.y && y+0.5*ball.height<winbrick.y+winbrick.height && x+0.5*ball.width>winbrick.x && x+0.5*ball.width<winbrick.x+winbrick.width){
    		winningCond=true;
    	}
    }
    
    public void BoundaryCollision(){
        if (x > (bounds.width - width )){ // collision with the right wall
            xVel = -xVel*0.8; // reverse movement.
            yVel=yVel*0.95;
            x = bounds.width -  width; // Set location to screen edge.
            if(0.5*Math.sqrt(xVel*xVel+yVel*yVel)>MaxEnergy){  //Conservation of energy.
            	EnergyScale=MaxEnergy/Math.sqrt(xVel*xVel+yVel*yVel);
            	xVel=xVel*EnergyScale;
            	yVel=yVel*EnergyScale;
            }
          }
        if (y > (bounds.height - height)){ //collision with the bottom wall
            yVel = -yVel*0.8; // reverse movement.
            xVel = xVel*0.95;
            y = bounds.height - height;
            if(0.5*Math.sqrt(xVel*xVel+yVel*yVel)>MaxEnergy){
            	EnergyScale=MaxEnergy/Math.sqrt(xVel*xVel+yVel*yVel);
            	xVel=xVel*EnergyScale;
            	yVel=yVel*EnergyScale;
            }
          }
          
        if (x <= 0) { // collision with the left wall
        	xVel = -xVel; x = 1;
        	if(xVel<2)xVel=2;
        	
            if(0.5*Math.sqrt(xVel*xVel+yVel*yVel)>MaxEnergy){
            	EnergyScale=MaxEnergy/Math.sqrt(xVel*xVel+yVel*yVel);
            	xVel=xVel*EnergyScale;
            	yVel=yVel*EnergyScale;
            }
        }
        if (y <= 0) { //collision with the top wall
        	yVel = -yVel; y = 1; 
            if(0.5*Math.sqrt(xVel*xVel+yVel*yVel)>MaxEnergy){
            	EnergyScale=MaxEnergy/Math.sqrt(xVel*xVel+yVel*yVel);
            	xVel=xVel*EnergyScale;
            	yVel=yVel*EnergyScale;
            }
        }
    }
    
    public void Collision(Brick brick){//Collision with the specified brick
        //Collision with bottom wall
        if((brick.y+brick.height-ball.height<y && y<brick.y+brick.height)&&(x+0.5*ball.width<brick.x+brick.width && x+0.5*ball.width>brick.x)){//bottom
    		if(x-brick.x>0){// move down or right 
      			  //If delta x is greater than delta y, do nothing
      			  if(((brick.y+brick.height)-y>Math.min((brick.x+brick.width-x),ball.width))){}
      			  else{
      	        	yVel = -yVel*0.8; 	
      	  		    xVel = xVel*0.95;
      	  		    y=brick.y+brick.height+1;
      	  		    brick.HP=brick.HP-Math.sqrt(xVel*xVel+yVel*yVel);
      	  		    if(brick.HP>0)brick.Hit();
      			  }
      		  }
      		  else{ //move down or left
      			//If delta x is greater than delta y, do nothing
      			  if(((brick.y+brick.height-y)>Math.min((brick.x-x),ball.width))){}
      			  else{
      	        	yVel = -yVel*0.8; 	
      	  		    xVel = xVel*0.95;
      	  		    y=brick.y+brick.height+1;
      	  		    brick.HP=brick.HP-Math.sqrt(xVel*xVel+yVel*yVel);
      	  		    if(brick.HP>0)brick.Hit();
      			  }
      		  }
        	
          if(0.5*Math.sqrt(xVel*xVel+yVel*yVel)>MaxEnergy){
          	EnergyScale=MaxEnergy/Math.sqrt(xVel*xVel+yVel*yVel);
          	xVel=xVel*EnergyScale;
          	yVel=yVel*EnergyScale;
          }
        }
    	//Collision with left wall
    	if((brick.x<x+ball.width && x+ball.width<brick.x+ball.width)&&(y+0.5*ball.height<brick.y+brick.height && y>brick.y)){
    		if(brick.y>y){// Move up or left
    			//If delta y is greater than delta x, then skip, else move right
    			if(Math.min((y+ball.height-brick.y),ball.height)>(x+ball.width-(brick.x))){}
    			else{
    				xVel = -xVel*0.8;
    				yVel=yVel*0.95;
    				x=brick.x-ball.width-1;
    				brick.HP=brick.HP-Math.sqrt(xVel*xVel+yVel*yVel);
    				if(brick.HP>0)brick.Hit();
    			}
    		}
    		else{//Move down or left
    			//If delta y is greater than delta x, then skip, else move right
    			if(Math.min((y-(brick.y+brick.height)),ball.height)>(x+ball.width-(brick.x))){}
    			else{          
    				xVel = -xVel*0.8;
    				yVel=yVel*0.95;
    				x=brick.x-ball.width-1;
    				brick.HP=brick.HP-Math.sqrt(xVel*xVel+yVel*yVel);
    				if(brick.HP>0)brick.Hit();
    			}
    		}
    		if(0.5*Math.sqrt(xVel*xVel+yVel*yVel)>MaxEnergy){
          		EnergyScale=MaxEnergy/Math.sqrt(xVel*xVel+yVel*yVel);
          		xVel=xVel*EnergyScale;
          		yVel=yVel*EnergyScale;
          	}
    	}
      //Collision with right wall
        if((brick.x+brick.width-ball.width<x && x<brick.x+brick.width)&&(y+0.5*ball.height<brick.y+brick.height && y>brick.y)){//right boundary
        	if(brick.y>y){// Move up or right
        		//If delta y is greater than delta x, then skip, else move right
        		if(Math.min((y+ball.height-brick.y),ball.height)>(x-(brick.x+brick.width))){}
        		else{
        	          xVel = -xVel*0.8;
        	  		  yVel=yVel*0.95;
        	  		  x=brick.x+brick.width+1;
        	  		  brick.HP=brick.HP-Math.sqrt(xVel*xVel+yVel*yVel);
        	  		  if(brick.HP>0)brick.Hit();
        		}
        	}
        	else{//Move down or right
        		//If delta y is greater than delta x, then skip, else move right
        		if(Math.min((y-(brick.y+brick.height)),ball.height)>(x-(brick.x+brick.width))){}
        		else{
      	          xVel = -xVel*0.8;
    	  		  yVel=yVel*0.95;
    	  		  x=brick.x+brick.width+1;
    	  		  brick.HP=brick.HP-Math.sqrt(xVel*xVel+yVel*yVel);
    	  		  if(brick.HP>0)brick.Hit();
        		}
        	}
        	if(0.5*Math.sqrt(xVel*xVel+yVel*yVel)>MaxEnergy){
          	EnergyScale=MaxEnergy/Math.sqrt(xVel*xVel+yVel*yVel);
          	xVel=xVel*EnergyScale;
          	yVel=yVel*EnergyScale;
          }
        }
      //Collision with top wall
        if((brick.y<y+ball.height && y+ball.height<brick.y+2*ball.height)&&(x<brick.x+brick.width && x+0.5*ball.width>brick.x)){//top
        	if(x-brick.x>0){// move up or right 
  			  //If delta x is greater than delta y, do nothing
  			  if(((y-brick.y)>Math.min((brick.x+brick.width-x),ball.width))){}
  			  else{
  		          yVel = -yVel*0.8; 
  		  		  xVel = xVel*0.95;
  		  		  y=brick.y-ball.height-1;
  		  		  brick.HP=brick.HP-Math.sqrt(xVel*xVel+yVel*yVel);
  		  		  if(brick.HP>0)brick.Hit();
  			  }
  		  }
  		  else{
  			//If delta x is greater than delta y, do nothing
  			  if(((y-brick.y)>Math.min((brick.x-x),ball.width))){}
  			  else{
  		          yVel = -yVel*0.8; 
  		  		  xVel = xVel*0.95;
  		  		  y=brick.y-ball.height-1;
  		  		  brick.HP=brick.HP-Math.sqrt(xVel*xVel+yVel*yVel);
  		  		  if(brick.HP>0)brick.Hit();
  			  }
  		  }

          if(0.5*Math.sqrt(xVel*xVel+yVel*yVel)>MaxEnergy){
          	EnergyScale=MaxEnergy/Math.sqrt(xVel*xVel+yVel*yVel);
          	xVel=xVel*EnergyScale;
          	yVel=yVel*EnergyScale;
          }
        }
    }
    
    public double[] cross(double xo, double yo, double zo, double xf, double yf, double zf){
    	double cross[]=new double[3];
    	
        cross[0] = yo*zf-zo*yf;
        cross[1]= xf*zo-zf*xo;
        cross[2]= xo*yf-yo*xf;

    	return cross;
    }

	public void draw(Graphics g){
    // the ball draws itself in the graphics context given.
      Shape ball = new Ellipse2D.Float(x, y, width, height);
      Graphics2D ga = (Graphics2D)g;
	    URL imgURL = BallGame.class.getResource("/img/PointEfield.jpg");
	    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
    	ga.drawImage(img,xother-20,yother-20,null);
      ga.setPaint(ballColor);
      ga.draw(ball);// Draw the ball.
      ga.fill(ball);// Draw the ball.
      ga.setFont(new Font("Serif", Font.BOLD, screen.height/40));
      ga.drawString(time, screen.width-screen.width/20, screen.height/40);
    } 
  } // end of class GameBall

// Now the instance methods:
  public void paintComponent(Graphics g){
    // Get the drawing area bounds for game logic.
	bounds = g.getClipBounds();
	// Clear the drawing area.
	g.clearRect(screen.x, screen.y, screen.width, screen.height);
	if(field==1){//Set background to gravity
	    URL imgURL = BallGame.class.getResource("/img/GravityS.jpg");
	    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
	    g.drawImage(img,screen.width-screen.width/5,screen.height/40,null);
	}
	if(field==2){//Set Background to Efield
	    URL imgURL = BallGame.class.getResource("/img/EFieldS.jpg");
	    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
		g.drawImage(img,screen.width-screen.width/5,screen.height/40,null);
	}
	if(field==3){//Set background to Bfield
	    URL imgURL = BallGame.class.getResource("/img/BFieldS.jpg");
	    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
		g.drawImage(img,screen.width-screen.width/5,screen.height/40,null);
	}

    // Draw the brick.
    g.setColor(brick.getColor());
    if(brick.HP>0) g.fillRect(brick.x, brick.y, brick.width, brick.height);
    g.setColor(brick2.getColor());
    if(brick2.HP>0) g.fillRect(brick2.x, brick2.y,brick2.width,brick2.height);
    g.setColor(brick3.getColor());
    if(brick3.HP>0) g.fillRect(brick3.x, brick3.y,brick3.width,brick3.height);
    g.setColor(winbrick.getColor());
    g.fillRect(winbrick.x, winbrick.y, winbrick.width, winbrick.height);
    // Draw the ball
    ball.draw(g);
  }

  public static void main(String arg[]) throws IOException{
	  int edge;
	  while(true){
	  if(Stage==1){
			Continue=false;
			//SET UP STAGE 1************************
			//**************************************
		    java.util.Timer gameTimer = new java.util.Timer();  // Create a Timer object
		    URL imgURL = BallGame.class.getResource("/img/Stage1.jpg");
		    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
		    JFrame preview = new JFrame("Stage 1 Preview");
		    preview.setSize(1000,600);
		    ImagePanel previews = new ImagePanel(img);
		    preview.getContentPane().add(previews);
		    preview.setVisible(true);    
		    BallGame panel = new BallGame();
		    preview.setVisible(false);
		    panel.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    panel.frame.setSize(panel.screen.width, panel.screen.height);
		    edge=panel.screen.height/20;

		    panel.frame.setContentPane(panel); 
		    panel.frame.setVisible(true);
		    
		    // Set up the brick.
		    panel.brick.x = panel.screen.width/4;
		    panel.brick.setColor(0,0,200);
		    panel.brick.y = panel.screen.height/4;
		    panel.brick.width = panel.screen.width/2;//Change size of brick
		    panel.brick.height = panel.screen.height/8;//Change size of brick
		    panel.brick.HP=100;
		    
		    panel.winbrick.x=panel.screen.width-panel.screen.width/20;
		    panel.winbrick.y=panel.screen.height-panel.screen.height/20-edge;
		    panel.winbrick.width=panel.screen.height/20;
		    panel.winbrick.height=panel.screen.height/20;
		    
		    // Set up the brick.
		    panel.brick2.setColor(0,0,200);
		    panel.brick2.x = panel.screen.width/3;    
		    panel.brick2.y = panel.screen.height/4;
		    panel.brick2.width = panel.screen.width/8;//Change size of brick
		    panel.brick2.height = panel.screen.height/2;//Change size of brick
		    panel.brick2.HP=100;

		    //Empty bricks
		    panel.brick3.HP=0;
		    
		    // Set up a timer to do the gameTask regularly.
		    gameTimer.schedule(panel.gameTask, 0, 20);
			//END OF STAGE 1************************
			//***************************************/
			Continue=false;
			//SET UP STAGE 1************************
			//*************************************
		  }
	    while(!Continue);
	  if(Stage==2){
			Continue=false;
			//SET UP STAGE 2************************
			//**************************************
		    java.util.Timer gameTimer = new java.util.Timer();  // Create a Timer object
		    URL imgURL = BallGame.class.getResource("/img/Stage2.jpg");
		    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
		    JFrame preview = new JFrame("Stage 2 Preview");
		    preview.setSize(1000,600);
		    ImagePanel previews = new ImagePanel(img);

		    preview.getContentPane().add(previews);
		    preview.setVisible(true);    
		    BallGame panel = new BallGame();
		    preview.setVisible(false);
		    panel.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    panel.frame.setSize(panel.screen.width, panel.screen.height);

		    panel.frame.setContentPane(panel); 
		    panel.frame.setVisible(true);
		    edge=panel.screen.height/20;
		    // Set up the brick.
		    panel.brick.setColor(0,200,0);
		    panel.brick.x = 0;
		    //panel.brick.x =0;
		    
		    panel.brick.y = panel.screen.height/4;
		    panel.brick.width = panel.screen.width/2;//Change size of brick
		    panel.brick.height = panel.screen.height/8;//Change size of brick
		    panel.brick.HP=100;
   
		    panel.winbrick.x=0;
		    panel.winbrick.y=panel.screen.height-panel.screen.height/20-edge;
		    panel.winbrick.width=panel.screen.height/20;
		    panel.winbrick.height=panel.screen.height/20;
		    
		    // Set up the brick.
		    panel.brick2.setColor(0,200,0);
		    panel.brick2.x = panel.screen.width-panel.screen.width/8;    
		    panel.brick2.y = 0;
		    panel.brick2.width = panel.screen.width/8;//Change size of brick
		    panel.brick2.height = panel.screen.height/2;//Change size of brick
		    panel.brick2.HP=100;

		    //Empty bricks
		    panel.brick3.HP=0;
		    
		    // Set up a timer to do the gameTask regularly.
		    gameTimer.schedule(panel.gameTask, 0, 20);
			//END OF STAGE 2************************
			//**************************************
		  }
	  if(Stage==3){
			Continue=false;
			//SET UP STAGE 3************************
			//**************************************
		    java.util.Timer gameTimer = new java.util.Timer();  // Create a Timer object
		    URL imgURL = BallGame.class.getResource("/img/Stage3.jpg");
		    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
		    JFrame preview = new JFrame("Stage 3 Preview");
		    preview.setSize(1000,600);
		    ImagePanel previews = new ImagePanel(img);

		    preview.getContentPane().add(previews);
		    preview.setVisible(true);    
		    BallGame panel = new BallGame();
		    preview.setVisible(false);
		    panel.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    panel.frame.setSize(panel.screen.width, panel.screen.height);
		    edge=panel.screen.height/20;
		    panel.frame.setContentPane(panel); 
		    panel.frame.setVisible(true);
		    
		    // Set up the brick.
		    panel.brick.setColor(200,0,0);
		    panel.brick.x = panel.screen.width/4;
		    panel.brick.y = panel.screen.height/4;
		    panel.brick.width = panel.screen.width/2;//Change size of brick
		    panel.brick.height = panel.screen.height/8;//Change size of brick
		    panel.brick.HP=100;
		    
		    panel.winbrick.x=panel.screen.width/2;
		    panel.winbrick.y=panel.screen.height/2;
		    panel.winbrick.width=panel.screen.height/16;
		    panel.winbrick.height=panel.screen.height/16;
		    
		    // Set up the brick.
		    panel.brick2.setColor(200,0,0);
		    panel.brick2.x = panel.screen.width/4;    
		    panel.brick2.y = 3*panel.screen.height/8;
		    panel.brick2.width = panel.screen.width/8;//Change size of brick
		    panel.brick2.height = panel.screen.height/3;//Change size of brick
		    panel.brick2.HP=100;
		    
		    // Set up brick
		    panel.brick3.setColor(200,0,0);
		    panel.brick3.x = panel.screen.width/4;
		    panel.brick3.y = 17*panel.screen.height/24;
		    panel.brick3.width = panel.screen.width/2;//Change size of brick
		    panel.brick3.height = panel.screen.height/8;//Change size of brick
		    panel.brick3.HP=100;

		    // Set up a timer to do the gameTask regularly.
		    gameTimer.schedule(panel.gameTask, 0, 20);
			//END OF STAGE 3************************
			//**************************************
		  }
	  }
  }
}