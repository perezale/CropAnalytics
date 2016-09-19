package net.pladema;

import static org.bytedeco.javacpp.opencv_highgui.WINDOW_AUTOSIZE;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.namedWindow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_core.split;


import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
//import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

public class OpenCV3Test {
	
	public static String grabFrame(String filename){
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filename);    	
		String output= "";
		
	     try {
	         //Start grabber to capture video
	         grabber.start();
	         grabber.setFrameRate(29);	       
	         
	         OpenCVFrameConverter.ToMat conv = new OpenCVFrameConverter.ToMat();	         
	         
	         while(true){
	        	 Mat img = conv.convert(grabber.grab());	         
		         img = detectObjects(img);
	        	 
		         if(img!=null){		        	 
		             imshow("Result" , img);
		             if(handleWaitKey()) break;		                     	   		        	 
		         }else{
		        	 break;
		         }
		         img.release();
		         
	         }
	         
	         
	     }catch (Exception e) {      
	     }
	     return output;
	}
	
	private static boolean handleWaitKey() {
		int key = waitKey(30);
		if (key == 27 )  // Escape
			return true;
		if (key == 32) // Barra espacio
		{
			while(waitKey()!= 32);
			return false;
		}
		return key >=0;
	}

	public static Mat detectObjects(Mat srcImage){
    			
		int thresh = 100;
		
        Mat resultImage = srcImage.clone();
        if(resultImage== null) return null;
        
        //MatVector rgb = new MatVector();
        //cvtColor(rgb.get(1), grayImage, CV_BGR2GRAY);
        
        Mat grayImage = new Mat();
        cvtColor(resultImage, grayImage, CV_BGR2GRAY);
         
        
        threshold(grayImage, grayImage, 100, 200, CV_THRESH_BINARY);
        
        Mat cannyOutput = new Mat();
        Canny( grayImage, cannyOutput, thresh, thresh*2, 3, false );
        
        
        imshow("Thres" , grayImage); 
        imshow("Canny" , cannyOutput);   
        
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        
        findContours(cannyOutput , contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, new Point(0,0));
        
        grayImage._deallocate();
        cannyOutput._deallocate();
        for(int pos = 0; pos < contours.size(); pos ++){        	
        	Rect boundingBox = boundingRect(contours.get(pos));
        	
        	if(boundingBox.width() < 12 || boundingBox.height() <12
        			&& boundingBox.width() > 35 || boundingBox.height() >35){
            	continue;
            }
        	
        	rectangle(resultImage, new Point(boundingBox.x(),boundingBox.y()),
        			new Point(boundingBox.x()+boundingBox.width(), boundingBox.y()+boundingBox.height()),
        			new Scalar(0,255,0,0),1,0,0);
        	
        }                        
      
        return resultImage;
    }
	
	public static void main(String[] args){
		//String video = "C:\\SSD\\datasets\\img-processing\\alone.mp4";
		String video = "C:\\SSD\\datasets\\img-processing\\586917256.mp4";
		//String video = "C:\\SSD\\datasets\\img-processing\\garden.mp4";
		//String filename = "young-strawberry-transplants.jpg";
		
		namedWindow("Result",WINDOW_AUTOSIZE);
		//namedWindow("Canny",WINDOW_AUTOSIZE);
		namedWindow("Thres",WINDOW_AUTOSIZE);
		
		String output = grabFrame(video);
				 
	     
	}
	
}

