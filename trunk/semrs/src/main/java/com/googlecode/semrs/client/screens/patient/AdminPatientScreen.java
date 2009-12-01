package com.googlecode.semrs.client.screens.patient;

import java.util.Date;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.semrs.client.ExtendedMessageBox;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.GenericConfig;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.DefaultsHandler;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.TimeField;
import com.gwtext.client.widgets.form.VType;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.RowParams;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtext.client.widgets.portal.Portal;
import com.gwtext.client.widgets.portal.PortalColumn;
import com.gwtext.client.widgets.portal.Portlet;
import com.gwtextux.client.widgets.image.Image;
import com.gwtextux.client.widgets.upload.UploadDialog;
import com.gwtextux.client.widgets.upload.UploadDialogListenerAdapter;

public class AdminPatientScreen extends ShowcasePanel{

	
	private String patientId = "";
	private String voided = "";
	private String lastEncounterDate = "";
	FormPanel formPanel = null;
    FormPanel voidForm = null; 
    FormPanel encounterForm = null;
	public static boolean flag1 = false;
	boolean currentEncountersReloadFlag = true;
	boolean previousEncountersReloadFlag = false;
	TabPanel patientTabpanel = null;
	Window newEncounterWindow = null;
	Window voidWindow = null;
	private Store mainPanelStore;
	private com.gwtext.client.widgets.TabPanel tabPanel;

	
	protected void onActivate() {
	
		if(!flag1){
			getViewPanel();
		}
		flag1=false;
	}

