package com.googlecode.semrs.client.screens.admin;

import java.util.Date;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.semrs.client.ExtendedMessageBox;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
import com.googlecode.semrs.client.screens.patient.ListPatientsScreen;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.SortDir;
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
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Resizable;
import com.gwtext.client.widgets.ResizableConfig;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.event.ResizableListenerAdapter;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.VType;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.FormListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.form.event.TextFieldListenerAdapter;
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
import com.gwtextux.client.widgets.form.ItemSelector;
import com.gwtextux.client.widgets.image.Image;
import com.gwtextux.client.widgets.upload.UploadDialog;
import com.gwtextux.client.widgets.upload.UploadDialogListenerAdapter;


public class AdminGroupsScreen extends ShowcasePanel {

	  private com.gwtext.client.widgets.TabPanel tabPanel;
	  
	    private FormPanel groupForm = null;
	    
	    private com.gwtext.client.widgets.Window editGroupWindow = null;
	     
	     public static boolean reloadFlag = false;
	     
	     private static String groupId;
	     
	     //private static String deleteURL;
	     
	     FieldDef[] fieldDefs = new FieldDef[] { 
	     		new StringFieldDef("id"),
	            new StringFieldDef("name"), 
	            new StringFieldDef("description"), 
	     		new StringFieldDef("lastEditDate"),	
	     		new StringFieldDef("lastEditUser")
	     };

	     RecordDef recordDef = new RecordDef(fieldDefs);

	     JsonReader reader = new JsonReader("response.value.items", recordDef);

	     HttpProxy proxy = new HttpProxy("/semrs/groupServlet", Connection.GET);
	     final Store store = new Store(proxy,reader,true);
	     final PagingToolbar pagingToolbar = new PagingToolbar(store);
	     
	     public AdminGroupsScreen(){
	    	//reader.setVersionProperty("response.value.version");
	         reader.setTotalProperty("response.value.total_count");
	         reader.setId("id");
	     }
	     
	     protected void onActivate() {
	    	 if(reloadFlag){
	    	   store.load(0, pagingToolbar.getPageSize()); 
	    	   reloadFlag = false;
	    	 }
	     }
	
