import static org.bytedeco.javacpp.Loader.sizeof;
import static org.bytedeco.javacpp.opencv_core.cvCloneImage;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;


import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;

import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_CCOMP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;




import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.CvType;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class Application {
    
	public static void smooth(String filename) { 
        IplImage image = cvLoadImage(filename);
        if (image != null) {
            cvSmooth(image, image);
            cvSaveImage(filename, image);
            cvReleaseImage(image);
        }
    }
    
    public static String grabFrame(String filename){    	
    	FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filename);    	
    	String output= "";
    	
	     try {
	         //Start grabber to capture video
	         grabber.start(); 

	         OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	         grabber.setFrameNumber(30*19);
	         IplImage img = converter.convert(grabber.grab());
	         
	         
	         if (img != null) {         
	             //save video frame as a picture
	        	 String temp = "temp.jpg";
	             cvSaveImage(temp, img);	 
	             output = temp;
	         }

	     }catch (Exception e) {      
	     }
	     return output;
    }
    
    public static IplImage detectObjects(IplImage srcImage){
    	    	
        IplImage resultImage = cvCloneImage(srcImage);
        
        IplImage grayImage = cvCreateImage(cvGetSize(srcImage), IPL_DEPTH_8U, 1);
        cvCvtColor(srcImage, grayImage, CV_BGR2GRAY);
        
        CvMemStorage mem;
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();       
        cvThreshold(grayImage, grayImage, 150, 255, CV_THRESH_BINARY);
        cvSaveImage("threshold.jpg", grayImage);
        mem = cvCreateMemStorage(0);

        cvFindContours(grayImage, mem, contours, sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));

        CvRect boundbox;

        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            boundbox = cvBoundingRect(ptr, 0);
            
            if(boundbox.width() < 30 || boundbox.height() <30){
            	continue;
            }

            cvRectangle( resultImage , cvPoint( boundbox.x(), boundbox.y() ), 
                 cvPoint( boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),
                 cvScalar( 0, 255, 0, 0 ), 1, 0, 0 );
        }

        return resultImage;
    }
    
    public static void main(String[] args){
    	String video = "C:\\SSD\\datasets\\img-processing\\alone.mp4";
    	//String video = "C:\\SSD\\datasets\\img-processing\\garden.mp4";
    	video = null;
    	String image = "young-strawberry-transplants.jpg";
    	
    	
    	String frame = null;
    	if(video != null){
    		frame = grabFrame(video);
    	}else{
    		frame = image;
    	}
    	
    	if(frame != null && !frame.isEmpty()){
    		//smooth(frame);
    		IplImage detectObjects = detectObjects(new IplImage(cvLoadImage(frame)));
    		cvSaveImage("objects.jpg", detectObjects);
    	}
    	
    }
}

