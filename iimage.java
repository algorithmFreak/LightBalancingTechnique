import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.*;
class iimage extends Frame implements ActionListener
{
    Image img01;
    Button bopen;
    Button bapply;
    Button bhist;
    Button bblur;
    Button binvert;
    Button bclose;
    int []iin;
    int iout[];
    int width,height;
	 //Main
    public static void main(String ar[])
    {
    	
       	iimage img=new iimage("IMAGE");
        img.setSize(640,400);
        img.setVisible(true);        
    }
    
    //Button
    iimage(String s)
    {
    	super(s);    
        setLayout(new FlowLayout());
        Button bopen=new Button("OPEN");
        Button bapply=new Button("APPLY");
		Button bhist=new Button("HISTOGRAM");
		Button bblur=new Button("BLUR");
		Button binvert=new Button("INVERT");
		Button bclose=new Button("CLOSE");
        bopen.addActionListener(this);
        bapply.addActionListener(this);
        binvert.addActionListener(this);
        bhist.addActionListener(this);
        bblur.addActionListener(this);
        bclose.addActionListener(this);
        add(bopen);add(bapply);add(binvert);add(bblur);add(bhist);
        add(bclose);
        addWindowListener(new WinAdapter());
    }


    //Close
    class WinAdapter extends WindowAdapter
    {
        public void windowClosing(WindowEvent we){System.exit(0);}
    }
    
    //GET THE FILE TO BE OPENED FROM USER
    String openfile()
    {
    	   
        FileDialog fd=new FileDialog(new Frame(),"File Select");
        fd.show();
        String fullpath=fd.getDirectory()+fd.getFile();
        fd.dispose();
        return fullpath;
    }
    
    
    public void update(Graphics g)
    {
          paint(g);
    }
     
    public  void paint(Graphics g)
    {
       			g.drawImage(img01,10,60,this); 
    }
 
    public void actionPerformed(ActionEvent ae)
    {
        
        //ON PRESSING open BUTTON
		 if(ae.getActionCommand()=="OPEN")
		 {
				         try
				            {
				                String Filename = openfile();
				                if(Filename!=null)
				                {
				                	    img01 = getToolkit().getImage(Filename);
			       						 PixelGrabber pg=new PixelGrabber(img01,0,0,-1,-1,true);
									     try
					    				 {
		        				  			if(pg.grabPixels())
		            			  		    {
				 		        			iin=(int[]) pg.getPixels();
			               					width=pg.getWidth();
			    							height=pg.getHeight();
			    							iout= new int[width*height];
			                      }
			    	       	 }
			            	   catch(InterruptedException ie){}
    	
				               	}
				               
				            }  	               	           
				            catch(Exception ex){}
				            repaint();
          }
        
        //ON PRESSING apply BUTTON
       	 if(ae.getActionCommand()=="APPLY")
         {
           
           new oimage("OUTPUT",img01,iin,iout,width,height);
           
        } 
        	//ON PRESSING CLOSE BUTTON
         if(ae.getActionCommand()=="CLOSE")
         {
           
           System.exit(0);
        }  
        	
        	//ON PRESSING CLOSE BUTTON
         if(ae.getActionCommand()=="HISTOGRAM")
         {
           
           new HistoGram(img01,iin,width,height);
        }
        if(ae.getActionCommand()=="INVERT")
         {
           
           new Invert(img01,iin,iout,width,height);
        }
         if(ae.getActionCommand()=="BLUR")
         {
           
           new Blur(img01,iin,iout,width,height);
        } 	     
    } 
}


