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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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

        //canvas.drawText("1234567890abcdef",10 ,60,p);
        //Typeface face = Typeface.create(Typeface.SERIF,Typeface.NORMAL);
        //p.setTypeface(face);
        //canvas.drawText("1234567890abcdef",10 ,100,p);
        //p.setColor(Color.argb(80,127,127,127));
        g = new Game(numPiece,numPiece);
        g.genNum();
        drawBoard();

        img.setImageBitmap(bitmap);
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
        //invalidate();
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

    class ImageTouchListener implements View.OnTouchListener {
        float down_x = -1;
        float down_y = -1;
        float up_x = -1;
        float up_y = -1;

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.e(TAG, "ACTION_DOWN");
                    Log.e("Image", "onTouch " + motionEvent.getAction() + " x " + motionEvent.getRawX() + " y " + motionEvent.getRawY());
                    down_x = motionEvent.getRawX();
                    down_y = motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "ACTION_UP");
                    Log.e("Image", "onTouch " + motionEvent.getAction() + " x " + motionEvent.getRawX() + " y " + motionEvent.getRawY());
                    up_x = motionEvent.getRawX();
                    up_y = motionEvent.getRawY();

                    float dx = Math.abs(down_x - up_x);
                    float dy = Math.abs(down_y - up_y);
                    if (dx > dy) {
                        Log.e(TAG, "move hori");

                        if (down_x > up_x) {
                            //move_leftright(3);
                            g.move(g.LEFT);
                        } else {
                            //move_leftright(2);
                            g.move(g.RIGHT);
                        }
                    } else {
                        Log.e(TAG, "move v");

                        if (down_y > up_y) {
                            //move_updown(1);
                            g.move(g.UP);
                        } else {
                            //move_updown(0);
                            g.move(g.DOWN);
                        }
                    }
                    if(g.canMove() == false){
                        Toast.makeText(MainActivity.this,"Game Over",Toast.LENGTH_LONG).show();
                        imgBtRestart.setVisibility(View.VISIBLE);
                        txtMsg.setVisibility(View.VISIBLE);
                    }

                    g.genNum();
                    drawBoard();
                    break;
            }

            return true;
        }
    }
}
