package com.blueberry.command;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawCanvas extends SurfaceView implements SurfaceHolder.Callback{

	public boolean isDrawing ,isRunning;//��ʾ�Ƿ���Ի��ƣ������߳��Ƿ��������
	
	private Bitmap mBitmap;//���Ƶ���λͼ����
	private DrawInvoker mInvoker;//���������������
	private DrawThread mThread;//�����߳�
	
	public DrawCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInvoker = new DrawInvoker();
		mThread = new DrawThread();
		getHolder().addCallback(this);
	}

	/**
	 * ����һ������·��
	 * @param path
	 */
	public void add(DrawPath path){
		mInvoker.add(path);
	}
	
	/**
	 * ������һ�������Ļ���
	 */
	public void redo(){
		isDrawing  = true;
		mInvoker.redo();
	}
	
	/**
	 * ������һ���Ļ���
	 */
	public void undo(){
		isDrawing = true;
		mInvoker.undo();
	}
	
	/**
	 * �Ƿ��������
	 * @return
	 */
	public boolean canUndo(){
		return mInvoker.canUndo();
	}
	
	/**
	 * �Ƿ��������
	 * @return
	 */
	public boolean canRedo(){
		return mInvoker.canUndo();
	}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isRunning  = true;
		mThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		isRunning = false;
		while(retry){
			try {
				mThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class DrawThread extends Thread{
		
		@Override
		public void run() {
			Canvas canvas =null;
			while(isRunning){
				if(isDrawing){
					try{
					canvas = getHolder().lockCanvas(null);
					if(mBitmap==null){
						mBitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);
					}
					Canvas c = new Canvas(mBitmap);
					c.drawColor(0,PorterDuff.Mode.CLEAR);
					canvas.drawColor(0,PorterDuff.Mode.CLEAR);
					mInvoker.execute(c);
					canvas.drawBitmap(mBitmap, 0, 0,null);
					}finally{
						getHolder().unlockCanvasAndPost(canvas);
					}
					isDrawing = false;
				}
			}
		}
	}

}