	public Panel getViewPanel() {
		
		flag1=true;
		//panel.removeAll(false);
		
		if(formPanel!=null){
			//com.google.gwt.user.client.Window.alert("formPanel not null");
			try{
			formPanel.removeAll(true);
			}catch(Throwable t){
				//com.google.gwt.user.client.Window.alert("formPanel throwable " + t);
				try{
					formPanel.removeAll(false);
				}catch(Throwable t2){
					//com.google.gwt.user.client.Window.alert("formPanel throwable2 " + t2);
				}
			}
		}
			
			//com.google.gwt.user.client.Window.alert("formPanel destroyed");
			if(newEncounterWindow!=null){
			  //com.google.gwt.user.client.Window.alert("newEncounterWindow not null");
				try{
					newEncounterWindow.removeAll(true);
				}catch(Throwable t){
					//com.google.gwt.user.client.Window.alert("newEncounterWindow throwable " + t);
					try{
						newEncounterWindow.removeAll(false);
					}catch(Throwable t2){
						//com.google.gwt.user.client.Window.alert("newEncounterWindow throwable2 " + t2);
					}
				}
			//  com.google.gwt.user.client.Window.alert("newEncounterWindow destroyed");
				
			}
			if(voidWindow!=null){
			// com.google.gwt.user.client.Window.alert("voidWindow not null");
				try{
					voidWindow.removeAll(true);
				}catch(Throwable t){
					//com.google.gwt.user.client.Window.alert("voidWindow throwable " + t);
					try{
					  voidWindow.removeAll(false);
					}catch(Throwable t2){
						//com.google.gwt.user.client.Window.alert("voidWindow throwable2 " + t2);
					}
				}
				//com.google.gwt.user.client.Window.alert("voidWindow destroyed");
			}
			
			
			if(voidForm!=null){
			  //com.google.gwt.user.client.Window.alert("voidForm not null");
				try{
				  voidForm.removeAll(true);
				}catch(Throwable t){
					//com.google.gwt.user.client.Window.alert("voidForm throwable " + t);
					try{
					  voidForm.removeAll(false);
					}catch(Throwable t2){
						//com.google.gwt.user.client.Window.alert("voidForm throwable2 " + t2);
					}
				}
			//  com.google.gwt.user.client.Window.alert("voidForm destroyed");
				
			}
			if(encounterForm!=null){
			// com.google.gwt.user.client.Window.alert("encounterForm not null");
				try{
				encounterForm.removeAll(true);
				}catch(Throwable t){
					//com.google.gwt.user.client.Window.alert("encounterForm throwable " + t);
					try{
					encounterForm.removeAll(false);
					}catch(Throwable t2){
						//com.google.gwt.user.client.Window.alert("encounterForm throwable2 " + t2);
					}
				}
			 //com.google.gwt.user.client.Window.alert("encounterForm destroyed");
				
			}
				
			if(patientTabpanel!=null){
			// com.google.gwt.user.client.Window.alert("patientTabpanel not null");
				try{
				patientTabpanel.removeAll(true);
				}catch(Throwable t){
					//com.google.gwt.user.client.Window.alert("patientTabpanel throwable " + t);
					try{
						patientTabpanel.removeAll(false);
						}catch(Throwable t2){
							//com.google.gwt.user.client.Window.alert("patientTabpanel throwable2 " + t2);
					}
				}
			// com.google.gwt.user.client.Window.alert("patientTabpanel destroyed");
			}

		
	
		
		panel = new Panel();
		/*
		panel.setDefaults(new DefaultsHandler() {
     	   public void apply(Component component) {
 		      if(!Ext.isIE()) {
 		         component.setHideMode("visibility");
 		         GenericConfig config = new GenericConfig();
 		         config.setProperty("position", "absolute");
 		         component.setStyle(config);
 		      }
 		   }
 		});*/

		MainPanel.resetTimer();
		//com.google.gwt.user.client.Window.alert("building panel" + panel.toString());
		formPanel = new FormPanel();  
		//com.google.gwt.user.client.Window.alert("building formPanel" + formPanel.toString());
		formPanel.setId("patientDetailForm");
		formPanel.setTitle("Detalle de Paciente");  
		formPanel.setFrame(true);  
		formPanel.setPaddings(5, 0, 5, 5);  
		formPanel.setWidth(960);  
		formPanel.setLabelWidth(100);
		formPanel.setIconCls("patient-detail-icon");

		
		//formPanel.setLayout(new FitLayout());
		
		voidForm = new FormPanel();  
		voidForm.setId("patientEditVoidForm");
	    voidForm.setFrame(true);  
	    voidForm.setWidth(450);  
	    voidForm.setHeight(600);
	    voidForm.setAutoScroll(true);
		
	    encounterForm = new FormPanel();  
	    encounterForm.setId("patientEditNewEncounterForm");
	    encounterForm.setFrame(true);  
	    encounterForm.setWidth(450);  
	    encounterForm.setHeight(600);
	    encounterForm.setAutoScroll(true);
	    
	    patientTabpanel = new TabPanel();  
        patientTabpanel.setActiveTab(0);  
        patientTabpanel.setHeight(600);  
        patientTabpanel.setId("patientDetailTabPanel");
        /*
        patientTabpanel.setDefaults(new DefaultsHandler() {
        	   public void apply(Component component) {
        		      if(!Ext.isIE()) {
        		         component.setHideMode("visibility");
        		         GenericConfig config = new GenericConfig();
        		         config.setProperty("position", "absolute");
        		         component.setStyle(config);
        		      }
        		   }
        		});
        		*/

        Panel firstTab = new Panel();
        firstTab.setTitle("Consultas Activas"); 
        firstTab.setPaddings(10);
        firstTab.setLayout(new FormLayout()); 
     
        Panel secondTab = new Panel();  
        secondTab.setTitle("Consultas Pasadas");  
        secondTab.setLayout(new FormLayout()); 
        secondTab.setPaddings(10);
        
        Panel thirdTab = new Panel();  
        thirdTab.setTitle("Resumen");  
        thirdTab.setLayout(new FormLayout()); 
        thirdTab.setPaddings(10);
     
        
        RecordDef formRecordDef = new RecordDef(new FieldDef[]{   
				new StringFieldDef("id"),  
				new StringFieldDef("name"),  
				new StringFieldDef("lastName"),
				new StringFieldDef("sex"),
				new StringFieldDef("birthDate"),
				new StringFieldDef("age"),  
				new StringFieldDef("phoneNumber"),  
				new StringFieldDef("mobile"),
				new StringFieldDef("address"),
				new StringFieldDef("description"), 
				new StringFieldDef("birthPlace"),
				new StringFieldDef("email"),
				new StringFieldDef("creationDate"),
				new StringFieldDef("provider"),
				new StringFieldDef("providerId"),
				new StringFieldDef("voided"),
				new StringFieldDef("voidReason"),
				new StringFieldDef("voidDate"),
				new StringFieldDef("lastEncounterDate"),
				new StringFieldDef("bloodType"),
				new StringFieldDef("loadSuccess")
				
		});  

		final JsonReader formReader = new JsonReader("data", formRecordDef);  
		formReader.setSuccessProperty("success"); 
		formReader.setId("id");
		
		HttpProxy loadProxy = new HttpProxy("/semrs/patientServlet?patientAction=getPatient&id="+getPatientId(), Connection.GET);
		final Store formStore = new Store(loadProxy,formReader);
		
		FieldDef[] currentEncountersFieldDefs = new FieldDef[] { 
	    		new StringFieldDef("id"),
	            new StringFieldDef("patientId"), 
	            new StringFieldDef("patientName"), 
	            new StringFieldDef("encounterProvider"), 
	    		new StringFieldDef("encounterDate"),
	    		new StringFieldDef("refferral"),
	    		new StringFieldDef("reason"),
	    		new StringFieldDef("creationDate"),	
	    		new StringFieldDef("creationUser"),
	    		new StringFieldDef("edit")
	    };

	    RecordDef currentEncountersRecordDef = new RecordDef(currentEncountersFieldDefs);
	    JsonReader currentEncountersReader = new JsonReader("response.value.items", currentEncountersRecordDef);
		currentEncountersReader.setTotalProperty("response.value.total_count");
    	currentEncountersReader.setId("id");

    	HttpProxy currentEncountersProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=listCurrentEncounters&patientId="+getPatientId(), Connection.GET);
		final Store currentEncountersStore = new Store(currentEncountersProxy,currentEncountersReader,true);
		final PagingToolbar currentEncountersPagingToolbar = new PagingToolbar(currentEncountersStore);
		//currentEncountersPagingToolbar.setId("patientEditCurrentEncountersPagingToolbar");
        currentEncountersStore.setDefaultSort("encounterDate", SortDir.DESC);
        currentEncountersStore.addStoreListener(new StoreListenerAdapter() {
        	public void onLoadException(Throwable error) {
        		  //Check for session expiration
        		  RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
        	        try {
        	            rb.sendRequest(null, new RequestCallback() {

        	                public void onError(Request request, Throwable exception) {
        	                	MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de consultas.");
        	                }
							public void onResponseReceived(Request arg0, Response arg1) {
								String errorMessage = arg1.getText();
			            		if(errorMessage.indexOf("login") != -1){
			            			MessageBox.alert("Error", "Su sesi&oacute;n de usuario ha expirado, presione OK para volver a loguearse." ,  
			                                 new MessageBox.AlertCallback() { 
												public void execute() {
													redirect("/semrs/");
												}  
			                                 });  
			            			
			            		}else{
			            			MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de consultas.");
			            		}
							}
        	            });
        	        } catch (RequestException e) {
        	        	MessageBox.alert("Error", "Ha ocurrido un error al tratar de conectarse con el servidor.");
        	        }
			}
        	 public void onDataChanged(Store store) {
        		 MainPanel.resetTimer();
        	    }

        });
        
        //currentEncountersStore.load(); 
        
        
        FieldDef[] previousEncountersFieldDefs = new FieldDef[] { 
	    		new StringFieldDef("id"),
	    		new StringFieldDef("patientId"), 
		        new StringFieldDef("patientName"), 
		        new StringFieldDef("encounterProvider"), 
	    		new StringFieldDef("encounterDate"),
	    		new StringFieldDef("refferral"),
	    		new StringFieldDef("reason"),
	    		new StringFieldDef("creationDate"),
	    		new StringFieldDef("creationUser"),
	    		new StringFieldDef("endDate"),
	    		new StringFieldDef("lastEditDate"),
	    		new StringFieldDef("lastEditUser")
	    };

	    RecordDef previousEncountersRecordDef = new RecordDef(previousEncountersFieldDefs);

	    JsonReader previousEncountersReader = new JsonReader("response.value.items", previousEncountersRecordDef);
    	previousEncountersReader.setTotalProperty("response.value.total_count");
    	previousEncountersReader.setId("id");

		
		HttpProxy previousEncountersProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=listPreviousEncounters&patientId="+getPatientId(), Connection.GET);
		final Store previousEncountersStore = new Store(previousEncountersProxy,previousEncountersReader,true);
		final PagingToolbar previousEncountersPagingToolbar = new PagingToolbar(previousEncountersStore);
		//previousEncountersPagingToolbar.setId("patientEditPreviousEncountersPagingToolbar");
        previousEncountersStore.setDefaultSort("lastEditDate", SortDir.DESC);
        previousEncountersStore.addStoreListener(new StoreListenerAdapter() {
        	public void onLoadException(Throwable error) {
        		  //Check for session expiration
        		  RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
        	        try {
        	            rb.sendRequest(null, new RequestCallback() {

        	                public void onError(Request request, Throwable exception) {
        	                	MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de consultas.");
        	                }
							public void onResponseReceived(Request arg0, Response arg1) {
								String errorMessage = arg1.getText();
			            		if(errorMessage.indexOf("login") != -1){
			            			MessageBox.alert("Error", "Su sesi&oacute;n de usuario ha expirado, presione OK para volver a loguearse." ,  
			                                 new MessageBox.AlertCallback() { 
												public void execute() {
													redirect("/semrs/");
												}  
			                                 });  
			            			
			            		}else{
			            			MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de consultas.");
			            		}
							}
        	            });
        	        } catch (RequestException e) {}
			}
        	 public void onDataChanged(Store store) {
        		 MainPanel.resetTimer();
        	    }

        });
        
        FieldDef[] latestTreatmentsFieldDefs = new FieldDef[] { 
	    		new StringFieldDef("id"),
	    		new StringFieldDef("name"), 
		        new StringFieldDef("startDate"), 
		        new StringFieldDef("endDate"), 
	    		new StringFieldDef("instructions"),
	    		new StringFieldDef("encounterDate"),
	    		new StringFieldDef("encounterProvider"),
	    		new StringFieldDef("encounterId")
	    };

	    RecordDef latestTreatmentsRecordDef = new RecordDef(latestTreatmentsFieldDefs);

	    JsonReader latestTreatmentsReader = new JsonReader("response.value.items", latestTreatmentsRecordDef);
	    latestTreatmentsReader.setTotalProperty("response.value.total_count");
	    latestTreatmentsReader.setId("id");

		
		HttpProxy latestTreatmentsProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getLatestEncounterRecords&patientId="+getPatientId()+"&limit=5"+"&recordType=treatments", Connection.GET);
		final Store latestTreatmentsStore = new Store(latestTreatmentsProxy,latestTreatmentsReader,false);
		latestTreatmentsStore.setDefaultSort("encounterDate", SortDir.DESC);
      


        
        FieldDef[] latestDiagnosesFieldDefs = new FieldDef[] { 
	    		new StringFieldDef("id"),
	    		new StringFieldDef("name"), 
		        new StringFieldDef("severity"), 
		        new StringFieldDef("type"),
	    		new StringFieldDef("encounterDate"),
	    		new StringFieldDef("encounterProvider"),
	    		new StringFieldDef("encounterId")
	    };

	    RecordDef latestDiagnosesRecordDef = new RecordDef(latestDiagnosesFieldDefs);

	    JsonReader latestDiagnosesReader = new JsonReader("response.value.items", latestDiagnosesRecordDef);
	    latestDiagnosesReader.setTotalProperty("response.value.total_count");
	    latestDiagnosesReader.setId("id");

		
		HttpProxy latestDiagnosesProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getLatestEncounterRecords&patientId="+getPatientId()+"&limit=5"+"&recordType=diagnoses", Connection.GET);
		final Store latestDiagnosesStore = new Store(latestDiagnosesProxy,latestDiagnosesReader,false);
		latestDiagnosesStore.setDefaultSort("encounterDate", SortDir.DESC);
	
		
		
        FieldDef[] latestLabTestsFieldDefs = new FieldDef[] { 
	    		new StringFieldDef("id"),
	    		new StringFieldDef("name"), 
		        new StringFieldDef("result"), 
		        new StringFieldDef("resultDesc"), 
		        new StringFieldDef("date"),
	    		new StringFieldDef("encounterDate"),
	    		new StringFieldDef("encounterProvider"),
	    		new StringFieldDef("encounterId")
	    };

	    RecordDef latestLabTestsRecordDef = new RecordDef(latestLabTestsFieldDefs);

	    JsonReader latestLabTestsReader = new JsonReader("response.value.items", latestLabTestsRecordDef);
	    latestLabTestsReader.setTotalProperty("response.value.total_count");
	    latestLabTestsReader.setId("id");

		
		HttpProxy latestLabTestsProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getLatestEncounterRecords&patientId="+getPatientId()+"&limit=5"+"&recordType=labTests", Connection.GET);
		final Store latestLabTestsStore = new Store(latestLabTestsProxy,latestLabTestsReader,false);
		latestLabTestsStore.setDefaultSort("encounterDate", SortDir.DESC);
		
        
		
		  FieldDef[] latestSymptomsFieldDefs = new FieldDef[] { 
		    		new StringFieldDef("id"),
		    		new StringFieldDef("name"), 
			        new StringFieldDef("type"), 
			        new StringFieldDef("htype"),
			        new StringFieldDef("severity"),
		    		new StringFieldDef("encounterDate"),
		    		new StringFieldDef("encounterProvider"),
		    		new StringFieldDef("encounterId")
		    };

		    RecordDef latestSymptomsRecordDef = new RecordDef(latestSymptomsFieldDefs);

		    JsonReader latestSymptomsReader = new JsonReader("response.value.items", latestSymptomsRecordDef);
		    latestSymptomsReader.setTotalProperty("response.value.total_count");
		    latestSymptomsReader.setId("id");


		    HttpProxy latestSymptomsProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getLatestEncounterRecords&patientId="+getPatientId()+"&limit=5"+"&recordType=symptomOrDiseases", Connection.GET);
		    final Store latestSymptomsStore = new Store(latestSymptomsProxy,latestSymptomsReader,false);
		    latestSymptomsStore.setDefaultSort("encounterDate", SortDir.DESC);


		    FieldDef[] latestComplicationsFieldDefs = new FieldDef[] { 
		    		new StringFieldDef("id"),
		    		new StringFieldDef("name"), 
		    		new StringFieldDef("date"), 
		    		new StringFieldDef("encounterDate"),
		    		new StringFieldDef("encounterProvider"),
		    		new StringFieldDef("encounterId")
		    };

		    RecordDef latestComplicationsRecordDef = new RecordDef(latestComplicationsFieldDefs);

		    JsonReader latestComplicationsReader = new JsonReader("response.value.items", latestComplicationsRecordDef);
		    latestComplicationsReader.setTotalProperty("response.value.total_count");
		    latestComplicationsReader.setId("id");


		    HttpProxy latestComplicationsProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getLatestEncounterRecords&patientId="+getPatientId()+"&limit=5"+"&recordType=complications", Connection.GET);
		    final Store latestComplicationsStore = new Store(latestComplicationsProxy,latestComplicationsReader,false);
		    latestComplicationsStore.setDefaultSort("encounterDate", SortDir.DESC);


		    FieldDef[] latestProceduresFieldDefs = new FieldDef[] { 
		    		new StringFieldDef("id"),
		    		new StringFieldDef("name"), 
		    		new StringFieldDef("date"), 
		    		new StringFieldDef("encounterDate"),
		    		new StringFieldDef("encounterProvider"),
		    		new StringFieldDef("encounterId")
		    };

		    RecordDef latestProceduresRecordDef = new RecordDef(latestProceduresFieldDefs);

		    JsonReader latestProceduresReader = new JsonReader("response.value.items", latestProceduresRecordDef);
		    latestProceduresReader.setTotalProperty("response.value.total_count");
		    latestProceduresReader.setId("id");


		    HttpProxy latestProceduresProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getLatestEncounterRecords&patientId="+getPatientId()+"&limit=5"+"&recordType=procedures", Connection.GET);
		    final Store latestProceduresStore = new Store(latestProceduresProxy,latestProceduresReader,false);
		    latestProceduresStore.setDefaultSort("encounterDate", SortDir.DESC);



	

	  	    
		
		Panel proxy = new Panel();  
		proxy.setBorder(true);  
		proxy.setBodyBorder(false);
		proxy.setCollapsible(false);	
		proxy.setId("patientImageProxy");
		final String imgSrc = "/semrs/imageServlet?type=patient&id="+getPatientId();
		final Image patientImage = new Image("",imgSrc+"&op=load&date="+new Date().getSeconds());
		HTML newImage = new HTML("<a href='javascript:;'>Nueva Imagen</a>");
		ClickListener cl = new ClickListener(){
			public void onClick(Widget widget) { 
				UploadDialog dialog = new UploadDialog();  
				dialog.setId("uploadPatientImageDialog");
				dialog.setUrl(imgSrc+"&op=upload");  
				dialog.setPermittedExtensions(new String[]{"jpg", "gif", "png", "jpeg"});  
				dialog.addListener(new UploadDialogListenerAdapter() {
					public boolean onBeforeAdd(UploadDialog source, String filename) {
						if(source.getQueuedCount() > 0) {
							return false;
						} else {
							return true;
						}
					}

					public void onUploadComplete(UploadDialog source){
						patientImage.setSrc(imgSrc+"&op=load&date="+new Date().getSeconds());
						MainPanel.resetTimer();

					}
				});
				dialog.show(); 
			}  
			
		};
		newImage.addClickListener(new ClickListener() {  
			public void onClick(Widget widget) { 
				UploadDialog dialog = new UploadDialog();  
				dialog.setId("uploadPatientImageDialog");
				dialog.setUrl(imgSrc+"&op=upload");  
				dialog.setPermittedExtensions(new String[]{"jpg", "gif", "png", "jpeg"});  
				dialog.addListener(new UploadDialogListenerAdapter() {
					public boolean onBeforeAdd(UploadDialog source, String filename) {
						if(source.getQueuedCount() > 0) {
							return false;
						} else {
							return true;
						}
					}

					public void onUploadComplete(UploadDialog source){
						patientImage.setSrc(imgSrc+"&op=load&date="+new Date().getSeconds());
						MainPanel.resetTimer();

					}
				});
				dialog.show(); 
			}  
		}); 
		proxy.add(patientImage);
		proxy.add(newImage);
		
		 
		
	    // proxy.add(cb);
		
	     final Panel topPanel = new Panel();  
	     topPanel.setLayout(new ColumnLayout());  
	     topPanel.setBorder(false); 
	     topPanel.setButtonAlign(Position.CENTER);
	     topPanel.setFrame(false);

	     Panel firstColumn = new Panel();  
	     firstColumn.setLayout(new FormLayout());  
	     firstColumn.setBorder(false);  

	     firstColumn.add(proxy,new AnchorLayoutData("80%") );
	     topPanel.add(firstColumn, new ColumnLayoutData(0.20)); //0.2 

	     Panel secondColumn = new Panel();  
	     secondColumn.setLayout(new FormLayout());  
	     secondColumn.setBorder(true);  
	     

	     final TextField textPatientId =  new TextField("C&eacute;dula", "id");
	     textPatientId.setId("patientIdEdit");
	     textPatientId.setReadOnly(true);
         
	     final TextField patientName =  new TextField("Nombres", "name");
	     patientName.setId("patientNameEdit");
	     patientName.setAllowBlank(false);  
	     
	     final TextField patientLastName =  new TextField("Apellidos", "lastName");
	     patientLastName.setId("patientLastNameEdit");
	     patientLastName.setAllowBlank(false);
	     
	     final TextArea addressTextArea = new TextArea("Direcci&oacute;n", "address");  
	     addressTextArea.setHideLabel(false);  
	     addressTextArea.setWidth(190);
	     addressTextArea.setHeight(80);
	     addressTextArea.setAllowBlank(false);
	     addressTextArea.setId("editPatientAddressTextArea");
	     
		 final TextField creationDate = new TextField("Fecha de Ingreso", "creationDate", 190);  
		 creationDate.setId("editPatientCreationDate");	
		 creationDate.setReadOnly(true);
	     
		 final TextField lastEncounterDateText = new TextField("Ult. Consulta", "lastEncounterDate", 190);  
		 lastEncounterDateText.setId("editPatientLastEncounterDate");	
		 lastEncounterDateText.setReadOnly(true);
	     
	     
	     FieldDef[] cbFieldDefs = new FieldDef[] { 
	    		 new StringFieldDef("username"),
	    		 new StringFieldDef("name")
	     };

	     RecordDef cbRecordDef = new RecordDef(cbFieldDefs);

	     JsonReader cbReader = new JsonReader("response.value.items", cbRecordDef);

	     HttpProxy cbProxy = new HttpProxy("/semrs/patientServlet?patientAction=getProviders", Connection.GET);
	     final Store cbStore = new Store(cbProxy,cbReader,true);
	     cbStore.load();
	     
	     final TextField providerId = new TextField("providerId","providerId");
	     providerId.setId("patientEditproviderId");
	     providerId.setVisible(false);
	     
	     final ComboBox cb = new ComboBox();  
	     cb.setId("providerCB");
	     cb.setName("providerIdCB");
	     cb.setMinChars(1);  
	     cb.setFieldLabel("M&eacute;dico Tratante");  
	     cb.setStore(cbStore);
	     cb.setDisplayField("name");  
	     cb.setValueField("username");
	     cb.setMode(ComboBox.REMOTE);  
	     cb.setTriggerAction(ComboBox.ALL);  
	     cb.setEmptyText(" ");  
	     cb.setLoadingText("Buscando...");  
	     cb.setTypeAhead(true);  
	     cb.setSelectOnFocus(true);  
	     cb.setWidth(200);  
	     cb.setPageSize(10);
	     cb.addListener(new ComboBoxListenerAdapter(){
	         public void onSelect(ComboBox comboBox, Record record, int index){
	        	 providerId.setValue(record.getAsString("username"));
	         }
	       });
	     

	     
	     
	     final TextArea description = new TextArea("Observaciones", "description");  
	     description.setHideLabel(false);  
	     description.setWidth(190);
	     description.setHeight(90);
	     description.setAllowBlank(true);
	     description.setId("patientEditDesc");
	     
	     
	     
	     
	     final TextArea voidedTextArea = new TextArea("Raz&oacute;n egreso", "voidReasonText");  
		 voidedTextArea.setHideLabel(false);  
		 voidedTextArea.setWidth(190);
		 voidedTextArea.setHeight(80);
		 voidedTextArea.setReadOnly(true);
		 voidedTextArea.setId("patientEditVoidReason");
		 
		 final TextField voidDate = new TextField("Fecha egreso");
		 voidDate.setId("patientEditVoidedDate");
		 voidDate.setReadOnly(true);
			        
        	   
	     
	     secondColumn.add(textPatientId,  new AnchorLayoutData("60%"));
	     secondColumn.add(patientName, new AnchorLayoutData("94%"));  
	     secondColumn.add(patientLastName, new AnchorLayoutData("94%"));   
	     secondColumn.add(addressTextArea, new AnchorLayoutData("94%")); 
	     secondColumn.add(creationDate, new AnchorLayoutData("70%")); 
	     secondColumn.add(lastEncounterDateText, new AnchorLayoutData("70%")); 
	     secondColumn.add(cb, new AnchorLayoutData("90%")); 
	     secondColumn.add(description, new AnchorLayoutData("90%")); 
	     if(getVoided().equals("true")){
	       secondColumn.add(voidedTextArea, new AnchorLayoutData("94%")); 
	       secondColumn.add(voidDate, new AnchorLayoutData("60%"));
	     }
	     secondColumn.add(providerId, new AnchorLayoutData("90%")); 
	     topPanel.add(secondColumn, new ColumnLayoutData(0.40));  //0.5
	     

	     Panel thirdColumn = new Panel();  
	     thirdColumn.setLayout(new FormLayout());  
	     thirdColumn.setBorder(true);  
	     
	     
		 final TextField age =  new TextField("Edad", "age");
		 age.setId("patientEditAge");
		 age.setReadOnly(true);
	     
	     final DateField birthDate = new DateField("F. Nacimiento", "birthDate",190);  
	     birthDate.setId("patientBirthDateEdit");
	     birthDate.setAllowBlank(false);  
		 birthDate.setMaxValue(new Date());
		 birthDate.setReadOnly(true);

	
	     
	     
	     
	     Store sexStore = new SimpleStore(new String[]{"abbr", "sex"}, new String[][]{ new String[]{"M","Masculino"}, new String[]{"F", "Femenino"}});  
	     sexStore.load();  
			final ComboBox sexCB = new ComboBox();  
			sexCB.setFieldLabel("Sexo");  
			sexCB.setHiddenName("sex");  
			sexCB.setStore(sexStore);  
			sexCB.setDisplayField("sex");  
			sexCB.setTypeAhead(true);  
			sexCB.setMode(ComboBox.LOCAL);  
			sexCB.setTriggerAction(ComboBox.ALL);  
			sexCB.setSelectOnFocus(true);  
			sexCB.setWidth(190); 
			sexCB.setName("sex");
			sexCB.setAllowBlank(false);
			sexCB.setId("editPatientSex");
			sexCB.setReadOnly(true);
		
			Store bloodTypeStore = new SimpleStore(new String[]{"bloodtypev", "bloodtypet"}, 
					new String[][]{new String[]{""," "}, new String[]{"A+","A+"}, new String[]{"B+", "B+"},
					new String[]{"AB+", "AB+"}, new String[]{"O+", "O+"},new String[]{"A-", "A-"},
					new String[]{"B-", "B-"},new String[]{"AB-", "AB-"}, new String[]{"O-", "O-"}});  
			bloodTypeStore.load();  
			final ComboBox bloodTypeCB = new ComboBox();  
			bloodTypeCB.setFieldLabel("Tipo de Sangre");  
			bloodTypeCB.setHiddenName("bloodType");  
			bloodTypeCB.setStore(bloodTypeStore);  
			bloodTypeCB.setDisplayField("bloodtypet");  
			bloodTypeCB.setTypeAhead(true);  
			bloodTypeCB.setMode(ComboBox.LOCAL);  
			bloodTypeCB.setTriggerAction(ComboBox.ALL);  
			bloodTypeCB.setSelectOnFocus(true);  
			bloodTypeCB.setWidth(190); 
			bloodTypeCB.setName("bloodType");
			bloodTypeCB.setAllowBlank(true);
			bloodTypeCB.setReadOnly(true);
			sexCB.setId("editPatientBloodType");	
			
			
	     final TextField birthPlace = new TextField("Lugar de Nacimiento", "birthPlace", 190);  
		 birthPlace.setId("editPatientBirthPlace");	
		
	     final TextField phone = new TextField("Telef&oacute;no", "phoneNumber", 190);
	     phone.setId("editPatientPhone");
	     phone.setAllowBlank(false);
	     phone.setVtype(VType.ALPHANUM);
		 
		 final TextField mobile = new TextField("M&oacute;vil", "mobile", 190);
		 mobile.setId("editPatientMobile");
		 mobile.setVtype(VType.ALPHANUM);
		 	 
	     final TextField email = new TextField("Email", "email", 190);  
	     email.setVtype(VType.EMAIL);
	     email.setId("editPatientEmail");
	     
	     thirdColumn.add(birthDate,  new AnchorLayoutData("65%"));
	     thirdColumn.add(age, new AnchorLayoutData("65%"));  
	     thirdColumn.add(sexCB, new AnchorLayoutData("65%")); 	
	     thirdColumn.add(bloodTypeCB, new AnchorLayoutData("65%")); 
	     thirdColumn.add(birthPlace, new AnchorLayoutData("65%")); 	
	     thirdColumn.add(email, new AnchorLayoutData("65%")); 
	     thirdColumn.add(phone, new AnchorLayoutData("65%"));
	     thirdColumn.add(mobile, new AnchorLayoutData("65%")); 
	     topPanel.add(thirdColumn, new ColumnLayoutData(0.40));  //0.5
	   

				
			   
	     
	   if(getVoided().equals("false")){
	     final Button saveButton = new Button("Guardar");
			saveButton.setId("savePatientEditButton");
			saveButton.addListener(new ButtonListenerAdapter(){
				public void onClick(final Button button, EventObject e){
			     ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea guardar los cambios?", "Si", "No",  
	               new MessageBox.ConfirmCallback() {  
	                   public void execute(String btnID) {
	                      if(btnID.equals("yes")){

					MessageBox.show(new MessageBoxConfig() {  
						{  
							setMsg("Guardando los cambios, por favor espere...");  
							setProgressText("Guardando...");  
							setWidth(300);  
							setWait(true);  
							setWaitConfig(new WaitConfig() {  
								{  
									setInterval(200);  
								}  
							});  
							setAnimEl(button.getId());  
						}  
					});  
			
						RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,  "/semrs/patientServlet?patientAction=savePatient&isNew=false&"+formPanel.getForm().getValues());
						try {

							rb.sendRequest(null, new RequestCallback() {
								public void onResponseReceived(Request req, final Response res) {
									MessageBox.hide();  
			                        MessageBox.getDialog().close();
									if(res.getText().indexOf("errores") !=-1){
										MessageBox.show(new MessageBoxConfig() {  
				        					{  
				        						setTitle("Error");
				        						setMsg(res.getText());
				        						setIconCls(MessageBox.ERROR);
				        					    setModal(true);
				        					    setButtons(MessageBox.OK);
				        					}  
				        				});
				
										if(patientName.getValueAsString().equals("")){
											patientName.markInvalid("Este campo es obligatorio");
										}
										if(patientLastName.getValueAsString().equals("")){
											patientLastName.markInvalid("Este campo es obligatorio");
										}
										if(birthDate.getValueAsString().equals("")){
											birthDate.markInvalid("Este campo es obligatorio");
										}
										if(sexCB.getValueAsString().equals("")){
											sexCB.markInvalid("Este campo es obligatorio");
										}
										if(phone.getValueAsString().equals("")){
											phone.markInvalid("Este campo es obligatorio");
										}
										if(addressTextArea.getValueAsString().equals("")){
											addressTextArea.markInvalid("Este campo es obligatorio");
										}
									
									 }else if(res.getText().equals("")){
			                        	   MessageBox.hide();
				                           MessageBox.alert("Error", "Error interno"); 
			                           }else{
			                        	currentEncountersReloadFlag = false;
			                        	formStore.load();
										MainPanel.resetTimer();
										ListPatientsScreen.reloadFlag = true;
										if(getMainPanelStore()!=null){
          								  getMainPanelStore().reload();
          								}
										MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());  

									}

								}

								public void onError(Request req, Throwable exception) {
									MessageBox.hide();  
			                        MessageBox.getDialog().close();
									MessageBox.alert("Error", "Error interno"); 
								}

							});

						} catch (RequestException re) {
							MessageBox.hide();  
	                        MessageBox.getDialog().close();
							MessageBox.alert("Error", "Error interno"); 
						}
				
				}
	               	 }
       
             });
			  }
			});
			saveButton.setIconCls("save-icon");
			topPanel.addButton(saveButton);
			
			   newEncounterWindow= new Window();  
			   newEncounterWindow.setTitle("Nueva Consulta");  
			   newEncounterWindow.setId("patientEditNewEncounterWindow");
			   newEncounterWindow.setWidth(450);  
			   newEncounterWindow.setHeight(250);    
			   newEncounterWindow.setLayout(new FitLayout());  
			   newEncounterWindow.setPaddings(5);  
			   newEncounterWindow.setResizable(false);
			   newEncounterWindow.setButtonAlign(Position.CENTER);  
			   newEncounterWindow.setModal(true);
			   newEncounterWindow.setIconCls("encounter-icon");
			   newEncounterWindow.setCloseAction(Window.HIDE);
			   
			   //final TextField providerIdEncounter = new TextField();
			  // providerIdEncounter.setName("providerIdEncounter");
			   //providerIdEncounter.setId("providerIdEncounter");
			  // providerIdEncounter.setVisible(false);
			   final Hidden providerIdEncounter = new Hidden();
			   providerIdEncounter.setName("providerIdEncounter");
			     
			     final ComboBox newEncounterProviderCB = new ComboBox();  
			     newEncounterProviderCB.setId("newEncounterProviderCB");
			     newEncounterProviderCB.setName("providerIdCB2");
			     newEncounterProviderCB.setMinChars(1);  
			     newEncounterProviderCB.setFieldLabel("M&eacute;dico Tratante");  
			     newEncounterProviderCB.setStore(cbStore);
			     newEncounterProviderCB.setDisplayField("name");  
			     newEncounterProviderCB.setValueField("username");
			     newEncounterProviderCB.setMode(ComboBox.REMOTE);  
			     newEncounterProviderCB.setTriggerAction(ComboBox.ALL);  
			     newEncounterProviderCB.setEmptyText(" ");  
			     newEncounterProviderCB.setLoadingText("Buscando...");  
			     newEncounterProviderCB.setTypeAhead(true);  
			     newEncounterProviderCB.setSelectOnFocus(true);  
			     newEncounterProviderCB.setWidth(200);  
			     newEncounterProviderCB.setPageSize(10);
			     newEncounterProviderCB.addListener(new ComboBoxListenerAdapter(){
			         public void onSelect(ComboBox comboBox, Record record, int index){
			        	 providerIdEncounter.setValue(record.getAsString("username"));
			         }
			       });
			
			   
			     encounterForm.add(providerIdEncounter);
			     encounterForm.add(newEncounterProviderCB);

		        
		        MultiFieldPanel dateTimePanel = new MultiFieldPanel(); 
		        dateTimePanel.setId("newEncounterDateTimePanel");
		        final DateField dateField = new DateField("Fecha","encounterDate", 140);
		        dateField.setId("newEncounterDate");
		        dateField.setMinValue(new Date());
	            dateTimePanel.addToRow(dateField, 250);  
		        final TimeField timeField = new TimeField();
		        timeField.setId("newEncounterTime");
		        timeField.setName("encounterTime");
		        timeField.setIncrement(60);
		        timeField.setMinValue("8:00am");  
		        timeField.setMaxValue("6:00pm"); 
		        timeField.setHideLabel(true);  
		        timeField.setWidth(80);  
		        timeField.setReadOnly(true);
		        dateTimePanel.addToRow(timeField, new ColumnLayoutData(1));
		        encounterForm.add(dateTimePanel);
		        
			    final TextArea textAreaRefferral = new TextArea("Referido de", "refferral");  
			    textAreaRefferral.setId("patientEditTextAreaRefferral");
			    textAreaRefferral.setHideLabel(false);  
			    textAreaRefferral.setWidth(190);
			    textAreaRefferral.setHeight(80);
			    textAreaRefferral.setAllowBlank(true);
		        encounterForm.add(textAreaRefferral);
		        
		        final TextArea textAreaReason = new TextArea("Motivo de Consulta", "reason");  
		        textAreaReason.setId("patientEditTextAreaReason");
		        textAreaReason.setHideLabel(false);  
		        textAreaReason.setWidth(190);
		        textAreaReason.setHeight(80);
		        textAreaReason.setAllowBlank(true);
		        encounterForm.add(textAreaReason);
		        
		        
		        Panel encounterPanel = new Panel();  
		        encounterPanel.setLayout(new ColumnLayout());  
		        encounterPanel.setBorder(false); 
		        encounterPanel.setButtonAlign(Position.CENTER);
		        
		        
		        final Button saveEncounterButton = new Button("Guardar");
		        saveEncounterButton.setId("patientEditSaveEncounterButton");
		        saveEncounterButton.addListener(new ButtonListenerAdapter(){
		        	public void onClick(final Button button, EventObject e){
		        		ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea programar una nueva consulta para este paciente?", "Si", "No",  
			     	               new MessageBox.ConfirmCallback() {  
			     	                   public void execute(String btnID) {
			     	                      if(btnID.equals("yes")){
		        		MessageBox.show(new MessageBoxConfig() {  
		        			{  
		        				setMsg("Guardando los cambios, por favor espere...");  
		        				setProgressText("Guardando...");  
		        				setWidth(300);  
		        				setWait(true);  
		        				setWaitConfig(new WaitConfig() {  
		        					{  
		        						setInterval(200);  
		        					}  
		        				});  
		        				setAnimEl(button.getId());  
		        			}  
		        		});  
		        		
		        		 RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/encounterServlet?encounterAction=newEncounter&patientId="+getPatientId()+"&"+encounterForm.getForm().getValues());
		        		 try {
		        			
		                     rb.sendRequest(null, new RequestCallback() {
		                        public void onResponseReceived(Request req, final Response res) {
		                           MessageBox.hide();  
				                   MessageBox.getDialog().close();
		                           if(res.getText().indexOf("errores") !=-1){
		                        	  // MessageBox.alert("Error", res.getText()); 
		                       		MessageBox.show(new MessageBoxConfig() {  
			        					{  
			        						setTitle("Error");
			        						setMsg(res.getText());
			        						setIconCls(MessageBox.ERROR);
			        					    setModal(true);
			        					    setButtons(MessageBox.OK);
			        					}  
			        				});
		                           }else if(res.getText().equals("")){
		                        	   MessageBox.hide();
			                           MessageBox.alert("Error", "Error interno"); 
		                           }else{
		                        	   ListEncountersScreen.reloadFlag =true;
		                       	MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText() ,  
		                                 new MessageBox.AlertCallback() { 
									public void execute() {
									  MainPanel.resetTimer();
									  currentEncountersStore.reload();
		                              newEncounterWindow.hide();
									}  
                                });  
		                           }
		                        }
		                        
		                        public void onError(Request req, Throwable exception) {
		                           MessageBox.hide();  
				                  MessageBox.getDialog().close();
		                           MessageBox.alert("Error", "Error interno"); 
		                        }
		                        
		                     });
		                     
		                  } catch (RequestException re) {
		                	 MessageBox.hide();  
		                     MessageBox.getDialog().close();
		                	 MessageBox.alert("Error", "Error interno"); 
		                  }
		       
			     	                      }
			     	                   }
		        		});
		                  
		        	}
		        	
		        });
		        saveEncounterButton.setIconCls("save-icon");
		        encounterPanel.addButton(saveEncounterButton);
		        
		        Button cancelEncounterButton = new Button("Cancelar");
		        cancelEncounterButton.setId("patientEditCancelEncounterButton");
		        cancelEncounterButton.addListener(new ButtonListenerAdapter(){
		        	public void onClick(Button button, EventObject e){
		        		newEncounterWindow.hide();
		        	}
		        	
		        });
		        cancelEncounterButton.setIconCls("cancel-icon");
		        encounterPanel.addButton(cancelEncounterButton);  
		        encounterForm.add(encounterPanel);
			
		  

		        final Button newEncounterButton = new Button("Nueva Consulta");
				newEncounterButton.setId("newPatientEncounterButton");
				newEncounterButton.addListener(new ButtonListenerAdapter(){
					public void onClick(Button button, EventObject e) {
						newEncounterWindow.show();
	
	            	}  
				});
				newEncounterButton.setIconCls("add-icon");
				topPanel.addButton(newEncounterButton);
				newEncounterWindow.add(encounterForm); 
				
				    voidWindow= new Window();  
			        voidWindow.setTitle("Dar de Alta");  
			        voidWindow.setId("patientEditVoidwindow");
			        voidWindow.setWidth(450);  
			        voidWindow.setHeight(200);    
			        voidWindow.setLayout(new FitLayout());  
			        voidWindow.setPaddings(5);  
			        voidWindow.setResizable(false);
			        voidWindow.setButtonAlign(Position.CENTER);  
			        voidWindow.setModal(true);
			        voidWindow.setIconCls("void-icon");
			        voidWindow.addListener(new ContainerListenerAdapter() {
			            public void onResize(BoxComponent component, int adjWidth, int adjHeight, int rawWidth, int rawHeight) {
			            	voidWindow.getEl().center();
			            }
			        });
			        
			       
			        voidWindow.setCloseAction(Window.HIDE);  
			   

			       
			        
			        final TextArea textAreaVoid = new TextArea("Motivo", "voidReason");  
			        textAreaVoid.setId("patientEditTextAreaVoid");
			        textAreaVoid.setHideLabel(false);  
			        // anchor width by percentage and height by raw adjustment  
			        // sets width to 100% and height to "remainder" height - 53px  
			        textAreaVoid.setWidth(190);
			        textAreaVoid.setHeight(80);
			        textAreaVoid.setAllowBlank(false);
			        voidForm.add(textAreaVoid);
			        
			        Panel voidPanel = new Panel();  
			        voidPanel.setLayout(new ColumnLayout());  
			        voidPanel.setBorder(false); 
			        voidPanel.setButtonAlign(Position.CENTER);
			        
			        
			        final Button saveVoidButton = new Button("Guardar");
			        saveVoidButton.setId("patientEditSaveVoidButton");
			        saveVoidButton.addListener(new ButtonListenerAdapter(){
			        	public void onClick(final Button button, EventObject e){
			        		ExtendedMessageBox.confirmlg("Confirmar","Al dar de alta se eliminaran las consultas pendientes, Esta seguro que desea dar de alta a este paciente?", "Si", "No",  
				     	               new MessageBox.ConfirmCallback() {  
				     	                   public void execute(String btnID) {
				     	                      if(btnID.equals("yes")){
			        		MessageBox.show(new MessageBoxConfig() {  
			        			{  
			        				setMsg("Guardando los cambios, por favor espere...");  
			        				setProgressText("Guardando...");  
			        				setWidth(300);  
			        				setWait(true);  
			        				setWaitConfig(new WaitConfig() {  
			        					{  
			        						setInterval(200);  
			        					}  
			        				});  
			        				setAnimEl(button.getId());  
			        			}  
			        		});  
			        		
			        		 RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/patientServlet?patientAction=changeStatus&status=void&id="+getPatientId()+"&voidReason="+textAreaVoid.getValueAsString());
			        		 try {
			        			
			                     rb.sendRequest(null, new RequestCallback() {
			                        public void onResponseReceived(Request req, final Response res) {
			                           MessageBox.hide();  
					                   MessageBox.getDialog().close();
			                           if(res.getText().indexOf("errores") !=-1){
			                        	   if(res.getText().indexOf("ya") !=-1){
			                       			   MessageBox.show(new MessageBoxConfig() {  
			                					{  
			                						setTitle("Error");
			                						setMsg(res.getText());
			                						setIconCls(MessageBox.ERROR);
			                					    setModal(true);
			                					    setButtons(MessageBox.OK);
			                					    setCallback(new MessageBox.PromptCallback() { 
			        								public void execute(
			        										String btnID,
			        										String text) {
			        									ListPatientsScreen.reloadFlag = true;
			        									MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
			        									
			        								}  
			                                        });  
			                					}  
			                				});
			                        	   }else{
			                        			MessageBox.show(new MessageBoxConfig() {  
						        					{  
						        						setTitle("Error");
						        						setMsg(res.getText());
						        						setIconCls(MessageBox.ERROR);
						        					    setModal(true);
						        					    setButtons(MessageBox.OK);
						        					}  
						        				});
			                        	     //MessageBox.alert("Error", res.getText()); 
			                        	   }
			                           }else if(res.getText().equals("")){
			                        	   MessageBox.hide();
				                           MessageBox.alert("Error", "Error interno"); 
			                           }else{
			                        	   ListPatientsScreen.reloadFlag = true;
			                        	   ListEncountersScreen.reloadFlag =true;
			                        		if(getMainPanelStore()!=null){
              								  getMainPanelStore().reload();
              								}
			                        		MessageBox.show(new MessageBoxConfig() {  
        	                					{  
        	                						setTitle("Operaci&oacute;n Ex&iacute;tosa");
        	                						setIconCls(MessageBox.INFO);
        	                						setMsg(res.getText());  
        	                					    setModal(true);
        	                					    setButtons(MessageBox.OK);
        	                					    setCallback(new MessageBox.PromptCallback() { 
													public void execute(
															String btnID,
															String text) {
														MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
														
													}  
                                                    });  
        	                					}  
        	                				});   
			                           }
			                        }
			                        
			                        public void onError(Request req, Throwable exception) {
			                           MessageBox.hide();  
					                      MessageBox.getDialog().close();
			                           MessageBox.alert("Error", "Error interno"); 
			                        }
			                        
			                     });
			                     
			                  } catch (RequestException re) {
			                	 MessageBox.hide();  
			                     MessageBox.getDialog().close();
			                	 MessageBox.alert("Error", "Error interno"); 
			                  }
			                  
			               ////
				     	                      }
				     	                   }
			        		});
			                  
			        	}
			        	
			        });
			        saveVoidButton.setIconCls("save-icon");
			        voidPanel.addButton(saveVoidButton);
			        
			        Button cancelVoidButton = new Button("Cancelar");
			        cancelVoidButton.setId("patientEditCancelVoidButton");
			        cancelVoidButton.addListener(new ButtonListenerAdapter(){
			        	public void onClick(Button button, EventObject e){
			        		voidWindow.hide();
			        		//.hide();
			        	}
			        	
			        });
			        cancelVoidButton.setIconCls("cancel-icon");
			        voidPanel.addButton(cancelVoidButton);  
			        
			        voidForm.add(voidPanel);
				

				
				final Button voidButton = new Button("Dar de alta");
				voidButton.setId("patientEditVoidButton");
				voidButton.addListener(new ButtonListenerAdapter(){
					public void onClick(Button button, EventObject e) {
						  voidWindow.show();
	            	}  
				});
				voidButton.setIconCls("void-icon");
				topPanel.addButton(voidButton);
				voidWindow.add(voidForm); 
				
	      }else{
	    	  
	    	  final Button activateButton = new Button("Activar");
	    	  activateButton.setId("patientEditActivateButton");
	    	  activateButton.addListener(new ButtonListenerAdapter(){
					public void onClick(final Button button, EventObject e) {
						
						ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea activar este paciente?", "Si", "No",  
		                        new MessageBox.ConfirmCallback() {  
		                            public void execute(String btnID) {
		                           	 if(btnID.equals("yes")){
		                           		MessageBox.show(new MessageBoxConfig() {  
		                					{  
		                						setMsg("Activando registro, por favor espere...");  
		                						setProgressText("Eliminando...");  
		                						setWidth(300);  
		                						setWait(true);  
		                						setWaitConfig(new WaitConfig() {  
		                							{  
		                								setInterval(200);  
		                							}  
		                						});  
		                						setAnimEl(button.getId());  
		                					}  
		                				});  


		                				try {
		                					final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/patientServlet?patientAction=changeStatus&status=active&id="+getPatientId());
		                					rb.sendRequest(null, new RequestCallback() {
		                						public void onResponseReceived(Request req, final Response res) {
		                							MessageBox.hide();  
		        			                        MessageBox.getDialog().close(); 
		                							if(res.getText().indexOf("errores") !=-1){
		                							  	   if(res.getText().indexOf("ya") !=-1){
		        			                       			   MessageBox.show(new MessageBoxConfig() {  
		        			                					{  
		        			                						setTitle("Error");
		        			                						setMsg(res.getText());
		        			                						setIconCls(MessageBox.ERROR);
		        			                					    setModal(true);
		        			                					    setButtons(MessageBox.OK);
		        			                					    setCallback(new MessageBox.PromptCallback() { 
		        			        								public void execute(
		        			        										String btnID,
		        			        										String text) {
		        			        									ListPatientsScreen.reloadFlag = true;
		        			        									MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
		        			        									
		        			        								}  
		        			                                        });  
		        			                					}  
		        			                				});
		        			                        	   }else{
		        			                        			MessageBox.show(new MessageBoxConfig() {  
		        						        					{  
		        						        						setTitle("Error");
		        						        						setMsg(res.getText());
		        						        						setIconCls(MessageBox.ERROR);
		        						        					    setModal(true);
		        						        					    setButtons(MessageBox.OK);
		        						        					}  
		        						        				});
		        			                        	     //MessageBox.alert("Error", res.getText()); 
		        			                        	   }
		                							 }else if(res.getText().equals("")){
		          		                        	   MessageBox.hide();
		          			                           MessageBox.alert("Error", "Error interno"); 
		          		                           }else{
		                								MainPanel.resetTimer();
		                								ListPatientsScreen.reloadFlag = true;
		                								if(getMainPanelStore()!=null){
			                								  getMainPanelStore().reload();
			                								}
		                								MessageBox.show(new MessageBoxConfig() {  
		            	                					{  
		            	                						setTitle("Operaci&oacute;n Ex&iacute;tosa");
		            	                						setIconCls(MessageBox.INFO);
		            	                						setMsg(res.getText());  
		            	                					    setModal(true);
		            	                					    setButtons(MessageBox.OK);
		            	                					    setCallback(new MessageBox.PromptCallback() { 
																public void execute(
																		String btnID,
																		String text) {
																	MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
																	
																}  
		                                                        });  
		            	                					}  
		            	                				}); 
		                								
		                							}


		                						}

		                						public void onError(Request req, Throwable exception) {
		                							MessageBox.hide();  
		        			                        MessageBox.getDialog().close();
		                							MessageBox.alert("Error", "Error interno"); 
		                						}

		                					});

		                				} catch (RequestException re) {
		                					MessageBox.hide();  
					                        MessageBox.getDialog().close();
		                					MessageBox.alert("Error", "Error interno"); 
		                				}
		                           	 }
		                            }  
		                        });  

	            	}  
				});
	    	  activateButton.setIconCls("activate-icon");
	    	  topPanel.addButton(activateButton);
	    	  
	      }
	   
		if(getLastEncounterDate().equals("")){
			final Button delete = new Button("Eliminar");
			delete.setId("deletePatientEditButton");
			delete.addListener(new ButtonListenerAdapter(){
				public void onClick(final Button button, EventObject e){
					
					ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea eliminar este paciente?", "Si", "No",  
	                        new MessageBox.ConfirmCallback() {  
	                            public void execute(String btnID) {
	                           	 if(btnID.equals("yes")){
	                           		MessageBox.show(new MessageBoxConfig() {  
	                					{  
	                						setMsg("Eliminando registro, por favor espere...");  
	                						setProgressText("Eliminando...");  
	                						setWidth(300);  
	                						setWait(true);  
	                						setWaitConfig(new WaitConfig() {  
	                							{  
	                								setInterval(200);  
	                							}  
	                						});  
	                						setAnimEl(button.getId());  
	                					}  
	                				});  


	                				try {
	                					final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/patientServlet?patientAction=deletePatient&patientId="+getPatientId());
	                					rb.sendRequest(null, new RequestCallback() {
	                						public void onResponseReceived(Request req, final Response res) {
	                							MessageBox.hide();  
	        			                        MessageBox.getDialog().close(); 
	                							if(res.getText().indexOf("errores") !=-1){
	                							  	   if(res.getText().indexOf("existe") !=-1){
	        			                       			   MessageBox.show(new MessageBoxConfig() {  
	        			                					{  
	        			                						setTitle("Error");
	        			                						setMsg(res.getText());
	        			                						setIconCls(MessageBox.ERROR);
	        			                					    setModal(true);
	        			                					    setButtons(MessageBox.OK);
	        			                					    setCallback(new MessageBox.PromptCallback() { 
	        			        								public void execute(
	        			        										String btnID,
	        			        										String text) {
	        			        									ListPatientsScreen.reloadFlag = true;
	        			        									MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
	        			        									
	        			        								}  
	        			                                        });  
	        			                					}  
	        			                				});
	        			                        	   }else{
	        			                        	    // MessageBox.alert("Error", res.getText()); 
	        			                        			MessageBox.show(new MessageBoxConfig() {  
	        						        					{  
	        						        						setTitle("Error");
	        						        						setMsg(res.getText());
	        						        						setIconCls(MessageBox.ERROR);
	        						        					    setModal(true);
	        						        					    setButtons(MessageBox.OK);
	        						        					}  
	        						        				});
	        			                        	   } 
	                							 }else if(res.getText().equals("")){
	          		                        	   MessageBox.hide();
	          			                           MessageBox.alert("Error", "Error interno"); 
	          		                           }else{
	                								MainPanel.resetTimer();
	                								ListPatientsScreen.reloadFlag = true;
	                								ListEncountersScreen.reloadFlag =true;
	                								if(getMainPanelStore()!=null){
	                								  getMainPanelStore().reload();
	                								}
	                								MessageBox.show(new MessageBoxConfig() {  
	            	                					{  
	            	                						setTitle("Operaci&oacute;n Ex&iacute;tosa");
	            	                						setIconCls(MessageBox.INFO);
	            	                						setMsg(res.getText());  
	            	                					    setModal(true);
	            	                					    setButtons(MessageBox.OK);
	            	                					    setCallback(new MessageBox.PromptCallback() { 
															public void execute(
																	String btnID,
																	String text) {
																MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
																
															}  
	                                                        });  
	            	                					}  
	            	                				});  
	                								/*
	                								MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText() ,  
	                		                                 new MessageBox.AlertCallback() { 
	                									public void execute() {
	                										MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
	                									}  
	                                                 });  
	                                                 */
	                								
	                							}


	                						}

	                						public void onError(Request req, Throwable exception) {
	                							MessageBox.hide();  
	        			                        MessageBox.getDialog().close();
	                							MessageBox.alert("Error", "Error interno"); 
	                						}

	                					});

	                				} catch (RequestException re) {
	                					MessageBox.hide();  
				                        MessageBox.getDialog().close();
	                					MessageBox.alert("Error", "Error interno"); 
	                				}
	                           	 }
	                            }  
	                        });  

				

				}

			});
			delete.setIconCls("delete-icon");
	    	topPanel.addButton(delete);
		}else{
			
			Button report = new Button("Exportar Historia");
			report.setId("reportPatientDetailButton");
			report.addListener(new ButtonListenerAdapter(){
	        	public void onClick(Button button, EventObject e){
	        		com.google.gwt.user.client.Window.open("/semrs/patientServlet?patientAction=exportRecord&patientId="+getPatientId(), "_self", ""); 
	        	}
	        	
	        });
			report.setIconCls("acrobat-icon");
			topPanel.addButton(report);
		}
			
		
			
			Button cancel = new Button("Cancelar");
			cancel.setId("cancelPatientDetailButton");
	        cancel.addListener(new ButtonListenerAdapter(){
	        	public void onClick(Button button, EventObject e){
	        		MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
	        	}
	        	
	        });
	        cancel.setIconCls("cancel-icon");
	        topPanel.addButton(cancel); 
	     

	     formPanel.add(topPanel); 
	     
	   
	     
	 /*First*/
        
              
        
        GridView currentEncountersView = new GridView(){
     	   public String getRowClass(Record record, int index, RowParams rowParams, Store store) {
     		   if(record.getAsString("edit").startsWith("f")){
     			   return "redClass";
     		   }
     		   return "";
            }
        };
        currentEncountersView.setEmptyText("No hay Registros");
        currentEncountersView.setAutoFill(true);
        currentEncountersView.setForceFit(true);

        
        final ToolbarButton deleteEncounterButton = new ToolbarButton("Eliminar Consulta");
        deleteEncounterButton.setIconCls("delete-icon");
        deleteEncounterButton.setDisabled(true); 
        
        final GridPanel currentEncountersGrid = new GridPanel(currentEncountersStore, createCurrentEncountersColModel());
        //currentEncountersGrid.setId("patientEditCurrentEncountersGrid");
        currentEncountersGrid.setEnableDragDrop(false);
        currentEncountersGrid.setWidth(920);
        currentEncountersGrid.setHeight(550);
        currentEncountersGrid.setTitle("Lista de Consultas Activas");
        currentEncountersGrid.setLoadMask(true);  
        currentEncountersGrid.setSelectionModel(new RowSelectionModel());  
        currentEncountersGrid.setFrame(true);  
        currentEncountersGrid.setView(currentEncountersView);
        currentEncountersGrid.addGridCellListener(new GridCellListener() {  

			
			public void onCellDblClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
			    Record r = grid.getStore().getAt(rowIndex);
			    String edit = r.getAsString("edit");
			    ShowcasePanel.adminEncounterScreen.setEncounterId(r.getAsString("id"));
			    ShowcasePanel.adminEncounterScreen.flag1 = false;
			    if(edit.startsWith("t")){
			    	ShowcasePanel.adminEncounterScreen.setEdit(true);
			    	ShowcasePanel.adminEncounterScreen.setCurrentEncountersStore(currentEncountersStore);
			    	ShowcasePanel.adminEncounterScreen.setPreviousEncountersStore(previousEncountersStore);
			    	showScreen(getTabPanel(),ShowcasePanel.adminEncounterScreen, "Editar Consulta","encounter-icon","admEncounterDetail");
			    }else{
			    	 MessageBox.show(new MessageBoxConfig() {  
     					{  
     						setTitle("Atenci&oacute;n");
     						setMsg("No se encuentra autorizado para editar esta consulta");
     						setIconCls(MessageBox.WARNING);
     					    setModal(true);
     					    setButtons(MessageBox.OK); 
     					}  
     				});
			    }
			}

			
			public void onCellClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
		      
				final Record r = grid.getStore().getAt(rowIndex);
				String recordId = r.getAsString("id");
				deleteEncounterButton.setDisabled(false);
				deleteEncounterButton.setId(recordId);

				
			}


			public void onCellContextMenu(GridPanel grid, int rowIndex,
					int cellIndex, EventObject e) {
				// TODO Auto-generated method stub
				
			}
            });  
        
       
        currentEncountersPagingToolbar.setPageSize(10);
        currentEncountersPagingToolbar.setDisplayInfo(true);
        currentEncountersPagingToolbar.setEmptyMsg("No hay registros");
  

        NumberField currentEncountersPageSizeField = new NumberField();
        //currentEncountersPageSizeField.setId("currentEncountersPageSizeField");
        currentEncountersPageSizeField.setWidth(40);
        currentEncountersPageSizeField.setSelectOnFocus(true);
        currentEncountersPageSizeField.addListener(new FieldListenerAdapter() {
            public void onSpecialKey(Field field, EventObject e) {
                if (e.getKey() == EventObject.ENTER) {
                    int pageSize = Integer.parseInt(field.getValueAsString());
                    currentEncountersPagingToolbar.setPageSize(pageSize);
                }
            }
        });

        ToolTip currentEncountersToolTip = new ToolTip("Introduzca el tama&ntilde;o de p&aacute;gina");
        //currentEncountersToolTip.setId("currentEncountersToolTip");
        currentEncountersToolTip.applyTo(currentEncountersPageSizeField);
        
        currentEncountersPagingToolbar.addField(currentEncountersPageSizeField);
        currentEncountersPagingToolbar.addSeparator();
        
		deleteEncounterButton.addListener(new ButtonListenerAdapter() {  
			public void onClick(final Button button, EventObject e) {
				if(!button.getId().equals("")){
					ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea eliminar esta consulta?", "Si", "No",  
							new MessageBox.ConfirmCallback() {  
						public void execute(String btnID) {
							if(btnID.equals("yes")){
								MessageBox.show(new MessageBoxConfig() {  
									{  
										setMsg("Eliminando registro, por favor espere...");  
										setProgressText("Eliminando...");  
										setWidth(300);  
										setWait(true);  
										setWaitConfig(new WaitConfig() {  
											{  
												setInterval(200);  
											}  
										});  
										setAnimEl(button.getId());  
									}  
								});  

								final RequestBuilder deleteRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/encounterServlet?encounterAction=deleteEncounter&encounterId="+button.getId());
								try {

									deleteRequest.sendRequest(null, new RequestCallback() {
										public void onResponseReceived(Request req, final Response res) {
											MessageBox.hide();  
											if(res.getText().indexOf("errores") !=-1){
												//MessageBox.alert("Error", res.getText()); 
												MessageBox.show(new MessageBoxConfig() {  
						        					{  
						        						setTitle("Error");
						        						setMsg(res.getText());
						        						setIconCls(MessageBox.ERROR);
						        					    setModal(true);
						        					    setButtons(MessageBox.OK);
						        					}  
						        				});
											}else if(res.getText().equals("")){
												MessageBox.hide();
												MessageBox.alert("Error", "Error interno"); 
											}else{
												deleteEncounterButton.setId("");
												button.setDisabled(true);
												ListEncountersScreen.reloadFlag =true;
												MainPanel.resetTimer();
												MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());  
												currentEncountersStore.reload();
											}


										}

										public void onError(Request req, Throwable exception) {
											MessageBox.hide();
											MessageBox.alert("Error", "Error interno"); 
										}

									});

								} catch (RequestException re) {
									MessageBox.hide();
									MessageBox.alert("Error", "Error interno"); 

								}
							}
						}  
					});  

				}
			}  
		});  
		currentEncountersPagingToolbar.addButton(deleteEncounterButton);
		currentEncountersPagingToolbar.addSeparator();
        
        
        ToolbarButton currentEncounterExportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
        	public void onClick(Button button, EventObject e) {
        		com.google.gwt.user.client.Window.open("/semrs/encounterServlet?encounterAction=exportEncounters&current=true", "_self", ""); 

        	}  
        });  
        currentEncounterExportButton.setIconCls("excel-icon");
        currentEncountersPagingToolbar.addButton(currentEncounterExportButton);
        currentEncountersPagingToolbar.addSeparator();
        currentEncountersPagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
        currentEncountersGrid.setBottomToolbar(currentEncountersPagingToolbar);
        firstTab.add(currentEncountersGrid);
        firstTab.doLayout();

  

        /***Previous***/
        
       
         GridView previousEncountersView = new GridView();
         previousEncountersView.setEmptyText("No hay Registros");
         previousEncountersView.setAutoFill(true);
         previousEncountersView.setForceFit(true);

         
         final GridPanel previousEncountersGrid = new GridPanel(previousEncountersStore, createPreviousEncountersColModel());
         //previousEncountersGrid.setId("patientEditPreviousEncountersGrid");
         previousEncountersGrid.setEnableDragDrop(false);
         previousEncountersGrid.setWidth(920);
         previousEncountersGrid.setHeight(550);
         previousEncountersGrid.setTitle("Lista de Consultas Pasadas");
         previousEncountersGrid.setLoadMask(true);  
         previousEncountersGrid.setSelectionModel(new RowSelectionModel());  
         previousEncountersGrid.setFrame(true);  
         previousEncountersGrid.setView(previousEncountersView);
         previousEncountersGrid.addGridCellListener(new GridCellListener() {  

 			
 			public void onCellDblClick(GridPanel grid, int rowIndex,
 					int colIndex, EventObject e) {
 			    Record r = grid.getStore().getAt(rowIndex);
 			    String id = r.getAsString("id");
 			   ShowcasePanel.adminEncounterScreen.flag1 = false;
 			    ShowcasePanel.adminEncounterScreen.setEncounterId(r.getAsString("id"));
		    	ShowcasePanel.adminEncounterScreen.setEdit(false);
		    	showScreen(getTabPanel(),ShowcasePanel.adminEncounterScreen, "Editar Consulta","encounter-icon","admEncounterDetail");
 			    
 			}

 			
 			public void onCellClick(GridPanel grid, int rowIndex,
 					int colIndex, EventObject e) {
 		      
 				
 			}


 			public void onCellContextMenu(GridPanel grid, int rowIndex,
 					int cellIndex, EventObject e) {
 				// TODO Auto-generated method stub
 				
 			}
             });  
         
        
         previousEncountersPagingToolbar.setPageSize(10);
         previousEncountersPagingToolbar.setDisplayInfo(true);
         previousEncountersPagingToolbar.setEmptyMsg("No hay registros");
   

         NumberField previousEncountersPageSizeField = new NumberField();
         //previousEncountersPageSizeField.setId("previousEncountersPageSizeField");
         previousEncountersPageSizeField.setWidth(40);
         previousEncountersPageSizeField.setSelectOnFocus(true);
         previousEncountersPageSizeField.addListener(new FieldListenerAdapter() {
             public void onSpecialKey(Field field, EventObject e) {
                 if (e.getKey() == EventObject.ENTER) {
                     int pageSize = Integer.parseInt(field.getValueAsString());
                     previousEncountersPagingToolbar.setPageSize(pageSize);
                 }
             }
         });

         ToolTip previousEncountersToolTip = new ToolTip("Introduzca el tama&ntilde;o de p&aacute;gina");
         //previousEncountersToolTip.setId("previousEncountersToolTip");
         previousEncountersToolTip.applyTo(previousEncountersPageSizeField);
         
         previousEncountersPagingToolbar.addField(previousEncountersPageSizeField);
         previousEncountersPagingToolbar.addSeparator();
         ToolbarButton previousEncountersExportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
         	public void onClick(Button button, EventObject e) {
         		com.google.gwt.user.client.Window.open("/semrs/encounterServlet?encounterAction=exportEncounters&current=false", "_self", ""); 

         	}  
         });  
         //previousEncountersExportButton.setId("previousEncountersExportButton");
         previousEncountersExportButton.setIconCls("excel-icon");
         previousEncountersPagingToolbar.addButton(previousEncountersExportButton);
         previousEncountersPagingToolbar.addSeparator();
         previousEncountersPagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
         previousEncountersGrid.setBottomToolbar(previousEncountersPagingToolbar);
         try{
         previousEncountersGrid.addListener(new PanelListenerAdapter() {  
                      public void onRender(Component component) {  
                     	 previousEncountersStore.load(0, previousEncountersPagingToolbar.getPageSize()); 
                      }  
                  }); 
         }catch(Throwable t){
        	 com.google.gwt.user.client.Window.alert("Error ading listener to grid " + t);
         }
       
       // secondTabFormPanel.add(previousEncountersGrid);
       // secondTabFormPanel.doLayout();
        secondTab.add(previousEncountersGrid);
        secondTab.doLayout();
        
        
        
        /*Portal tab*/
        
      
   
      
        ColumnModel treatmentsModel = new ColumnModel(new ColumnConfig[] { 
                new ColumnConfig("C&oacute;digo", "id"), 
                new ColumnConfig("Nombre", "name"),
                new ColumnConfig("Fecha Inicio", "startDate"),
                new ColumnConfig("Fecha F&iacute;n", "endDate"),
                new ColumnConfig("Instrucciones", "instructions"),
                new ColumnConfig("Fecha de Consulta","encounterDate"),
                new ColumnConfig("M&eacute;dico Tratante", "encounterProvider"),

        		
        });
        for (int i = 0; i < treatmentsModel.getColumnConfigs().length; i++){
            ((ColumnConfig) treatmentsModel.getColumnConfigs()[i]).setSortable(true);
        }
        
        
        
        ColumnModel diagnosesModel = new ColumnModel(new ColumnConfig[] { 
                new ColumnConfig("C&oacute;digo", "id"), 
                new ColumnConfig("Nombre", "name"),
                new ColumnConfig("Severidad", "severity"),
                new ColumnConfig("Tipo", "type"),
                new ColumnConfig("Fecha de Consulta","encounterDate"),
                new ColumnConfig("M&eacute;dico Tratante", "encounterProvider"),

        		
        });
        for (int i = 0; i < diagnosesModel.getColumnConfigs().length; i++){
            ((ColumnConfig) diagnosesModel.getColumnConfigs()[i]).setSortable(true);
        }
        
        
        ColumnModel labTestsModel = new ColumnModel(new ColumnConfig[] { 
                new ColumnConfig("C&oacute;digo", "id"), 
                new ColumnConfig("Nombre", "name"),
                new ColumnConfig("Resultado", "result"),
                new ColumnConfig("Observaciones", "resultDesc"),
                new ColumnConfig("Fecha de Ex&aacute;men", "date"),
                new ColumnConfig("Fecha de Consulta","encounterDate"),
                new ColumnConfig("M&eacute;dico Tratante", "encounterProvider"),

        		
        });
        for (int i = 0; i < labTestsModel.getColumnConfigs().length; i++){
            ((ColumnConfig) labTestsModel.getColumnConfigs()[i]).setSortable(true);
        }
        
        ColumnModel symptomsModel = new ColumnModel(new ColumnConfig[] { 
                new ColumnConfig("C&oacute;digo", "id"), 
                new ColumnConfig("Nombre", "name"),
                new ColumnConfig("Tipo", "type"),
                new ColumnConfig("Severidad", "severity"),
                new ColumnConfig("Fecha de Consulta","encounterDate"),
                new ColumnConfig("M&eacute;dico Tratante", "encounterProvider"),

        		
        });
        for (int i = 0; i < symptomsModel.getColumnConfigs().length; i++){
            ((ColumnConfig) symptomsModel.getColumnConfigs()[i]).setSortable(true);
        }
        
        ColumnModel proceduresComplicationsModel = new ColumnModel(new ColumnConfig[] { 
                new ColumnConfig("C&oacute;digo", "id"), 
                new ColumnConfig("Nombre", "name"),
                new ColumnConfig("Fecha", "date"),
                new ColumnConfig("Fecha de Consulta","encounterDate"),
                new ColumnConfig("M&eacute;dico Tratante", "encounterProvider"),

        		
        });
        for (int i = 0; i < proceduresComplicationsModel.getColumnConfigs().length; i++){
            ((ColumnConfig) proceduresComplicationsModel.getColumnConfigs()[i]).setSortable(true);
        }
        


        
        Portal portal = new Portal();  
        portal.addListener(new PanelListenerAdapter() {  
            public void onRender(Component component) {  
            	latestTreatmentsStore.load();
            	latestDiagnosesStore.load();
            	latestLabTestsStore.load();
            	latestSymptomsStore.load();
            	latestComplicationsStore.load();
            	latestProceduresStore.load();
            	
             }  
         }); 
        
        portal.setAutoScroll(true);

        //create portal columns  
        PortalColumn firstCol = new PortalColumn();  
        firstCol.setPaddings(10, 10, 0, 10);  

        //add portlets to portal columns  
        final Portlet treatmentsPortlet = new Portlet();  
        treatmentsPortlet.setTitle("&Uacute;ltimos 5 Tratamientos");  
        treatmentsPortlet.setLayout(new FitLayout());  
        treatmentsPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			   public void execute() {
				   latestTreatmentsStore.reload();
				   }
				}));
        treatmentsPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {

				treatmentsPortlet.hide();
			
			}
		})); 
        treatmentsPortlet.add(getPortalGrid(latestTreatmentsStore,treatmentsModel,getTabPanel()));  
        firstCol.add(treatmentsPortlet);  
        
        final Portlet diagnosesPortlet = new Portlet();  
        diagnosesPortlet.setTitle("&Uacute;ltimos 5 Diagnosticos");  
        diagnosesPortlet.setLayout(new FitLayout());  
        diagnosesPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			   public void execute() {
				   latestDiagnosesStore.reload();
				   }
				}));
        diagnosesPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {

				diagnosesPortlet.hide();
			
			}
		})); 
        diagnosesPortlet.add( getPortalGrid(latestDiagnosesStore,diagnosesModel,getTabPanel()));  
        firstCol.add(diagnosesPortlet);  

        //add portal column to portal  
        portal.add(firstCol, new ColumnLayoutData(.33));  

        //another column  
        PortalColumn secondCol = new PortalColumn();  
        secondCol.setPaddings(10, 10, 0, 10);  
        
        final Portlet labTestsPortlet = new Portlet();  
        labTestsPortlet.setTitle("&Uacute;ltimos 5 Ex&aacute;menes");  
        labTestsPortlet.setLayout(new FitLayout());  
        labTestsPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			   public void execute() {
				   latestLabTestsStore.reload();
				   }
				}));
        labTestsPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {

				labTestsPortlet.hide();
			
			}
		})); 
        labTestsPortlet.add(getPortalGrid(latestLabTestsStore ,labTestsModel,getTabPanel()));  
        
        final Portlet symptomsPortlet = new Portlet();  
        symptomsPortlet.setTitle("&Uacute;ltimos 5 S&iacute;ntomas");  
        symptomsPortlet.setLayout(new FitLayout());  
        symptomsPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			   public void execute() {
				   latestSymptomsStore.reload();
				   }
				}));
        symptomsPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {

				symptomsPortlet.hide();
			
			}
		}));   
        symptomsPortlet.add(getPortalGrid(latestSymptomsStore ,symptomsModel,getTabPanel()));  
        
        secondCol.add(labTestsPortlet);  
        secondCol.add(symptomsPortlet);  
        portal.add(secondCol, new ColumnLayoutData(.33));  

        //third column  
        PortalColumn thirdCol = new PortalColumn();  
        thirdCol.setPaddings(10, 10, 0, 10);  
        
        final Portlet complicationsPortlet = new Portlet();  
        complicationsPortlet.setTitle("&Uacute;ltimas 5 Complicaciones");  
        complicationsPortlet.setLayout(new FitLayout());  
        complicationsPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			   public void execute() {
				   latestComplicationsStore.reload();
				   }
				}));
        complicationsPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {

				complicationsPortlet.hide();
			
			}
		}));   
        complicationsPortlet.add(getPortalGrid(latestComplicationsStore ,proceduresComplicationsModel,getTabPanel()));  
        
        final Portlet proceduresPortlet = new Portlet();  
        proceduresPortlet.setTitle("&Uacute;ltimos 5 Procedimientos");  
        proceduresPortlet.setLayout(new FitLayout());  
        proceduresPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			   public void execute() {
				   latestProceduresStore.reload();
				   }
				}));
        proceduresPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {

				proceduresPortlet.hide();
			
			}
		}));    
        proceduresPortlet.add(getPortalGrid(latestProceduresStore,proceduresComplicationsModel,getTabPanel()));  
        

        thirdCol.add(complicationsPortlet);  
        thirdCol.add(proceduresPortlet);  
        portal.add(thirdCol, new ColumnLayoutData(.33));  
           
       thirdTab.add(portal);
       thirdTab.doLayout();
        
        
        
        /////////////////
        
        
        patientTabpanel.add(firstTab); 
        patientTabpanel.add(secondTab);
        patientTabpanel.add(thirdTab);
        patientTabpanel.doLayout();
	
      //  com.google.gwt.user.client.Window.alert("adding patientTabpanel" + formPanel.toString() + " " + patientTabpanel.toString());
        formPanel.add(patientTabpanel);
		

        
        
        formStore.load();

		formStore.addStoreListener(new StoreListenerAdapter(){

			public void onLoad(Store store, Record[] records) {
				

				for(int i=0;i<records.length;i++){
					if(store.getRecordAt(i).getAsString("loadSuccess").trim().equals("false")){
						MessageBox.show(new MessageBoxConfig() {  
        					{  
        						setTitle("Error");
        						setMsg("Este paciente no existe");
        						setIconCls(MessageBox.ERROR);
        					    setModal(true);
        					    setButtons(MessageBox.OK);
        					    setCallback(new MessageBox.PromptCallback() { 
								public void execute(
										String btnID,
										String text) {
									ListPatientsScreen.reloadFlag = true;
									MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
									
								}  
                                });  
        					}  
        				});
						break;	
					}else{
						textPatientId.setValue(store.getRecordAt(i).getAsString("id"));
						patientName.setValue(store.getRecordAt(i).getAsString("name"));
						patientLastName.setValue(store.getRecordAt(i).getAsString("lastName"));
						addressTextArea.setValue(store.getRecordAt(i).getAsString("address"));
						phone.setValue(store.getRecordAt(i).getAsString("phoneNumber"));
						mobile.setValue(store.getRecordAt(i).getAsString("mobile"));
						birthDate.setValue(store.getRecordAt(i).getAsString("birthDate"));
						sexCB.setValue(store.getRecordAt(i).getAsString("sex"));
						email.setValue(store.getRecordAt(i).getAsString("email"));
						birthPlace.setValue(store.getRecordAt(i).getAsString("birthPlace"));
						lastEncounterDateText.setValue(store.getRecordAt(i).getAsString("lastEncounterDate"));
						creationDate.setValue(store.getRecordAt(i).getAsString("creationDate"));
						cb.setValue(store.getRecordAt(i).getAsString("provider"));
						providerId.setValue(store.getRecordAt(i).getAsString("providerId"));
						age.setValue(store.getRecordAt(i).getAsString("age"));
						voidedTextArea.setValue(store.getRecordAt(i).getAsString("voidReason"));
						voidDate.setValue(store.getRecordAt(i).getAsString("voidDate"));
						description.setValue(store.getRecordAt(i).getAsString("description"));
						bloodTypeCB.setValue(store.getRecordAt(i).getAsString("bloodType"));
						if(currentEncountersReloadFlag){
					    	currentEncountersStore.load();
						}
					}
				 
				}
				
				

			}
			public void onLoadException(Throwable error) {
				MessageBox.show(new MessageBoxConfig() {  
					{  
						setTitle("Error");
						setMsg("Ocurrio un error al tratar de obtener este paciente");
						setIconCls(MessageBox.ERROR);
					    setModal(true);
					    setButtons(MessageBox.OK);
					    setCallback(new MessageBox.PromptCallback() { 
						public void execute(
								String btnID,
								String text) {
							ListPatientsScreen.reloadFlag = true;
							MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
							
						}  
                        });  
					}  
				});
				}

		});
		

		
		
		formPanel.doLayout();
			
		//MessageBox.alert("adding formpanel");
		panel.add(formPanel);

		panel.doLayout();

		
		return panel;
	}
	
 
	
	  static ColumnModel createCurrentEncountersColModel() {
	      
	        ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
	        		//new ColumnConfig("C.I Paciente", "patientId"),
	                //new ColumnConfig("Nombres Paciente", "patientName"), 
	                new ColumnConfig("M&eacute;dico tratante", "encounterProvider"), 
	                new ColumnConfig("Fecha de Consulta", "encounterDate"),
	                new ColumnConfig("Referido De", "refferral"),
	                new ColumnConfig("Motivo", "reason"),
	                new ColumnConfig("Fecha de Creaci&oacute;n", "creationDate"),
	                new ColumnConfig("Usuario Creaci&oacute;n", "creationUser")
	
	        		
	        });
	        for (int i = 0; i < colModel.getColumnConfigs().length; i++){
	            ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
	        }
	        return colModel;
	    }

	  static ColumnModel createPreviousEncountersColModel() {
	      
	        ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
	        		//new ColumnConfig("C.I Paciente", "patientId"),
	                //new ColumnConfig("Nombres Paciente", "patientName"), 
	                new ColumnConfig("M&eacute;dico tratante", "encounterProvider"), 
	                new ColumnConfig("Fecha de Consulta", "encounterDate"),
	                new ColumnConfig("Referido De", "refferral"),
	                new ColumnConfig("Motivo", "reason"),
	                new ColumnConfig("Fecha de Creaci&oacute;n", "creationDate"),
	                new ColumnConfig("Usuario Creaci&oacute;n", "creationUser"),
	                new ColumnConfig("Fecha f&iacute;n", "endDate"),
	                new ColumnConfig("Fecha Ult. Modificaci&oacute;n", "lastEditDate"),
	                new ColumnConfig("Usuario Ult. Modificaci&oacute;n", "lastEditUser")

	        });
	        for (int i = 0; i < colModel.getColumnConfigs().length; i++){
	            ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
	        }
	        return colModel;
	    }

	 

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getVoided() {
		return voided;
	}

	public void setVoided(String voided) {
		this.voided = voided;
	}

	public String getLastEncounterDate() {
		return lastEncounterDate;
	}

	public void setLastEncounterDate(String lastEncounterDate) {
		this.lastEncounterDate = lastEncounterDate;
	}

	public com.gwtext.client.widgets.TabPanel getTabPanel() {
		return tabPanel;
	}

	public void setTabPanel(com.gwtext.client.widgets.TabPanel tabPanel) {
		this.tabPanel = tabPanel;
	}

	public Store getMainPanelStore() {
		return mainPanelStore;
	}

	public void setMainPanelStore(Store mainPanelStore) {
		this.mainPanelStore = mainPanelStore;
	}
}