//CLASS FOR ALGORITHM IMPLEMENTATION
class oimage extends Frame implements ActionListener
{
	Image img02;
	int width,height;   
	oimage(String s,Image i,int []iin,int []iout,int width,int height)
	{
		super(s);
		setSize(300,300);
        setVisible(true);
        this.width=width;
        this.height=height;
        setLayout(new FlowLayout());
        Button bs=new Button("SAVE");
        add(bs);
    	bs.addActionListener(this);
     	if(i!=null)
     	{
     		algo(width,height,iin,iout);
	        img02=createImage(new MemoryImageSource(width,height,iout,0,width));
	     }
    	addWindowListener(
        	new WindowAdapter()
		    {
    	    public void windowClosing(WindowEvent we)
    	    {
    	    		setVisible(false);
    	    }
    		});
    	repaint();
//    	new HistoGram(img02);
    }
    //Close
  
  
  public void actionPerformed(ActionEvent ae)
   			 {
   
    			 if(ae.getActionCommand()=="SAVE")
    			 {
            			if(img02!=null)
            			{
                			try
                			{
			                    String Filename = openfile();
			                    if(Filename!=null)
			                    {
			                        FileOutputStream fo = new FileOutputStream(Filename);
			                        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			                        Graphics gc = bi.getGraphics();
			                        gc.drawImage(img02,0,0,this);
			                        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fo);
			                        encoder.encode(bi);
			                        fo.close();
			                    }
                			}
            			    catch(Exception ex){}
        	    		}
        		}
   			 }
 
    
    public void update(Graphics g)
    {
          paint(g);
    }
     
    public  void paint(Graphics g)
    {
       			g.drawImage(img02,10,0,this); 
    }
 
     //GET THE FILE TO BE OPENED FROM USER
    String openfile()
    {
    	   
        FileDialog fd=new FileDialog(new Frame(),"File Select");
        fd.show();
        String fullpath=fd.getDirectory()+fd.getFile();
        fd.dispose();
        return fullpath;
    }
   
    //LIGHT BALANCING ADAPTIVE PROCESSING ON PIXELS
     void algo(int w,int h,int []iiin,int []iiout)
    {
    	int cnt=0;
    	double  avgr=0.0,avgb=0.0,avgg=0.0;
    	int BC=255;
    	double AGCG=1.0;
    	double AGCR=1.0;
    	double AGCB=1.0;
    	int tmp=0;
    	int i=0;
    	int j=0;
    	int C=20;
    	int POSITION=0;
    	int no=(int)((w/C)*h);
    	double	 []SLR= new double[no];
    	double []SLG= new double[no];
    	double []SLB= new double [no];
    	int []R=new int[w*h];
    	int []G=new int[w*h];
    	int []B=new int[w*h];
    	double []R1=new double[w*h];
    	double []G1=new double[w*h];
    	double []B1=new double[w*h];
    	    	
    
   
    	//SAMPLING GAIN ESTIMATION
    	
    	for(i=0;i<no;i++)
    	{		
      		for(j=0;j<C;j++)
    		{	
    				POSITION=(i*C)+j;
    				if(POSITION<iiout.length)
    				{
	    					tmp=iiin[POSITION];
	    					R[POSITION]=0xff&(tmp>>16);
	    					G[POSITION]=0xff&(tmp>>8);
	    					B[POSITION]=0xff&(tmp);
	    					if(R[POSITION]>100&&G[POSITION]>100&&B[POSITION]>100)
	    					{
		    						cnt++;
		    						avgr=avgr+R[POSITION];
		    						avgg=avgg+G[POSITION];
		      						avgb=avgb+B[POSITION];
	     					}
	     					
    				}
    			//iiout[POSITION]=((255<<24)|(R[POSITION]<<16)|(G[POSITION]<<8)|(B[POSITION]));	
    		}
    	
    		
    		if(cnt!=0)
    		{
				avgr=(avgr/cnt);
				avgg=(avgg/cnt);
				avgb=(avgb/cnt);
    		}
			SLR[i]=avgr;
         		SLB[i]=avgb;
    		SLG[i]=avgg;
    		cnt=0;
    		avgr=0.0;
    		avgb=0.0;
    		avgg=0.0;
    	} 	 
 		POSITION=0;
 	
 	
 	//INTERPOLATION FOR BACKGROUND
		 for(i=0;i<no-1;i++)		//FOR EACH SECTION
    	 {		
    	 			
    			for(j=0;j<C;j++)		//FOR EACH PIXEL
    			{
    					
    					POSITION=(i*C)+j;
    		    		if(R[POSITION]>1&&G[POSITION]>1&&B[POSITION]>1) 
    					{
    						R1[POSITION]=((SLR[i]*(C-j)+SLR[i+1]*j)/C);
    						G1[POSITION]=((SLG[i]*(C-j)+SLG[i+1]*j)/C);
    						B1[POSITION]=((SLB[i]*(C-j)+SLB[i+1]*j)/C);
    					}
    				
    				  
    					//ADAPTIVE GAIN PROCESSING FOR EACH BACKGROUND PIXEL
    					
    					if(R1[POSITION]!=0.0&&B1[POSITION]!=0.0&&G1[POSITION]!=0.0)
		    				 {
		    				 	AGCR=R1[POSITION]/BC;
		    				 	AGCB=B1[POSITION]/BC;
		    				 	AGCG=G1[POSITION]/BC;
		    				 	if(R[POSITION]>120&&B[POSITION]>120&&G[POSITION]>120)
		    					{
		    				 				R[POSITION]=255;
		     				 				G[POSITION]=255;       //MAKE BACK BRIGHT
		    				 				B[POSITION]=255;
		    					}
		    					else if (R[POSITION]<20&&B[POSITION]<20&&G[POSITION]<20)
		    					{	
		    							R[POSITION]=10;
		    							B[POSITION]=10;			//MAKE CHAR DARK
		    							G[POSITION]=10;
		    					}
		    					else
		    			 		{
		    				 			R[POSITION]=(int)(R[POSITION]*AGCR);
		    							B[POSITION]=(int)(B[POSITION]*AGCB);			//MAKE LIGHT BALANCE
		    							G[POSITION]=(int)(G[POSITION]*AGCG);
		    					}
		    				
		    				}	
    					iiout[POSITION]=((255<<24)|(R[POSITION]<<16)|(G[POSITION]<<8)|(B[POSITION]));	
    					
    			} 	 
    	}
    }
   
    
}

