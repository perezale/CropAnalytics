package net.pladema;

import static org.bytedeco.javacpp.Loader.sizeof;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_CCOMP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
//import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCloneImage;
import static org.bytedeco.javacpp.opencv_core.cvCloneMat;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.threshold;
import static org.bytedeco.javacpp.opencv_imgproc.findContours;
import static org.bytedeco.javacpp.opencv_imgproc.boundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;





import java.awt.Image;

import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.avcodec.*;
import static org.bytedeco.javacpp.avutil.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_video.*;
import static org.bytedeco.javacpp.opencv_videoio.*;

public class OpenCV3Test2 {
	
	public static String grabFrame(String filename){
		    
		String output= "";
		
	     try {
	         //Start grabber to capture video
	         
	    	 FrameGrabber grabber = FrameGrabber.createDefault(filename);
	         grabber.start();	         	         
	         	         
	         namedWindow("Cam",CV_WINDOW_AUTOSIZE);	         
	         //CanvasFrame canvasFrame = new CanvasFrame("Cam");
	         //Frame imgGrab;
	         ToMat converter = new OpenCVFrameConverter.ToMat();
	         
	         while (true){	        	 
	        	 
	        	Mat img = new Mat();
	        	
	        	Frame grab = grabber.grab();
	        		        	
	        	if(grab == null){
	        		System.err.println("Couldn't grab frame in video");
	            	 break;
	        	}
	        		
	        	img = converter.convert(grab);
	        	
	        	
	        	        	 	
	        	imshow("Cam", img);
	             //canvasFrame.showImage(imgGrab);
	             if(waitKey(30) == 27){
	            	 System.out.println("ESC key pressed by user");
	            	 break;
	             }
	             
	             	             
	         }
	         grabber.stop();
	         
	         
	         /*
	         
	         
	         
	         OpenCVFrameConverter.ToMat conv = new OpenCVFrameConverter.ToMat();
	         Mat img = conv.convert(grabber.grab());
	         
	         
	         if(img!=null){
	        	 String temp = "temp.jpg";
	             imwrite(temp, img);	 
	             output = temp;
	         }*/
	         
	     }catch (Exception e) {      
	     }
	     return output;
	}
	
	public static Mat detectObjects(Mat srcImage){
    	
        Mat resultImage = srcImage.clone();
        
        Mat grayImage = new Mat();
        cvtColor(resultImage, grayImage, CV_BGR2GRAY);
         
        threshold(grayImage, grayImage, 100, 150, CV_THRESH_BINARY);
        
        imwrite("threshold.jpg", grayImage);
           
        
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        findContours(grayImage, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, new Point(0,0));
        
        
        for(int pos = 0; pos < contours.size(); pos ++){        	
        	Rect boundingBox = boundingRect(contours.get(pos));
        	
        	if(boundingBox.width() < 10 || boundingBox.height() <10
        			&& boundingBox.width() > 30 || boundingBox.height() >30){
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
		//String filename = "young-strawberry-transplants.jpg";
		
		String output = grabFrame(video);
		/*
		Mat img = imread(output);
		
		Mat detectObjects = detectObjects(img);
		
		namedWindow("Result",WINDOW_AUTOSIZE);
        imshow("Result" , detectObjects);
        waitKey(0);   */     	    
	     
	}
	
}

