/**
 * Created by kylin on 15/2/22.
 */
package com.example.kylin.g2048;

import android.util.Log;

import java.util.Scanner;
import java.util.Random;

class Game{
    final int UP = 1;
    public final int DOWN= 2;
    public final int LEFT = 3;
    public final int RIGHT = 4;

    int[][] map;
    int gameH;
    int gameW;
    boolean isMove = false;
    public Game(int w,int h){
        gameH = h;
        gameW = w;
        map = new int[gameH][gameW];
        isMove = true;
    }
    public int[][] getMap(){
        return map;
    }
    void move(int direction){
        int i=0;
        int j=0;
        isMove = false;
        //System.out.println("direction " + direction);
        Log.e("Debug","direction " + direction);
        switch(direction){
            case LEFT:
                i =0;
                for(int[] row : map){
                    int[] r = filter(row);

                    merge(r);
                    r = filter(r);
                    isMove |= !cmpArr(r,row);
                    copy_hor(i,r);
                    i++;
                }
                break;
            case RIGHT:
                i =0;
                for(int[] row : map){
                    reverse(row);
                    int[] r = filter(row);

                    merge(r);
                    r = filter(r);
                    isMove |= !cmpArr(r,row);
                    reverse(r);
                    copy_hor(i,r);
                    i++;
                }
                break;
            case UP:{
                int[] row = new int[4];
                for(i=0;i<4;i++){
                    for(j=0;j<4;j++) row[j] = map[j][i]; // copy col i to row
                    dump(row);
                    int[] r = filter(row);
                    merge(r);
                    r = filter(r);
                    isMove |= !cmpArr(r,row);
                    for(j=0;j<4;j++)  map[j][i] = r[j] ; // copy row to col
                }
            }
            break;
            case DOWN:{
                int[] row = new int[4];
                for(i=0;i<4;i++){
                    for(j=0;j<4;j++) row[j] = map[j][i]; // copy col i to row
                    reverse(row);
                    dump(row);
                    int[] r = filter(row);
                    merge(r);
                    r = filter(r);
                    isMove |= !cmpArr(r,row);
                    reverse(r);
                    for(j=0;j<4;j++)  map[j][i] = r[j] ; // copy row to col
                }
            }
            break;
            default:
                System.out.println("no no");
                break;
        }
        Log.e("Game","isMove " + isMove);
    }
    boolean cmpArr(int[] a,int[] b){
        int len = a.length ;
        if(a.length != b.length) return false;

        for(int i = 0;i < len ;i++){
            if(a[i] != b[i])
                return false;
        }
        return true;
    }
    int[] filter(int[] arr){
        int[] retArr = new int[arr.length];
        int j=0;
        for(int i : arr){
            if(i>0) retArr[j++] = i;
        }
        return retArr;
    }
    void merge(int[] arr){
        int len = arr.length;
        int i,j;
        for( i = 0 ; i < len-1; i ++){
            if(arr[i] > 0){
                if(arr[i] == arr[i+1]){
                    arr[i] += arr[i+1];
                    arr[i+1] = 0;
                    break;
                }

            }
        }
    }
    void copy_hor(int idx,int[] row){
        for(int i = 0;i<4;i++)
            map[idx][i] = row[i];
    }
    void reverse(int[] arr){
        int len = arr.length;
        int tmp;
        int end = len -1;
        for(int i=0;i<len/2;i++){
            tmp = arr[i];
            arr[i] = arr[end - i];
            arr[end-i] = tmp;
        }
    }
    int[] genNextValue(){
        Random  ran = new Random();
        int n,x,y;
        int size = gameH * gameW;
        int[] ret = {-1,-1};
        boolean hasZero = false;
        for(int i = 0;i<gameH && hasZero == false;i++){
            for(int j = 0; j < gameW && hasZero == false ;j++){
                if(map[i][j] == 0) hasZero=true;
            }
        }
        if(hasZero == false) return ret;
        while(true){
            n = ran.nextInt(size);
            x = n % gameW;
            y = n / gameH;
            if(map[y][x] == 0){
                ret[0]=x;
                ret[1]=y;
                return ret;
            }
        }
    }
    public void genNum(){
        if(isMove == true) {
            int[] point = genNextValue();
            int x = point[0];
            int y = point[1];
            Random rand = new Random();

            if(x == -1) return;

            if(rand.nextInt(100) < 80)
                map[y][x] = 2; // ......
            else
                map[y][x] = 4;
        }
    }
    void finalcheck(){
        map =new int[][] {
                {8,2,8,4},
                {2,8,2,8},
                {8,2,8,4},
                {2,8,8,8}
        };
        System.out.println("check " + canMove());
    }
    boolean canMove(){
        int i,j;
        for(i = 0 ; i < 4; i++){
            for(j = 0 ; j < 4; j++){
                if(map[i][j] == 0) return true;
                if(j<3 && map[i][j] == map[i][j+1]) return true;
                if(i<3 && map[i][j] == map[i+1][j]) return true;
            }
        }


        return false;

    }
    int[] move(int[] v){
        int[] resArr = new int[4];
        for(int i = 0;i<4;i++){
            resArr[i] = v[i] + 1;
        }
        return resArr;
    }
    void dump(int[] arr){
        String tmp = "";
        System.out.println("** dump start 1d **");
        for(int i = 0;i < 4;i ++){
            tmp += arr[i] + " ";
        }

        System.out.println(tmp);
    }
    public void print(){
        dump(map);
    }
    void dump(int[][] arr){
        String tmp = "";
        System.out.println("** dump start **");
        for(int i = 0;i < 4;i ++){
            tmp = "";
            for(int j = 0;j < 4;j ++){
                tmp += arr[i][j] + " ";
                //arr[i][j] = i*4+j;
            }
            System.out.println(tmp);
        }
    }
    void test(){
/*        map[0][1] = 2;
        map[1][1] = 2;
        map[1][2] = 2;
        map[1][3] = 2;
        dump(map);
        move(DOWN);
        dump(map);
*/
        for(int i =0;i<16;i++){
            genNum();
            dump(map);
        }

    }
    public void start(){

        System.out.println("start");
        //random
        //get inpug
        //move , loop
    }
}
