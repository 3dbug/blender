package vrserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PrimeNumberStar extends JFrame {

    static boolean[] primes      = new boolean[ 10000 ];

    static double    angleStep   = Math.PI / 10/3.0;

    static double    radiusStep  = .025;

    static int       Width       = 1024;

    static int       Height      = 800;

    boolean          onlyPrimes  = false;

    DrawPanel        drawPanel;

    public boolean   printAngles = false;

    static {
        Arrays.fill( primes, true );
        primes[0] = primes[1] = false;
        for ( int i = 2 ; i < primes.length ; i++ ) {
            if ( primes[i] ) {
                for ( int j = 2 ; i * j < primes.length ; j++ ) {
                    primes[i * j] = false;
                }
            }
        }
    }

    public PrimeNumberStar() {
        setLayout( new BorderLayout() );
        drawPanel = new DrawPanel();
        add( drawPanel, BorderLayout.CENTER );
    }

    public static void main( String[] args) {
        PrimeNumberStar frame = new PrimeNumberStar();
        frame.setSize( Width, Height );
        frame.setTitle( "Prime" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
        frame.setFocusable( true );
        Listener listener = frame.new Listener();
        frame.addMouseWheelListener( listener );
        frame.addKeyListener( listener );
    }

    class Listener implements KeyListener, MouseWheelListener {
        @Override
        public void keyPressed( KeyEvent e) {
            int key = e.getKeyCode();
            if ( key == KeyEvent.VK_KP_LEFT || key == KeyEvent.VK_LEFT ) {
                angleStep -= .00001;
            }
            else if ( key == KeyEvent.VK_KP_RIGHT || key == KeyEvent.VK_RIGHT ) {
                angleStep += .00001;
            }
            else if ( key == KeyEvent.VK_KP_UP || key == KeyEvent.VK_UP ) {
                radiusStep -= .0001;
            }
            else if ( key == KeyEvent.VK_KP_DOWN || key == KeyEvent.VK_DOWN ) {
                radiusStep += .0001;
            }
            else if ( key == KeyEvent.VK_SPACE ) {
                onlyPrimes = !onlyPrimes;
            }
            else if ( e.getKeyChar() == 'n' ) {
                printAngles = !printAngles;
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

        @Override
        public void mouseWheelMoved( MouseWheelEvent mwe) {
            radiusStep += mwe.getPreciseWheelRotation() / 50;
            repaint();
        }
    }

    class DrawPanel extends JPanel {

        protected void paintComponent( Graphics g) {
            super.paintComponent( g );
            HashSet<Integer> seenAngles = new HashSet<Integer>();
            g.setColor( Color.red );
            g.drawString( "0", 200, 115 );
            double angle = 0.0;
            double radius = 0.0;
            g.setColor( Color.black );
            g.fillRect( 0, 0, Width, Height );
            g.setColor( Color.white );
            for ( int i = 0 ; i < primes.length ; i++ ) {
                if ( !onlyPrimes || primes[i] ) {
                    int x = (int) ( Width / 2.0 + ( Math.cos( angle ) * radius ) );
                    int y = (int) ( Height / 2.0 + ( Math.sin( angle ) * radius ) );

                    g.setColor( primes[i] ? Color.white : Color.orange );
                    g.drawLine( x, Height - y, x, Height - y );
                    if ( printAngles && primes[i] && radius > 200 && radius < 215 ) {
                        int a = (int) ( ( angle / Math.PI * 180.0 ) % 360 + .5);
                        if ( !seenAngles.contains( a ) ) {
                            g.drawString( "a=" + (int) a, x, y );
                            seenAngles.add( a );
                        }
                    }
                }
                angle += angleStep;
                double step = radiusStep;
                radius += step;
            }
            g.drawString( "angle increment=" + angleStep, 10, 10 );
            g.drawString( "radius=" + radiusStep, 10, 30 );
            if ( printAngles ) {
                List<Integer> sorted = new ArrayList<Integer>();
                sorted.addAll( seenAngles );
                Collections.sort( sorted );
                int prev = 0;
                for ( Integer a : sorted ) {
                    System.out.println( "a=" + a + " dist=" + ( a - prev ) );
                    prev = a;
                }
                System.out.println( "----" );
            }
        }
    }
}