//CLASS FOR HISTOGRAM IMPLEMENTATION
class HistoGram extends Frame 
{
	int hist[] = new int[256];
	int max_hist = 0,w,h;
	HistoGram(Image img,int []iin,int width,int height) 
		{
			super("Histograb of image");
			h=height;
			w=width;
			addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					setVisible(false);
				}
			});
			for (int i=0; i<iin.length; i++) 
			{
				int p = iin[i];
				int r = 0xff & (p >> 16);
				int g = 0xff & (p >> 8);
				int b = 0xff & (p);
				int y = (int) (.33 * r + .56 * g + .11 * b);
				hist[y]++;
			}
			for (int i=0; i<256; i++) 
			{
				if (hist[i] > max_hist)
				max_hist = hist[i];
			}
			setSize(400,400);
			setResizable(false);
			setVisible(true);
			repaint();
		

			
	}
	public void update(Graphics g) {paint(g);}
	public void paint(Graphics g) 
	{
		int x = (w - 256) / 2;
		int lasty = h - h * hist[0] / max_hist;
		for (int i=0; i<256; i++, x++)
		 {
			int y = h - h * hist[i] / max_hist;
			g.setColor(new Color(i, i, i));
			g.fillRect(x, y, 1, h);
			g.setColor(Color.red);
			g.drawLine(x-1,lasty,x,y);
			lasty = y;
		}
		
	}
}
//OR INVERTING GIVEN IMAGE
class Invert extends Frame
{
		Image img02;
		Invert(Image img,int []iin,int[]iout,int width,int height)
		{
			setSize(300,300);
	        setVisible(true);
	     	if(img!=null)
	     	{
	     		for (int i=0;i<width*height; i++) 
				{
					int rgb=iin[i];
					int r = 0xff - (rgb >> 16) & 0xff;
					int g = 0xff - (rgb >> 8) & 0xff;
					int b = 0xff - rgb & 0xff;
					iout[i]=(0xff000000 | r << 16 | g << 8 | b);
				}
				img02=createImage(new MemoryImageSource(width,height,iout,0,width));
	     	}
        	addWindowListener(
        	new WindowAdapter()
		    {
    	    public void windowClosing(WindowEvent we)
    	    {
    	    		setVisible(false);
    	    }
    		});
    		repaint();
	    }
    public void update(Graphics g)
    {
          paint(g);
    }
     
    public  void paint(Graphics g)
    {
       			g.drawImage(img02,10,0,this); 
    }
}

//OR BLURRING GIVEN IMAGE
class Blur extends Frame
{
		Image img02;
		Blur(Image img,int []iin,int[]iout,int width,int height)
		{
			setSize(300,300);
	        setVisible(true);
	     	if(img!=null)
	     	{
	     		for(int y=1; y<height-1; y++)
	     	    {
					for(int x=1; x<width-1; x++) 
					{
						int rs = 0;
						int gs = 0;
						int bs = 0;
						for(int k=-1; k<=1; k++)
					    {
							for(int j=-1; j<=1; j++)
						 	{
							int rgb = iin[(y+k)*width+x+j];
							int r = (rgb >> 16) & 0xff;
							int g = (rgb >> 8) & 0xff;
							int b = rgb & 0xff;
							rs += r;
							gs += g;
							bs += b;
						}
					}
				rs /= 9;
				gs /= 9;
				bs /= 9;
				iout[y*width+x] = (0xff000000 |	rs << 16 | gs << 8 | bs);
			}
		}
	}
		img02=createImage(new MemoryImageSource(width,height,iout,0,width));
        	addWindowListener(
        	new WindowAdapter()
		    {
    	    public void windowClosing(WindowEvent we)
    	    {
    	    		setVisible(false);
    	    }
    		});
    		repaint();
	  }
    public void update(Graphics g)
    {
          paint(g);
    }
     
    public  void paint(Graphics g)
    {
       			g.drawImage(img02,10,0,this); 
    }
}

