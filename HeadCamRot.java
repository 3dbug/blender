package vrserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import static java.lang.Math.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Move extends JFrame {

    static int       Width       = 1024;
    static int       Height      = 800;
    
    static int 		CX = Width / 2;
    static int 		CY = Height / 2;

    float camRot = 0;
    float bodyRot = 0;

    private DrawPanel drawPanel;

    public Move() {
        setLayout( new BorderLayout() );
        drawPanel = new DrawPanel();
        add( drawPanel, BorderLayout.CENTER );
    }

    public static void main( String[] args) {
        Move frame = new Move();
        frame.setSize( Width, Height );
        frame.setTitle( "HeadCamRot" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
        frame.setFocusable( true );
        Listener listener = frame.new Listener();
        frame.addKeyListener( listener );
    }

    class Listener implements KeyListener {
        @Override
        public void keyPressed( KeyEvent e) {
            int key = e.getKeyCode();
            if ( key == KeyEvent.VK_KP_LEFT || key == KeyEvent.VK_LEFT ) {
                camRot -= 5;
            }
            else if ( key == KeyEvent.VK_KP_RIGHT || key == KeyEvent.VK_RIGHT ) {
                camRot += 5;
            }
            else if ( key == KeyEvent.VK_ESCAPE ) {
                System.exit( 0 );
            }
            repaint();
        }
        @Override
        public void keyTyped( KeyEvent e) {
        }
        @Override
        public void keyReleased( KeyEvent e) {
        }
    }

    class DrawPanel extends JPanel {

        private static final int HeadNaxRot = 80;

		protected void paintComponent( Graphics g) {
            super.paintComponent( g );
            g.setColor( Color.black );
            g.fillRect( 0, 0, Width, Height );
            g.setColor( Color.white );

            if ( camRot > 180) { 
            	camRot -= 360;
            	bodyRot -= 360;
            }
            if ( camRot < -180) { 
            	camRot += 360;
            	bodyRot += 360;
            }
            int radius = 200;
            double camangle = camRot / 180 * Math.PI;
            
            int x = (int) ( CX + ( cos( camangle ) * radius ) );
            int y = (int) ( CY + ( sin( camangle ) * radius ) );

            float delta = camRot - bodyRot;
            if ( abs( delta ) > HeadNaxRot ) {
                if ( delta > HeadNaxRot ) {
               		bodyRot = camRot - HeadNaxRot;
                }
                if ( delta < HeadNaxRot ) {
                    bodyRot = camRot + HeadNaxRot ;
                }
            }
            g.setColor( Color.gray);
            g.fillArc(CX -  radius/2, CY -  radius/2,
         		   radius, radius, 
         		  (int) (bodyRot - HeadNaxRot) , (int) HeadNaxRot*2 );
            g.setColor( Color.white);
            g.drawLine(CX, CY,x, Height - y );
            g.drawString( "cam=" + (int) camRot, x, Height - y );
            g.drawString( "delta=" + (int) delta, 10, CY);

            double bodyangle = bodyRot / 180 * Math.PI;
            
            int xb = (int) ( CX + ( cos( bodyangle ) * radius ) );
            int yb = (int) ( CY + ( sin( bodyangle ) * radius ) );

            g.setColor( Color.orange );
            g.drawLine( CX , CY,  xb, Height - yb );
            g.drawString( "body=" + (int) bodyRot, xb, Height - yb );
        }
    }
}
