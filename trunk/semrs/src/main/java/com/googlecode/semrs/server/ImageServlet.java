package com.googlecode.semrs.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.awt.AlphaComposite;  
import java.awt.Graphics2D;  
import java.awt.Image;  
import java.awt.image.BufferedImage;  
import java.io.File;  
import javax.imageio.ImageIO;  

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.semrs.model.Patient;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.PatientService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.Util;

public class ImageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 646231334703969586L;

	private static final Log LOG = LogFactory.getLog(ImageServlet.class);

	private static final String userFolder = "/user/images/"; 

	private static final String patientFolder = "/patient/images/"; 

	private static final HashMap TYPE_MAPS = new HashMap();
	static{
		TYPE_MAPS.put( "image/jpeg", "jpeg");
		TYPE_MAPS.put( "image/gif", "gif" );
		TYPE_MAPS.put( "image/png", "png" );
		TYPE_MAPS.put( "image/jpg", "jpeg" );
		TYPE_MAPS.put( "application/jpg", "jpeg");
		TYPE_MAPS.put( "application/x-jpg", "jpeg");
	}


	public void init(ServletConfig config) throws ServletException {
		 super.init(config);
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		handleRequest(req, resp);
	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String type = req.getParameter("type");
		
		String operation = req.getParameter("op");
		Object obj =
			SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = "";
		if ( obj instanceof UserDetails ) {
			userName= ( (UserDetails)obj ).getUsername();
		} 
		String id   = req.getParameter("id").equals("currentUser")?userName:req.getParameter("id");
		
		String folder =  "";

		synchronized(folder){
			if(type.equals("user")){
				folder = userFolder;
			}else if(type.equals("patient")){
				folder = patientFolder;

			}
			ServletContext sc = req.getSession().getServletContext();
			// Required: Width image should be resized to  
			int width = Integer.parseInt(getParam(req, "width", "145"));  
			// Optional: If specified used, otherwise proportions are calculated  
			int height = Integer.parseInt(getParam(req, "height", "168"));  

			URL resource = this.getClass().getResource(folder);
			int counter = 0;
			File nopic = null;
			File imageFolder = new File(resource.getFile());
			if(imageFolder!=null){
				File[] images = imageFolder.listFiles();
				if(images!=null && images.length>0){
					for(File file : images){
						String fileName = file.getName().substring(0,file.getName().lastIndexOf("."));
						if(fileName.equals(id) || operation.equals("upload")){
							counter+=1;
							if(!fileName.equals("nopic")  && operation.equals("upload")){
								FileItemFactory factory = new DiskFileItemFactory();
								ServletFileUpload upload = new ServletFileUpload(factory);
								try {
									List items = upload.parseRequest(req);
									Iterator iter = items.iterator();
									while (iter.hasNext()) {
										FileItem item = (FileItem) iter.next();
										if (!item.isFormField()){
											String ext = (String) TYPE_MAPS.get( item.getContentType() );
											if( ext == null ){
												resp.setStatus(resp.SC_EXPECTATION_FAILED);
												return;
											}
											// save to file system now...

											resource = this.getClass().getResource(folder);
											FileOutputStream fos = new FileOutputStream(resource.getFile()+id+".jpeg");
											Util.copyStream( item.getInputStream(), fos );
											if(fileName.equals(id)){
												file.delete();
											}
											fos.flush();
											fos.close();
											item.getInputStream().close();
											break;
										}
									}
								} catch (FileUploadException e) {
									resp.setStatus(resp.SC_INTERNAL_SERVER_ERROR);
									return;
								}

								JSONObject response = null;
								try {
									response = new JSONObject();
									response.put("success", true);
									response.put("error", "Imagen salvada");
									response.put("code", "232");
								} catch (Exception e) {

								}

								Writer w = new OutputStreamWriter(resp.getOutputStream());
								w.write(response.toString());
								w.close();
								resp.setStatus(HttpServletResponse.SC_OK);
								return;
							}else if( !fileName.equals("nopic")  && operation.equals("load")){


								//String filename = sc.getRealPath("image.gif");
								String filename = file.getPath();

								// Get the MIME type of the image
								String mimeType = sc.getMimeType(filename);
								if (mimeType == null) {
									sc.log("Could not get MIME type of "+filename);
									resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
									return;
								}

								// Set content type
								resp.setContentType(mimeType);

								// Set content size
								resp.setContentLength((int)file.length());

								// Open the file and output streams
								FileInputStream in = new FileInputStream(file);
								ServletOutputStream out = resp.getOutputStream();

								try {  
									// Read the original image from the Server Location  
									BufferedImage bufferedImage = ImageIO.read(in);  
									// Calculate the new Height if not specified  
									if(bufferedImage.getWidth() > 200){

										int calcHeight = height > 0 ? height : (width * bufferedImage.getHeight() / bufferedImage.getWidth()); 
										ImageIO.write(Util.createResizedCopy(bufferedImage, width, calcHeight), "jpeg", out);  
									}else{
										ImageIO.write(Util.createResizedCopy(bufferedImage, 118, 120), "jpeg", out);  
									}  

								} catch (Exception e) {  
									return;
								}  

								in.close();
								out.close();
								return;

							}

						}else if(fileName.equals("nopic")){
							nopic = file;
						}
					}
					if(counter==0 && operation.equals("load")){

						//String filename = sc.getRealPath("image.gif");
						String filename = nopic.getPath();

						// Get the MIME type of the image
						String mimeType = sc.getMimeType(filename);
						if (mimeType == null) {
							sc.log("Could not get MIME type of "+filename);
							resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							return;
						}

						// Set content type
						resp.setContentType(mimeType);

						// Set content size
						resp.setContentLength((int)nopic.length());

						// Open the file and output streams
						FileInputStream in = new FileInputStream(nopic);
						ServletOutputStream out = resp.getOutputStream();

						try {  
							// Read the original image from the Server Location  
							BufferedImage bufferedImage = ImageIO.read(in);  
							// Calculate the new Height if not specified  
							if(bufferedImage.getWidth() > 200){

								int calcHeight = height > 0 ? height : (width * bufferedImage.getHeight() / bufferedImage.getWidth()); 
								ImageIO.write(Util.createResizedCopy(bufferedImage, width, calcHeight), "jpeg", out);  
							}else{
								ImageIO.write(Util.createResizedCopy(bufferedImage, 118, 120), "jpeg", out);  
							}  

						} catch (Exception e) {  
							return;
						}  

						in.close();
						out.close();
						return;
					}

				}else{
					if(operation.equals("upload")){
						FileItemFactory factory = new DiskFileItemFactory();
						ServletFileUpload upload = new ServletFileUpload(factory);
						try {
							List items = upload.parseRequest(req);
							Iterator iter = items.iterator();
							while (iter.hasNext()) {
								FileItem item = (FileItem) iter.next();
								if (!item.isFormField()){
									String ext = (String) TYPE_MAPS.get( item.getContentType() );
									if( ext == null ){
										resp.setStatus(resp.SC_EXPECTATION_FAILED);
										return;
									}
									// save to file system now...

									resource = this.getClass().getResource(folder);
									FileOutputStream fos = new FileOutputStream(resource.getFile()+id+".jpeg");
									Util.copyStream( item.getInputStream(), fos );
									fos.flush();
									fos.close();
									item.getInputStream().close();
									break;
								}
							}

						} catch (FileUploadException e) {
							resp.setStatus(resp.SC_INTERNAL_SERVER_ERROR);
							return;
						}

						JSONObject response = null;
						try {
							response = new JSONObject();
							response.put("success", true);
							response.put("error", "Imagen salvada");
							response.put("code", "232");
						} catch (Exception e) {

						}

						Writer w = new OutputStreamWriter(resp.getOutputStream());
						w.write(response.toString());
						w.close();
						resp.setStatus(HttpServletResponse.SC_OK);
						return;
					}
				}
			}
		}
	}
	

	
	// Check the param if it's not present return the default  
	     private String getParam(HttpServletRequest request, String param, String def) {  
	         String parameter = request.getParameter(param);  
	         if (parameter == null || "".equals(parameter)) {  
	             return def;  
	         } else {  
	             return parameter;  
	         }  
	     }  
	
	     

}