	public Panel getViewPanel() {
        if (panel == null) {
            panel = new Panel();
            MainPanel.resetTimer();
            
            final ToolbarButton deleteGroupButton = new ToolbarButton("Eliminar Grupo");
            deleteGroupButton.setIconCls("delete-icon");
            deleteGroupButton.setDisabled(true);
            
            store.setDefaultSort("id", SortDir.ASC);
            store.addStoreListener(new StoreListenerAdapter() {
            	public void onLoadException(Throwable error) {
            		  //Check for session expiration
            		  RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
            	        try {
            	            rb.sendRequest(null, new RequestCallback() {

            	                public void onError(Request request, Throwable exception) {
            	                	MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de grupos.");
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
				            			MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de grupos.");
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
          
 
            
            final FormPanel formPanel = new FormPanel();
            formPanel.setFrame(true);  
            formPanel.setTitle("Lista de Grupos");  
            formPanel.setWidth(900);  
            formPanel.setLabelWidth(100); 
            formPanel.setPaddings(5, 5, 5, 0);  
            formPanel.setLabelAlign(Position.TOP);  
            formPanel.setIconCls("groups-icon");
           
            GridView view = new GridView();
            view.setEmptyText("No hay Registros");
            view.setAutoFill(true);
            view.setForceFit(true);

            GridPanel grid = new GridPanel(store, createColModel());
            grid.setEnableDragDrop(false);
            grid.setWidth(850);
            grid.setHeight(520);
            grid.setLoadMask(true);  
            grid.setSelectionModel(new RowSelectionModel());  
            grid.setFrame(true);  
            grid.setView(view);
            grid.addGridCellListener(new GridCellListener() {  

				
				public void onCellDblClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
				    Record r = grid.getStore().getAt(rowIndex);
				    String recordId = r.getAsString("id");
				    setGroupId(recordId);
				    //groupId = recordId;
				    com.gwtext.client.widgets.Window groupWindow = getEditGroupWindow(false);
				    groupWindow.show();
				    //getEditGroupWindow(groupId).show();
				}

				
				public void onCellClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
					final Record r = grid.getStore().getAt(rowIndex);
					String recordId = r.getAsString("id");
					deleteGroupButton.setDisabled(false);
					deleteGroupButton.setId(recordId);
				}

	
				public void onCellContextMenu(GridPanel grid, int rowIndex,
						int cellIndex, EventObject e) {
					// TODO Auto-generated method stub
					
				}
                });  
            
           
            pagingToolbar.setPageSize(20);
            pagingToolbar.setDisplayInfo(true);
            pagingToolbar.setEmptyMsg("No hay registros");
      

            NumberField pageSizeField = new NumberField();
            pageSizeField.setWidth(40);
            pageSizeField.setSelectOnFocus(true);
            pageSizeField.addListener(new FieldListenerAdapter() {
                public void onSpecialKey(Field field, EventObject e) {
                    if (e.getKey() == EventObject.ENTER) {
                        int pageSize = Integer.parseInt(field.getValueAsString());
                        pagingToolbar.setPageSize(pageSize);
                    }
                }
            });

            ToolTip toolTip = new ToolTip("Introduzca el tama&ntilde;o de p&aacute;gina");
            toolTip.applyTo(pageSizeField);
             
            pagingToolbar.addField(pageSizeField);
            pagingToolbar.addSeparator();
            ToolbarButton newGroupButton = new ToolbarButton("Nuevo Grupo", new ButtonListenerAdapter() {  
            	public void onClick(Button button, EventObject e) {
            	   setGroupId("");
            	   //groupId = "";
            	    com.gwtext.client.widgets.Window groupWindow = getEditGroupWindow(true);
   				    groupWindow.show();
   				// getEditGroupWindow("").show();
            	}  
            });  
            newGroupButton.setIconCls("add-icon");
            pagingToolbar.addButton(newGroupButton);
            pagingToolbar.addSeparator();
            
            deleteGroupButton.addListener(new ButtonListenerAdapter() {  
            	public void onClick(final Button button, EventObject e) {
            		
            		ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea eliminar este grupo?", "Si", "No",  
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

                            		final RequestBuilder deleteRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/groupServlet?groupEdit=delete&id="+button.getId());
                    				try {
                    					
                    					deleteRequest.sendRequest(null, new RequestCallback() {
                    						public void onResponseReceived(Request req, final Response res) {
                    							button.setDisabled(true);
                    							MessageBox.hide();  
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
                    								MainPanel.resetTimer();
                    								MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());  
                    								store.reload();
                    								
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
            });  
           // pagingToolbar.addButton(deleteGroupButton);
           // pagingToolbar.addSeparator();
            ToolbarButton exportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
            	public void onClick(Button button, EventObject e) {
            		Window.open("/semrs/groupServlet?export=true", "_self", ""); 

            	}  
            });  
            exportButton.setIconCls("excel-icon");
            pagingToolbar.addButton(exportButton);
            pagingToolbar.addSeparator();
            pagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
            grid.setBottomToolbar(pagingToolbar);
            
            
            grid.addListener(new PanelListenerAdapter() {  
                         public void onRender(Component component) {  
                        	 store.load(0, pagingToolbar.getPageSize()); 
                         }  
                     }); 
            formPanel.add(grid, new AnchorLayoutData("100%"));
            
            panel.add(formPanel);
            
        }
      
    
        return panel;
    }

    static ColumnModel createColModel() {
      
        ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
        		new ColumnConfig("Id", "id"),
                new ColumnConfig("Nombre", "name"), 
                new ColumnConfig("Descripci&oacute;n", "description"), 
                new ColumnConfig("F.Modificaci&oacute;n", "lastEditDate"),
                new ColumnConfig("U.Modificaci&oacute;n", "lastEditUser")
        		
        });
        for (int i = 0; i < colModel.getColumnConfigs().length; i++){
            ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
        }
        return colModel;
    }


	public com.gwtext.client.widgets.TabPanel getTabPanel() {
		return tabPanel;
	}

	public void setTabPanel(com.gwtext.client.widgets.TabPanel tabPanel) {
		this.tabPanel = tabPanel;
	}
	
	
	public com.gwtext.client.widgets.Window getEditGroupWindow(boolean isNew){
		  
		 if(editGroupWindow!=null){
			editGroupWindow.clear();
		 }
		
		  editGroupWindow = new com.gwtext.client.widgets.Window();  
		  editGroupWindow.setTitle("Editar Grupo");  
		  editGroupWindow.setWidth(520);  
		  editGroupWindow.setHeight(550);    
		  editGroupWindow.setLayout(new FitLayout());  
		  editGroupWindow.setPaddings(5);  
		  editGroupWindow.setResizable(true);
		  editGroupWindow.setButtonAlign(Position.CENTER);  
		  editGroupWindow.setModal(true);
		  editGroupWindow.setId("editGroupWindow");
		  editGroupWindow.setIconCls("groups-icon");
		  editGroupWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE); 
		  editGroupWindow.setMaximizable(true);
		  //editGroupWindow.setMinimizable(true);
		  
		  
		   RecordDef recordDef = new RecordDef(new FieldDef[]{   
	        		new StringFieldDef("id"),  
	        		new StringFieldDef("name"),  
	        		new StringFieldDef("description"),
		     		new StringFieldDef("loadSuccess")
	        		});  

	        final JsonReader reader = new JsonReader("data", recordDef);  
	        reader.setSuccessProperty("success"); 
	        reader.setId("id");

	        //setup error reader to process from submit response from server  
	        RecordDef errorRecordDef = new RecordDef(new FieldDef[]{  
	        		new StringFieldDef("id"),  
	        		new StringFieldDef("msg")  
	        });  

	        final JsonReader errorReader = new JsonReader("field", errorRecordDef);  
	        errorReader.setSuccessProperty("success"); 
	        
	      if(groupForm!=null){
	    	  groupForm.clear();
	      }

	        groupForm = new FormPanel(); 
	        
	        groupForm.setReader(reader);  
	        groupForm.setErrorReader(errorReader); 
	        groupForm.setFrame(true);  
	        groupForm.setWidth(520);  
	        groupForm.setHeight(550);
	        groupForm.setAutoScroll(true);
	        groupForm.setId("editGroupForm");
	        
			Panel proxyPanel = new Panel();  
			proxyPanel.setBorder(true);  
			proxyPanel.setBodyBorder(false);
			proxyPanel.setCollapsible(false);  
			proxyPanel.setLayout(new FormLayout());
			proxyPanel.setButtonAlign(Position.CENTER);
			//proxyPanel.setIconCls("groupProxyPanel");

	        FieldSet groupFS = new FieldSet("Informaci&oacute;n de Grupo");  
	        groupFS.setCollapsible(true);
	        groupFS.setFrame(false);  
	        groupFS.setId("groupFS");
	        //groupFS.setAutoWidth(true);
	        
	        
	        TextField groupIdText = new TextField("Id","id",190);
	        groupIdText.setId("groupIdText");
	        //if(!getGroupId().equals("")){
	          groupIdText.setReadOnly(true);
	          groupIdText.setDisabled(true);
	        //}
	        //groupIdText.setAllowBlank(false);
	        groupIdText.setStyle("textTransform: uppercase;");
	        groupIdText.addListener(new FieldListenerAdapter(){
	        	 public void onBlur(Field field) {
	            String value = field.getValueAsString();
	            field.setValue(value.toUpperCase());
	        	}
	        	
	        });
	        TextField loadSuccess = new TextField("loadSuccess","loadSuccess",190);
	        loadSuccess.setId("groupLoadSuccess");
	        loadSuccess.setVisible(false);
	        groupFS.add(loadSuccess);  
	        groupFS.add(groupIdText); 
	        
	        TextField groupNameText = new TextField("Nombre", "name", 190);  
	        groupNameText.setId("groupNameText");
	        groupNameText.setAllowBlank(false);
	        if(!getGroupId().equals("")){
	        	groupNameText.setReadOnly(true);
	        }
	        groupFS.add(groupNameText); 
	        
	        TextArea groupDesc = new TextArea("Descripci&oacute;n", "description");  
	        groupDesc.setId("groupDesc");
	        groupDesc.setHideLabel(false);  
	        groupDesc.setWidth(190);
	        groupDesc.setHeight(80);
	        groupFS.add(groupDesc); 
	        
	        groupForm.add(groupFS);
	        
	        FieldSet groupUserFS = new FieldSet("Usuarios");  
	        groupUserFS.setId("groupUserFS");
	        groupUserFS.setCollapsible(true);
	        groupUserFS.setFrame(false);  
	        groupUserFS.setAutoWidth(true);
	        
	        RecordDef userRecordDef = new RecordDef(new FieldDef[] {new StringFieldDef("id"), new StringFieldDef("desc")});
			final JsonReader userRecordReader = new JsonReader("data", userRecordDef);  
			userRecordReader.setSuccessProperty("success"); 
			userRecordReader.setId("id");
			
			final ItemSelector itemSelector = new ItemSelector();
			HttpProxy availableUsers = new HttpProxy("/semrs/groupServlet?groupEdit=getUsers&id="+getGroupId(), Connection.GET);
			Store fromStore = new Store(availableUsers,userRecordReader);
			//fromStore.commitChanges();
			fromStore.load();
			itemSelector.setName("users");
			itemSelector.setId("users");
			itemSelector.setFieldLabel("Usuarios");

			if(!getGroupId().equals("")){
				HttpProxy groupUsers = new HttpProxy("/semrs/groupServlet?groupEdit=getUsers&id="+getGroupId()+"&groupUsers=true", Connection.GET);
				Store toStore = new Store(groupUsers,userRecordReader);
				toStore.load();
				itemSelector.setToStore(toStore);
			}else{
				Store toStore = new Store(userRecordDef);
				toStore.add(userRecordDef.createRecord(new Object[]{"",""}));
				toStore.commitChanges();
				itemSelector.setToStore(toStore);
				// toStore.load();
			}

			itemSelector.setMsWidth(160);  
			itemSelector.setMsHeight(200);  

			itemSelector.setValueField("id");  
			itemSelector.setDisplayField("desc");  

			itemSelector.setFromStore(fromStore);


			Toolbar fromToolbar = new Toolbar();  
			ToolbarButton addButton = new ToolbarButton();  
			addButton.setDisabled(true);  
			addButton.setIconCls("user-delete-icon");  
			fromToolbar.addButton(addButton);  
			fromToolbar.addSpacer();  
			fromToolbar.addItem(new ToolbarTextItem("Usuarios Disponibles"));  
			itemSelector.setFromToolbar(fromToolbar);  

			Toolbar toToolbar = new Toolbar();  
			ToolbarButton clearButton = new ToolbarButton("Usuarios de este grupo");  
			clearButton.setIconCls("user-add-icon"); 

			toToolbar.addButton(clearButton);  
			itemSelector.setToToolbar(toToolbar);  
			
			groupUserFS.add(itemSelector);
			
			groupForm.add(groupUserFS);
	        
			 final Button saveButton = new Button("Guardar");
			 saveButton.setId("groupSaveButton");
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
		        		
		        		 
		        		 try {
		        			 RequestBuilder saveRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/groupServlet?groupEdit=submit&isNew="+getGroupId()+"&"+groupForm.getForm().getValues());
		        			 saveRequest.sendRequest(null, new RequestCallback() {
		                        public void onResponseReceived(Request req, final Response res) {
		                           MessageBox.hide();  
		                           MessageBox.getDialog().close();
	    							MessageBox.hide();  
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
		                           editGroupWindow.hide();
		                           store.reload();
		                           }
		                        }
		                        
		                        public void onError(Request req, Throwable exception) {
		                           MessageBox.hide();
		                           //groupForm.getForm().load("/semrs/groupServlet?groupEdit=load&id="+groupId, null, Connection.GET, "Cargando...");
		                           MessageBox.alert("Error", "Error interno"); 
		                        }
		                        
		                     });
		                     
		                  } catch (RequestException re) {
		                	 MessageBox.hide();
		                	 //groupForm.getForm().load("/semrs/groupServlet?groupEdit=load&id="+groupId, null, Connection.GET, "Cargando...");
		                	 MessageBox.alert("Error", "Error interno"); 
		                  }
		        	}
		        	
		        });
		        saveButton.setIconCls("save-icon");
		        proxyPanel.addButton(saveButton);
		        
		        
		        Button delete = new Button("Eliminar");
		        delete.setId("groupDeleteButton");
		        if(getGroupId().equals("")){
		        	delete.setDisabled(true);
		        }else{
		        	delete.setDisabled(false);
		        }
		        final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/groupServlet?groupEdit=delete&id="+getGroupId());
		        delete.addListener(new ButtonListenerAdapter(){
		        	public void onClick(final Button button, EventObject e){
		        		
		        		ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea eliminar este grupo?", "Si", "No",  
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
		            	    					
		            	    					rb.sendRequest(null, new RequestCallback() {
		            	    						public void onResponseReceived(Request req, final Response res) {
		            	    							   MessageBox.hide();  
		            			                           MessageBox.getDialog().close();
		            		    							MessageBox.hide(); 
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
		            	    								MainPanel.resetTimer();
		            	    								MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText() ,  
		            	    		                                 new MessageBox.AlertCallback() { 
		            	    									public void execute() {
		            	    										editGroupWindow.hide();
		            	    										store.reload();
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
		        delete.setIconCls("delete-icon");
		        proxyPanel.addButton(delete);  
		        
		        
		        Button cancel = new Button("Cancelar");
		        cancel.setId("cancelGroupButton");
		        cancel.addListener(new ButtonListenerAdapter(){
		        	public void onClick(Button button, EventObject e){
		        		editGroupWindow.hide();
		        	}
		        	
		        });
		        cancel.setIconCls("cancel-icon");
		        proxyPanel.addButton(cancel);  
			
		        groupForm.add(proxyPanel);
			
	        groupForm.setMonitorValid(true);
	        groupForm.addListener(new FormPanelListenerAdapter() {
	            public void onClientValidation(FormPanel formPanel, boolean valid) {
	            	saveButton.setDisabled(!valid);
	            }
	        });
	      groupForm.doLayout();
	      if(!isNew){
	       groupForm.getForm().load("/semrs/groupServlet?groupEdit=load&id="+getGroupId(), null, Connection.GET, "Cargando...");
	       groupForm.getForm().addListener(new FormListenerAdapter(){
	    	   public void onActionComplete(Form form, int httpStatus, String responseText) {
	    		  if(form.findField("groupLoadSuccess").getValueAsString().equals("false")){
	    				MessageBox.show(new MessageBoxConfig() {  
	    					{  
	    						setTitle("Error");
	    						setMsg("Este grupo no existe");
	    						setIconCls(MessageBox.ERROR);
	    					    setModal(true);
	    					    setButtons(MessageBox.OK);
	    					    setCallback(new MessageBox.PromptCallback() { 
	    						public void execute(
	    								String btnID,
	    								String text) {
	    							store.reload();
	    							editGroupWindow.close();
	    							
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
						setMsg("Ocurrio un error al tratar de obtener este grupo");
						setIconCls(MessageBox.ERROR);
					    setModal(true);
					    setButtons(MessageBox.OK);
					    setCallback(new MessageBox.PromptCallback() { 
						public void execute(
								String btnID,
								String text) {
							store.reload();
							editGroupWindow.close();
							
						}  
                        });  
					}  
				});
	    	    }
	    	   
	       });
	      }
	      ResizableConfig config = new ResizableConfig();  
	      config.setHandles(Resizable.ALL);  
	      final Resizable resizable = new Resizable(groupForm, config);  
	               resizable.addListener(new ResizableListenerAdapter() {  
	                   public void onResize(Resizable self, int width, int height) {  
	                	   groupForm.setWidth(width);  
	                	   groupForm.setHeight(height);
	                	   
	                   }  
	               });  
	      editGroupWindow.add(groupForm);  
	      editGroupWindow.doLayout();
		  return editGroupWindow;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


}
