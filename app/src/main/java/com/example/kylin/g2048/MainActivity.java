package com.example.kylin.g2048;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.view.GestureDetectorCompat;

public class MainActivity extends ActionBarActivity {
    ImageView img;
    Bitmap bitmap;
    Canvas canvas;

    int imgWidth = 600;
    int imgHeight = imgWidth;
    int numPiece = 4;
    int gap = 10;
    float gridWidth;
    float numSize = 50;
    Game g;
    private String TAG = "Game";

    ImageButton imgBtRestart;
    TextView txtMsg;
    GestureDetectorCompat gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imgWidth = size.x - 10;
        imgHeight = imgWidth;
        numSize = imgHeight/8 -20;
        imgBtRestart = (ImageButton)findViewById(R.id.imgbtRestart);
        txtMsg = (TextView)findViewById(R.id.txtMsg);
        img = (ImageView)findViewById(R.id.imageView);

        bitmap = Bitmap.createBitmap(imgWidth,imgHeight, Bitmap.Config.ARGB_4444);
        canvas = new Canvas(bitmap);

        gridWidth = (imgWidth - gap * (numPiece +1))/ (float)numPiece;
        Paint p = new Paint();
        p.setTextSize(50);

        g = new Game(numPiece,numPiece);
        g.genNum();
        g.genNum();
        drawBoard();

        img.setImageBitmap(bitmap);

        gestureDetector = new GestureDetectorCompat(this,new GestureListener());
        img.setOnTouchListener(new ImageTouchListener());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imgBtRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g = new Game(numPiece,numPiece);
                g.genNum();
                drawBoard();
                imgBtRestart.setVisibility(View.GONE);
                txtMsg.setVisibility(View.GONE);
            }
        });
        imgBtRestart.setVisibility(View.GONE);
        txtMsg.setVisibility(View.GONE);
        Log.e(TAG,"onCreate end");
    }
    void drawBoard(){
        int[][] map = g.getMap();
        Log.e(TAG,"drawBoard");
        clearImage();
        drawBack();
        //draw map
        for(int i = 0;i<numPiece;i++){
            for(int j = 0;j<numPiece;j++){
                if(map[i][j] > 0)
                    drawNum(j,i,map[i][j]);
            }
        }
        img.invalidate();
    }
    void clearImage(){
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawRect(0,0,(int)imgHeight,(int)imgWidth,p);
    }
    void drawBack(){
        Paint p = new Paint();
        float w = (imgWidth - gap * (numPiece +1))/ (float)numPiece;
        Log.e("game","w " + w);
        p.setColor(Color.argb(80, 127, 127, 127));

        for(float i =0;i<imgHeight;i=i+gap+w) {
            for(float j = 0;j<imgWidth;j=j+gap+w) {
                canvas.drawRect(j + gap, i + gap, j + gap + w, i + gap + w, p);
                //Log.e("game"," " + j);
            }
        }
    }
    int[] gridColor = { Color.GRAY  ,0xff8e8eff     ,Color.CYAN     ,0xffff8080,
                        Color.GREEN ,0xffff8040   ,Color.MAGENTA  ,Color.RED,
                        Color.YELLOW,0xff345678     ,0xff667788     ,0xaabbccdd,
                        0xffbbeecc, 0xff0022ff      ,0xff11ff00     ,0xff77dd00};
    void drawNum(int x,int y,int value){
        Paint p = new Paint();
        p.setTextSize(numSize);
        int idx = (int)(Math.log(value)/Math.log(2));
        p.setColor(gridColor[idx]);
        canvas.drawRect(gap*(x+1) + x * gridWidth,gap*(y+1)+gridWidth*y,
                gap*(x+1) + x * gridWidth +  gridWidth,gap*(y+1)+gridWidth*y + gridWidth ,p);
        p.setColor(Color.BLACK);

        Typeface face = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);

        p.setTypeface(face);
        canvas.drawText(Integer.toString(value),10 + (gridWidth +gap)* x + 40 ,10 + (gridWidth+gap)*(y+1) - 60,p);
        //p.setColor(Color.BLACK);
        //canvas.drawRect(gap*(x+1) + gridWidth*x,gap*(y+1) + gridWidth*y,(gap + gridWidth)*(x+1),(gap+gridWidth)*(y+1),p);
        //canvas.drawText("2",10 + 87/2 -10 ,10 + 97,p);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            g.move(g.RIGHT);
                        } else {
                            g.move(g.LEFT);
                        }
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        g.move(g.DOWN);
                    } else {
                        g.move(g.UP);
                    }
                }
                if(g.canMove() == false){
                    Toast.makeText(MainActivity.this,"Game Over",Toast.LENGTH_LONG).show();
                    imgBtRestart.setVisibility(View.VISIBLE);
                    txtMsg.setVisibility(View.VISIBLE);
                }
                g.genNum();
                drawBoard();
                result = true;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
    class ImageTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }
}
