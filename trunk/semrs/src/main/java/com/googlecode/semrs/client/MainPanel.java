package com.googlecode.semrs.client;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.semrs.client.model.User;
import com.googlecode.semrs.client.screens.admin.AdminUsersScreen;
import com.googlecode.semrs.client.screens.patient.ListPatientsScreen;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.GenericConfig;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.DateFieldDef;
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
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.DefaultsHandler;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.WindowMgr;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.VType;
import com.gwtext.client.widgets.form.event.FormListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtextux.client.widgets.image.Image;
import com.gwtextux.client.widgets.upload.UploadDialog;
import com.gwtextux.client.widgets.upload.UploadDialogListenerAdapter;

 

public class MainPanel implements EntryPoint , HistoryListener {

    private static PopupPanel messagePanel = new PopupPanel(true);
    public static TabPanel centerPanel;
    private ScreenManager screenManager;
    private Menu menu;
    private User currentUser = new User();
    private ModelState model = ModelState.getInstance();
    private static int sessionTimeOut = 0;
    static Timer t = null;

    

    public void onModuleLoad() {
    	
    	Controller.getInstance().getCurrentUser();
    	

    	
    	
    
          
          RecordDef timeOutRecordDef = new RecordDef(new FieldDef[]{  
        		new StringFieldDef("rememberMe"),  
  				new StringFieldDef("sessionTimeOut") 	
  				
  		});  

  		final JsonReader timeOutReader = new JsonReader("data", timeOutRecordDef);  
  		timeOutReader.setSuccessProperty("success"); 
  		HttpProxy timeOutProxy = new HttpProxy("/semrs/userServlet?getSessionTimeOut=true", Connection.GET);
  		final Store timeOutStore = new Store(timeOutProxy,timeOutReader);
  		timeOutStore.addStoreListener(new StoreListenerAdapter(){

			public void onLoad(Store store, Record[] records) {
				

				for(int i=0;i<records.length;i++){
					
					setSessionTimeOut(store.getRecordAt(i).getAsInteger("sessionTimeOut"));
					if(store.getRecordAt(i).getAsString("rememberMe").equals("true")){
						t = new Timer() {
				            public void run() {
				            	sendLoginRequest();		       
				            }
				          };
					}else if(store.getRecordAt(i).getAsString("rememberMe").equals("false")){
					    t = new Timer() {
				            public void run() {
				          
				            	MessageBox.show(new MessageBoxConfig() {  
									{  
										setTitle("Atenci&oacute;n");
										setMsg("Su sesi&oacute;n de usuario esta pronta a expirar, presione Aceptar para renovar.");
										setIconCls(MessageBox.WARNING);
									    setModal(true);
									    setButtons(MessageBox.OK);
									    setCallback(new MessageBox.PromptCallback() { 
										public void execute(
												String btnID,
												String text) {
										    	sendLoginRequest();						
										}  
				                        });  
									}  
								});
				                 
				            }
				          };
					}
					t.scheduleRepeating(getSessionTimeOut() * 60 * 1000);
				}
			}
		});
  		timeOutStore.load();
  		
          
 	
        
    	
        //create the main panel and assign it a BorderLayout
        Panel mainPanel = new Panel();
        mainPanel.setLayout(new BorderLayout());

        BorderLayoutData northLayoutData = new BorderLayoutData(RegionPosition.NORTH);
        northLayoutData.setSplit(false);

        BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
        centerLayoutData.setMargins(new Margins(10, 0, 5, 5));
        
        BorderLayoutData topLayoutData = new BorderLayoutData(RegionPosition.CENTER);
        topLayoutData.setMargins(new Margins(5, 0, 5, 5));

        Panel centerPanelWrappper = new Panel();
        centerPanelWrappper.setLayout(new FitLayout());
        centerPanelWrappper.setBorder(false);
        centerPanelWrappper.setBodyBorder(false);

        centerPanel = new TabPanel();
        centerPanel.setBodyBorder(false);
        centerPanel.setEnableTabScroll(true);
        centerPanel.setAutoScroll(true);
        centerPanel.setAutoDestroy(false);
        centerPanel.setActiveTab(0);
        /*
        centerPanel.setDefaults(new DefaultsHandler() {
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

        //hide the panel when the tab is closed
        centerPanel.addListener(new TabPanelListenerAdapter() {
            public boolean doBeforeTabChange(TabPanel source, Panel newPanel, Panel oldPanel) {
                WindowMgr.hideAll();
                return true;
            }

            public void onRemove(Container self, Component component) {
                component.hide();
            }

            public void onContextMenu(TabPanel source, Panel tab, EventObject e) {
                showMenu(tab, e);
            }
            
            public void onTabChange(TabPanel source, Panel tab) {
                 tab.show();
            }
            
        });
        centerPanel.setLayoutOnTabChange(true);
        centerPanel.setTitle("Main Content");
        centerPanel.setDeferredRender(false);
        

     
      
        HTML signOutLink = new HTML("<a href='javascript:;'>Salir</a>");
        signOutLink.addClickListener(new ClickListener() {  
            public void onClick(Widget widget) {  
              	 ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea cerrar la sesi&oacute;n actual?", "Si", "No",  
                           new MessageBox.ConfirmCallback() {  
                               public void execute(String btnID) {
                              	 if(btnID.equals("yes")){
                              	 redirect("/semrs/j_spring_security_logout");
                              	 }
                               }  
                           });  
               }  
        });  
        
        Panel aboutPanel = new Panel();  
        //navPanel.setTitle("Navigation");  
        aboutPanel.setWidth(300);  
        aboutPanel.setHtml(getAboutHTML());
         

        BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);  
        centerData.setMargins(3, 0, 3, 3);  
        
        final Window window = new Window();  
        window.setTitle("Acerca de");  
        window.setClosable(true);  
        window.setWidth(250);  
        window.setHeight(220);  
        window.setPlain(true);
        window.setResizable(false);
        window.setLayout(new BorderLayout());  
        window.add(aboutPanel, centerData);  
        window.setCloseAction(Window.HIDE); 

        HTML aboutLink = new HTML("<a href='javascript:;'>Acerca de</a>");
        aboutLink.addClickListener(new ClickListener() {  
            public void onClick(Widget widget) { 
            	 window.show();
            }  
        });  
        
        final Window profileWindow = new Window();  
        profileWindow.setTitle("Editar Perfil");  
        profileWindow.setWidth(450);  
        profileWindow.setHeight(600);    
        profileWindow.setLayout(new FitLayout());  
        profileWindow.setPaddings(5);  
        profileWindow.setResizable(false);
        profileWindow.setButtonAlign(Position.CENTER);  
        profileWindow.setModal(true);
        profileWindow.setIconCls("user-edit");
        //profileWindow.setMaximizable(true);
        //profileWindow.setAutoScroll(true);
        profileWindow.addListener(new ContainerListenerAdapter() {
            public void onResize(BoxComponent component, int adjWidth, int adjHeight, int rawWidth, int rawHeight) {
            	profileWindow.getEl().center();
            }
        });
        
       
        profileWindow.setCloseAction(Window.HIDE);  
        RecordDef recordDef = new RecordDef(new FieldDef[]{   
        		new StringFieldDef("username"),  
        		new StringFieldDef("name"),  
        		new StringFieldDef("lastName"),
        		new StringFieldDef("email"),
        		new DateFieldDef("birthDate", "birthDate", "d/m/Y"),
        		new StringFieldDef("sex"),  
        		new StringFieldDef("phoneNumber"),  
        		new StringFieldDef("mobile"),
        		new StringFieldDef("address"),
        		new StringFieldDef("password"),
        		new StringFieldDef("passwordRetype"),
        		new StringFieldDef("enabled"),
        		new StringFieldDef("loadSuccess")
        		});  

        final JsonReader reader = new JsonReader("data", recordDef);  
        reader.setSuccessProperty("success"); 
        reader.setId("username");

        //setup error reader to process from submit response from server  
        RecordDef errorRecordDef = new RecordDef(new FieldDef[]{  
        		new StringFieldDef("id"),  
        		new StringFieldDef("msg")  
        });  

        final JsonReader errorReader = new JsonReader("field", errorRecordDef);  
        errorReader.setSuccessProperty("success"); 

        final FormPanel userForm = new FormPanel(); 
        userForm.setReader(reader);  
        userForm.setErrorReader(errorReader); 
        userForm.setFrame(true);  
        userForm.setWidth(450);  
        userForm.setHeight(600);
        userForm.setAutoScroll(true);
        
        
        final Button saveButton = new Button("Guardar");
        saveButton.addListener(new ButtonListenerAdapter(){
        	public void onClick(final Button button, EventObject e){
        		
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
        		
        		 RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/userServlet?userEdit=submit&"+userForm.getForm().getValues());
        		 try {
        			
                     rb.sendRequest(null, new RequestCallback() {
                        public void onResponseReceived(Request req,final Response res) {
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
                           }else if(res.getText().equals("")){
                        	   MessageBox.hide();
	                           MessageBox.alert("Error", "Error interno"); 
                           }else{
                           MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());
                           MainPanel.resetTimer();
                           AdminUsersScreen.reloadFlag = true;
                           }
                           resetTimer();
                        }
                        
                        public void onError(Request req, Throwable exception) {
                           MessageBox.hide();  
		                   MessageBox.getDialog().close();
                           userForm.getForm().load("/semrs/userServlet?userEdit=load&id=currentUser", null, Connection.GET, "Cargando...");
                           MessageBox.alert("Error", "Error interno"); 
                        }
                        
                     });
                     
                  } catch (RequestException re) {
                	 MessageBox.hide();  
                     MessageBox.getDialog().close();
                	 userForm.getForm().load("/semrs/userServlet?userEdit=load&id=currentUser", null, Connection.GET, "Cargando...");
                	 MessageBox.alert("Error", "Error interno"); 
                  }
        	}
        	
        });
        saveButton.setIconCls("save-icon");
        userForm.addButton(saveButton);
        
        Button cancel = new Button("Cancelar");
        cancel.addListener(new ButtonListenerAdapter(){
        	public void onClick(Button button, EventObject e){
        		profileWindow.hide();
        	}
        	
        });
        cancel.setIconCls("cancel-icon");
        userForm.addButton(cancel);  
        
        
        FieldSet userFS = new FieldSet("Informaci&oacute;n de Usuario");  
        userFS.setCollapsible(true);
        userFS.setFrame(false);  
 

        final Image userImage = new Image("","/semrs/imageServlet?type=user&id=currentUser&op=load&date="+new Date().getSeconds());
        Panel proxy = new Panel();  
        proxy.setBorder(true);  
        proxy.setBodyBorder(false);
        proxy.setCollapsible(false);  
        proxy.add(userImage);
        
        
        HTML newImage = new HTML("<a href='javascript:;'>Nueva Imagen</a>");
        newImage.addClickListener(new ClickListener() {  
            public void onClick(Widget widget) { 
      		   UploadDialog dialog = new UploadDialog();  
     		   dialog.setUrl("/semrs/imageServlet?type=user&id=currentUser&op=upload");  
     		   dialog.setPermittedExtensions(new String[]{"jpg", "gif", "png", "jpeg"});  
     		   //dialog.setPostVarName(currentUser.getId());  
     		   dialog.addListener(new UploadDialogListenerAdapter() {
     			   public boolean onBeforeAdd(UploadDialog source, String filename) {
     				    if(source.getQueuedCount() > 0) {
     				        return false;
     				    } else {
     				        return true;
     				    }
     				}
     			   
     			   public void onUploadComplete(UploadDialog source){
     				   userImage.setSrc("/semrs/imageServlet?type=user&id=currentUser&op=load&date="+new Date().getSeconds());
     				  resetTimer();
     				   
     			   }
     	        });
     		   dialog.show(); 
            }  
        });  
        proxy.add(newImage);
        userFS.add(proxy);
        
        
        TextField usernameText = new TextField("Usuario","username",190);
        usernameText.setReadOnly(true);
        userFS.add(usernameText);  
        /*
        TextField loadSuccess = new TextField("loadSuccess","loadSuccess",190);
	    loadSuccess.setId("userLoadSuccess");
	    loadSuccess.setVisible(false);
	    userFS.add(loadSuccess);
        */
        TextField email = new TextField("Email", "email", 190);  
        email.setVtype(VType.EMAIL);
        userFS.add(email); 
        
        FieldSet passwordFS = new FieldSet();  
        passwordFS.setCheckboxToggle(true);  
        passwordFS.setFrame(false);  
        passwordFS.setTitle("Nuevo Password?");  
        passwordFS.setCollapsed(true);  
        
        final TextField password = new TextField("Nuevo Password", "password", 190);
        final TextField passwordRetype = new TextField("Confirmar", "passwordRetype", 190);
        password.setPassword(true);
        password.setAllowBlank(true);
        passwordFS.add(password); 
        
        
        passwordRetype.setPassword(true);
        passwordRetype.setAllowBlank(true);
        passwordFS.add(passwordRetype); 
        
        
        //add a ComboBox field  
        Store activeStore = new SimpleStore(new String[]{"id", "enabled"}, new String[][]{ new String[]{"Si","Si"}, new String[]{"No", "No"}});  
        activeStore.load();  

        ComboBox enabledCB = new ComboBox();  
        enabledCB.setFieldLabel("Activo?");  
        enabledCB.setHiddenName("enabled");  
        enabledCB.setStore(activeStore);  
        enabledCB.setDisplayField("enabled");  
        enabledCB.setTypeAhead(true);  
        enabledCB.setMode(ComboBox.LOCAL);  
        enabledCB.setTriggerAction(ComboBox.ALL);  
        enabledCB.setSelectOnFocus(true);  
        enabledCB.setWidth(85); 
        enabledCB.setEmptyText("Si");
        enabledCB.setReadOnly(true);
        userFS.add(enabledCB);
        userFS.add(passwordFS);
        
        com.gwtext.client.widgets.form.Validator validatePasswordRetype = new com.gwtext.client.widgets.form.Validator() {
        	public boolean validate(String value)
        	throws com.gwtext.client.widgets.form.ValidationException {
        		String passwordText = password.getText();
        		if (!value.equals(passwordText)) {
        			throw new com.gwtext.client.widgets.form.ValidationException("Los passwords no son iguales");
        		} else if (value.length() < 6) {
        			throw new com.gwtext.client.widgets.form.ValidationException("El password debe contener almenos 6 caracteres.");
        		} else {
        			return true;
        		}
        	}
        }; 
        passwordRetype.setValidator(validatePasswordRetype); 
        password.setValidator(new com.gwtext.client.widgets.form.Validator() {
			public boolean validate(String value)
			throws com.gwtext.client.widgets.form.ValidationException {
				if (value.length() < 6) {
					throw new com.gwtext.client.widgets.form.ValidationException("El password debe contener almenos 6 caracteres.");
				} else {
					return true;
				}
			}
		});
        userForm.add(userFS);
        
        
        FieldSet personalFS = new FieldSet("Informaci&oacute;n Personal");  
        personalFS.setCollapsible(true);
        personalFS.setFrame(false);  
       
        TextField nameText  = new TextField("Nombre", "name", 190);
        nameText.setAllowBlank(false);
        personalFS.add(nameText);  
  
        TextField lastNameText  = new TextField("Apellido", "lastName", 190);
        lastNameText.setAllowBlank(false); 
        personalFS.add(lastNameText);  
  
        
        DateField birthDate = new DateField("F. Nacimiento", "birthDate", 190);  
        birthDate.setAllowBlank(true);  
        birthDate.setMaxValue(new Date());
        birthDate.setReadOnly(true);
        personalFS.add(birthDate);
        
        //add a ComboBox field  
        Store store = new SimpleStore(new String[]{"abbr", "sex"}, new String[][]{ new String[]{"M","Masculino"}, new String[]{"F", "Femenino"}});  
        store.load();  

        ComboBox cb = new ComboBox();  
        cb.setFieldLabel("Sexo");  
        cb.setHiddenName("sex");  
        cb.setStore(store);  
        cb.setDisplayField("sex");  
        cb.setTypeAhead(true);  
        cb.setMode(ComboBox.LOCAL);  
        cb.setTriggerAction(ComboBox.ALL);  
        cb.setSelectOnFocus(true);  
        cb.setWidth(190); 
        cb.setEmptyText("N.D");
        cb.setReadOnly(true);
        personalFS.add(cb);
        
        userForm.add(personalFS);
        
        FieldSet contactFS = new FieldSet("Informaci&oacute;n de Contacto");  
        contactFS.setCollapsible(true);
        contactFS.setFrame(false);  
        
        TextField phone = new TextField("Telef&oacute;no", "phoneNumber", 190);
        phone.setVtype(VType.ALPHANUM);
        contactFS.add(phone); 
        
        TextField mobile = new TextField("M&oacute;vil", "mobile", 190);
        mobile.setVtype(VType.ALPHANUM);
        contactFS.add(mobile); 
        
        TextArea textArea = new TextArea("Direcci&oacute;n", "address");  
        textArea.setHideLabel(false);  
        // anchor width by percentage and height by raw adjustment  
        // sets width to 100% and height to "remainder" height - 53px  
        textArea.setWidth(190);
        textArea.setHeight(80);
        contactFS.add(textArea);
        
        userForm.add(contactFS);
        userForm.setMonitorValid(true);
        userForm.addListener(new FormPanelListenerAdapter() {
            public void onClientValidation(FormPanel formPanel, boolean valid) {
            	saveButton.setDisabled(!valid);
            }
        });
        

        
        HTML profileLink = new HTML("<a href='javascript:;'>Mi Perfil</a>");
        profileLink.addClickListener(new ClickListener() {  
        	public void onClick(Widget widget) { 
        		profileWindow.show();
        		userForm.getForm().load("/semrs/userServlet?userEdit=load&id=currentUser", null, Connection.GET, "Cargando...");
        		/*
        		userForm.getForm().addListener(new FormListenerAdapter(){
    		    	   public void onActionComplete(Form form, int httpStatus, String responseText) {
    		    		  if(form.findField("userLoadSuccess").getValueAsString().equals("false")){
    		    				MessageBox.show(new MessageBoxConfig() {  
    		    					{  
    		    						setTitle("Error");
    		    						setMsg("Este usuario no existe");
    		    						setIconCls(MessageBox.ERROR);
    		    					    setModal(true);
    		    					    setButtons(MessageBox.OK);
    		    					    setCallback(new MessageBox.PromptCallback() { 
    		    						public void execute(
    		    								String btnID,
    		    								String text) {
    		    							profileWindow.close();
    		    							
    		    						}  
    		                            });  
    		    					}  
    		    				});
    		    			  
    		    		  }    
    		    	   }
    		    	   public void onActionFailed(Form form, int httpStatus, String responseText) {
    		   			MessageBox.show(new MessageBoxConfig() {  
    						{  
    							setTitle("Error");
    							setMsg("Ocurrio un error al tratar de obtener este usuario");
    							setIconCls(MessageBox.ERROR);
    						    setModal(true);
    						    setButtons(MessageBox.OK);
    						    setCallback(new MessageBox.PromptCallback() { 
    							public void execute(
    									String btnID,
    									String text) {
    								profileWindow.close();
    								
    							}  
    	                        });  
    						}  
    					});
    		    	    }
    		    	   
    		       });*/
        		userImage.setSrc("/semrs/imageServlet?type=user&id=currentUser&op=load&date="+new Date().getSeconds());
        		resetTimer();
        	}  
        });  
        
        profileWindow.add(userForm); 
        
       

        //final Label userLabel = new Label();
        final HTML userLabel = new HTML("<b> Bienvenido, </b>");
        model.addPropertyChangeListener("currentUser", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            	currentUser = (User) propertyChangeEvent.getNewValue();
            	userLabel.setHTML("<b> Bienvenido, "+ currentUser.getName() + " " + currentUser.getLastName()+" </b>");  
            }
        });
        
        //userLabel.setText("Bienvenido, ");
        userLabel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
        
        
        HorizontalPanel outer = new HorizontalPanel();
        VerticalPanel inner = new VerticalPanel();

        outer.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        inner.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);

        HorizontalPanel links = new HorizontalPanel();
        links.setSpacing(4);
        links.add(profileLink);
        links.add(signOutLink);
        links.add(aboutLink);


        //final Image logo = images.logo().createImage();
        Image logo = new Image("semrs_logo", "images/logo/semrs_logo.png");
        outer.add(logo);
        //outer.add(new ClockWidget());
        outer.setCellHorizontalAlignment(logo, HorizontalPanel.ALIGN_LEFT);

        outer.add(inner);
        inner.add(userLabel);
        inner.add(links);
        
        mainPanel.add(outer, new BorderLayoutData(RegionPosition.NORTH)); 
        //Panel clockWrapper = new Panel();
        
        //clockWrapper.add(clockWidget, new BorderLayoutData(RegionPosition.CENTER));
        
  
        screenManager = new ScreenManager(centerPanel);

        //setup the west regions layout properties
        BorderLayoutData westLayoutData = new BorderLayoutData(RegionPosition.WEST);
        westLayoutData.setMargins(new Margins(10, 5, 0, 5));
        westLayoutData.setCMargins(new Margins(10, 5, 5, 5));
        westLayoutData.setMinSize(155);
        westLayoutData.setMaxSize(350);
        westLayoutData.setSplit(true);

        //create the west panel and add it to the main panel applying the west region layout properties
        Panel westPanel = new Panel();
        westPanel.setId("side-nav");
        westPanel.setTitle("Men&uacute Principal");
        westPanel.setLayout(new FitLayout());
        westPanel.setWidth(210);
        westPanel.setCollapsible(true);

        final TabPanel tabPanel = new TabPanel();
        tabPanel.setActiveTab(0);
        tabPanel.setDeferredRender(true);
        tabPanel.setTabPosition(Position.BOTTOM);
        screenManager.addPropertyChangeListener("store", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            	tabPanel.clear();
                tabPanel.setActiveTab(0);
                tabPanel.setDeferredRender(true);
                tabPanel.setTabPosition(Position.BOTTOM);
            	tabPanel.add(screenManager.getTreeNav());
            	tabPanel.add(screenManager.getAccordionNav());
            	tabPanel.setActiveTab(0);
            	tabPanel.doLayout();
            }
        });
        tabPanel.add(screenManager.getTreeNav());
        tabPanel.add(screenManager.getAccordionNav());
        westPanel.add(tabPanel);
        mainPanel.add(westPanel, westLayoutData);

        final Panel introPanel = new Panel();
        introPanel.setTitle("Panel Principal");
        introPanel.setPaddings(10);
        introPanel.setLayout(new FitLayout());
        final MainTab showcaseView = new MainTab();
        introPanel.add(showcaseView);

        centerPanel.add(introPanel, centerLayoutData);
        centerPanelWrappper.add(centerPanel);
       
        mainPanel.add(centerPanelWrappper, centerLayoutData);
        

        final String initToken = History.getToken();
        if (initToken.length() != 0) {
            mainPanel.addListener(new PanelListenerAdapter() {
                public void onRender(Component component) {
                    onHistoryChanged(initToken);
                }
            });
        }

        Viewport viewport = new Viewport(mainPanel);

        // Add history listener
        History.addHistoryListener(this);
    }


    /**
     * This method is called whenever the application's history changes.
     *
     * @param historyToken the histrory token
     */
    public void onHistoryChanged(String historyToken) {
        screenManager.showScreen(historyToken);
    }

    private void showMenu(final Panel tab, EventObject e) {
        if (menu == null) {
            menu = new Menu();
            Item close = new Item("Cerrar ventana");
            close.setId("close-tab-item");
            close.addListener(new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    centerPanel.remove(centerPanel.getActiveTab());
                }
            });
            menu.addItem(close);

            Item closeOthers = new Item("Cerrar todas las ventanas");
            closeOthers.setId("close-others-item");
            closeOthers.addListener(new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    Component[] items = centerPanel.getItems();
                    for (int i = 0; i < items.length; i++) {
                        Panel panel = (Panel) items[i];
                        /*
                        if (panel.isClosable() && !panel.getId().equals(centerPanel.getActiveTab().getId())) {
                            centerPanel.remove(panel);
                        }
                        */
                        if (panel.isClosable()) {
                            centerPanel.remove(panel);
                        }
                    }
                }
            });
            menu.addItem(closeOthers);
        }

        BaseItem close = menu.getItem("close-tab-item");
        if (!centerPanel.getActiveTab().isClosable()) {
            close.disable();
        } else {
            close.enable();
        }

        BaseItem closeOthers = menu.getItem("close-others-item");
        if (centerPanel.getItems().length == 1) {
            closeOthers.disable();
        } else {
            closeOthers.enable();
        }
        menu.showAt(e.getXY());
    }

    public static void showMessage(String title, String message) {
        messagePanel.setPopupPosition(500, 300);
        messagePanel.setWidget(new HTML(getMessageHtml(title, message)));
        messagePanel.setWidth("300px");
        messagePanel.show();
    }
    
    
	public void sendLoginRequest(){
		 RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
	        try {
	            rb.sendRequest(null, new RequestCallback() {
	            	  public void onError(Request request, Throwable exception) {
	            		  MessageBox.show(new MessageBoxConfig() {  
	      					{  
	      						setTitle("Error");
	      						setMsg("Ha ocurrido un error al tratar de renovar la sesi&oacute;n.");
	      						setIconCls(MessageBox.ERROR);
	      					    setModal(true);
	      					    setButtons(MessageBox.OK);
	      					}});
	                	//MessageBox.alert("Error", "Ha ocurrido un error al tratar de renovar la sesi&oacute;n.");
	                }
						public void onResponseReceived(Request arg0, Response arg1) {
							String errorMessage = arg1.getText();
		            		if(errorMessage.indexOf("login") != -1){
		            			MessageBox.show(new MessageBoxConfig() {  
		        					{  
		        						setTitle("Error");
		        						setMsg("Su sesi&oacute;n de usuario ha expirado, presione Aceptar para volver a loguearse." );
		        						setIconCls(MessageBox.WARNING);
		        					    setModal(true);
		        					    setButtons(MessageBox.OK);
		        					    setCallback(new MessageBox.PromptCallback() { 
		        						public void execute(
		        								String btnID,
		        								String text) {
		        							redirect("/semrs/");
		        						  }
		        						});
		        					}
		            			});
		            
		            		}else{
		            			resetTimer();
		            		}
						}
	            });
	        } catch (RequestException e) {
	        	  MessageBox.show(new MessageBoxConfig() {  
  					{  
  						setTitle("Error");
  						setMsg("Ha ocurrido un error al tratar de conectarse con el servidor.");
  						setIconCls(MessageBox.ERROR);
  					    setModal(true);
  					    setButtons(MessageBox.OK);
  					}});
	        }
	}
    

    native void redirect(String url)
    /*-{
            $wnd.location.replace(url);

    }-*/; 
    
    
    private static String getAboutHTML() {  
    	return  "<p><img src='images/logo/semrs_logo.png' align='center'/> " +
    			"<p align='center'>SEMRS - Centro Integral de Salud Macuto " +  
    			"<p align='center'>Versi&oacute;n 0.1 " +
    	        "<p align='center'>2008 Roger Marin, Jose Alvarado " +  
    	        "<p align='center'>TEG Ingenieria de Sistemas USM ";
    }  

    

    private static native String getMessageHtml(String title, String message) /*-{
                                                          return ['<div class="msg">',
                                                                  '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
                                                                  '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>', title, '</h3>', message, '</div></div></div>',
                                                                  '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
                                                                  '</div>'].join('');
                                                      }-*/;


	
	public static void resetTimer(){
		  t.cancel();
		  t.scheduleRepeating(getSessionTimeOut()  * 60 * 1000);
	}


	public static int getSessionTimeOut() {
		return sessionTimeOut;
	}


	public static void setSessionTimeOut(int sessionTimeOut) {
		MainPanel.sessionTimeOut = sessionTimeOut;
	}
	




}
