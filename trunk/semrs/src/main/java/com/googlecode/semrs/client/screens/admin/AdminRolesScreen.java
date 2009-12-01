package com.googlecode.semrs.client.screens.admin;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.FormListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtextux.client.widgets.form.ItemSelector;

public class AdminRolesScreen extends ShowcasePanel{
	
	  private com.gwtext.client.widgets.TabPanel tabPanel;
	  
	    private FormPanel roleForm = null;
	    
	    private com.gwtext.client.widgets.Window editRoleWindow = null;
	     
	     public static boolean reloadFlag = false;
	     
	     private static String roleId;
	     
	     //private static String deleteURL;
	     
	     FieldDef[] fieldDefs = new FieldDef[] { 
	     		new StringFieldDef("auth"),
	            new StringFieldDef("description"), 
	            new StringFieldDef("longDescription"), 
	     		new StringFieldDef("lastEditDate"),	
	     		new StringFieldDef("lastEditUser")
	     };

	     RecordDef recordDef = new RecordDef(fieldDefs);

	     JsonReader reader = new JsonReader("response.value.items", recordDef);

	     HttpProxy proxy = new HttpProxy("/semrs/roleServlet", Connection.GET);
	     final Store store = new Store(proxy,reader,true);
	     final PagingToolbar pagingToolbar = new PagingToolbar(store);
	     
	     public AdminRolesScreen(){
	    	//reader.setVersionProperty("response.value.version");
	         reader.setTotalProperty("response.value.total_count");
	         reader.setId("auth");
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
          
          store.setDefaultSort("auth", SortDir.ASC);
          store.addStoreListener(new StoreListenerAdapter() {
          	public void onLoadException(Throwable error) {
          		  //Check for session expiration
          		  RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
          	        try {
          	            rb.sendRequest(null, new RequestCallback() {

          	                public void onError(Request request, Throwable exception) {
          	                	MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de roles.");
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
				            			MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de roles.");
				            		}
								}
          	            });
          	        }catch (RequestException e) {
        	        	MessageBox.alert("Error", "Ha ocurrido un error al tratar de conectarse con el servidor.");
        	        }
				}
          	 public void onDataChanged(Store store) {
          		 MainPanel.resetTimer();
          	    }

          });
        

          
          final FormPanel formPanel = new FormPanel();
          formPanel.setFrame(true);  
          formPanel.setTitle("Lista de Roles");  
          formPanel.setWidth(900);  
          formPanel.setLabelWidth(100); 
          formPanel.setPaddings(5, 5, 5, 0);  
          formPanel.setLabelAlign(Position.TOP);  
          formPanel.setIconCls("roles-icon");
         
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
				    String recordId = r.getAsString("auth");
				    setRoleId(recordId);
				    //roleId = recordId;
				    com.gwtext.client.widgets.Window roleWindow = getEditRoleWindow(false);
				    roleWindow.show();
				    //getEditGroupWindow(roleId).show();
				}

				
				public void onCellClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
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
          ToolbarButton exportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
          	public void onClick(Button button, EventObject e) {
          		Window.open("/semrs/roleServlet?export=true", "_self", ""); 

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
      		new ColumnConfig("Id", "auth"),
              new ColumnConfig("Nombre", "description"), 
              new ColumnConfig("Descripci&oacute;n", "longDescription"), 
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
	
	
	public com.gwtext.client.widgets.Window getEditRoleWindow(boolean isNew){
		  
		 if(editRoleWindow!=null){
			editRoleWindow.clear();
		 }
		
		  editRoleWindow = new com.gwtext.client.widgets.Window();  
		  editRoleWindow.setTitle("Editar Rol");  
		  editRoleWindow.setWidth(520);  
		  editRoleWindow.setHeight(550);    
		  editRoleWindow.setLayout(new FitLayout());  
		  editRoleWindow.setPaddings(5);  
		  editRoleWindow.setResizable(true);
		  editRoleWindow.setButtonAlign(Position.CENTER);  
		  editRoleWindow.setModal(true);
		  editRoleWindow.setId("editRoleWindow");
		  editRoleWindow.setIconCls("roles-icon");
		  editRoleWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);  
		  editRoleWindow.setMaximizable(true);
		
		  //editRoleWindow.setMinimizable(true);
		  
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
	        
	      if(roleForm!=null){
	    	  roleForm.clear();
	      }

	        roleForm = new FormPanel(); 
	        
	        roleForm.setReader(reader);  
	        roleForm.setErrorReader(errorReader); 
	        roleForm.setFrame(true);  
	        roleForm.setWidth(520);  
	        roleForm.setHeight(550);
	        roleForm.setAutoScroll(true);
	        roleForm.setId("editRoleForm");
	   
	        
			Panel proxyPanel = new Panel();  
			proxyPanel.setBorder(true);  
			proxyPanel.setBodyBorder(false);
			proxyPanel.setCollapsible(false);  
			proxyPanel.setLayout(new FormLayout());
			proxyPanel.setButtonAlign(Position.CENTER);
			//proxyPanel.setIconCls("groupProxyPanel");

	        FieldSet roleFS = new FieldSet("Informaci&oacute;n de Rol");  
	        roleFS.setCollapsible(true);
	        roleFS.setFrame(false);  
	        roleFS.setId("roleFS");
	        
	        TextField roleIdText = new TextField("Id","id",190);
	        roleIdText.setId("roleIdText");
	        if(!getRoleId().equals("")){
		          roleIdText.setReadOnly(true);
		       }
	        roleIdText.setAllowBlank(false);
	        roleIdText.setStyle("textTransform: uppercase;");
	        roleIdText.addListener(new FieldListenerAdapter(){
	        	 public void onBlur(Field field) {
	            String value = field.getValueAsString();
	            field.setValue(value.toUpperCase());
	        	}
	        	
	        });
	        roleFS.add(roleIdText);  
	        TextField loadSuccess = new TextField("loadSuccess","loadSuccess",190);
	        loadSuccess.setId("roleLoadSuccess");
	        loadSuccess.setVisible(false);
	        roleFS.add(loadSuccess);
	        
	        
	        TextField roleNameText = new TextField("Nombre", "name", 190);  
	        roleNameText.setId("roleNameText");
	        roleNameText.setAllowBlank(false);
	        roleFS.add(roleNameText); 
	        
	        TextArea roleDesc = new TextArea("Descripci&oacute;n", "description");  
	        roleDesc.setId("roleDesc");
	        roleDesc.setHideLabel(false);  
	        roleDesc.setWidth(190);
	        roleDesc.setHeight(80);
	        roleFS.add(roleDesc); 
	        
	        roleForm.add(roleFS,new BorderLayoutData(RegionPosition.CENTER));
	        
	        FieldSet roleModulesFS = new FieldSet("Modulos");  
	        roleModulesFS.setId("roleModulesFS");
	        roleModulesFS.setCollapsible(true);
	        roleModulesFS.setFrame(false);  
	        roleModulesFS.setAutoWidth(true);
	        
	        RecordDef moduleRecordDef = new RecordDef(new FieldDef[] {new StringFieldDef("id"), new StringFieldDef("desc")});
			final JsonReader moduleRecordReader = new JsonReader("data", moduleRecordDef);  
			moduleRecordReader.setSuccessProperty("success"); 
			moduleRecordReader.setId("id");
			
			final ItemSelector itemSelector = new ItemSelector();
			HttpProxy availableModules = new HttpProxy("/semrs/roleServlet?roleEdit=getModules&id="+getRoleId(), Connection.GET);
			Store fromStore = new Store(availableModules,moduleRecordReader);
			//fromStore.commitChanges();
			fromStore.load();
			itemSelector.setName("modules");
			itemSelector.setId("modules");
			itemSelector.setFieldLabel("Modulos");

			if(!getRoleId().equals("")){
				HttpProxy roleModules = new HttpProxy("/semrs/roleServlet?roleEdit=getModules&id="+getRoleId()+"&roleModules=true", Connection.GET);
				Store toStore = new Store(roleModules,moduleRecordReader);
				toStore.load();
				itemSelector.setToStore(toStore);
			}else{
				Store toStore = new Store(moduleRecordDef);
				toStore.add(moduleRecordDef.createRecord(new Object[]{"",""}));
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
			fromToolbar.addItem(new ToolbarTextItem("Modulos Disponibles"));  
			itemSelector.setFromToolbar(fromToolbar);  

			Toolbar toToolbar = new Toolbar();  
			ToolbarButton clearButton = new ToolbarButton("Modulos de este Rol");  
			clearButton.setIconCls("user-add-icon"); 

			toToolbar.addButton(clearButton);  
			itemSelector.setToToolbar(toToolbar);  
			
			roleModulesFS.add(itemSelector,new BorderLayoutData(RegionPosition.CENTER));
			
			roleForm.add(roleModulesFS,new BorderLayoutData(RegionPosition.CENTER));
	       
			 final Button saveButton = new Button("Guardar");
			 saveButton.setId("roleSaveButton");
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
		        			 RequestBuilder saveRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/roleServlet?roleEdit=submit&isNew="+getRoleId()+"&"+roleForm.getForm().getValues());
		        			 saveRequest.sendRequest(null, new RequestCallback() {
		                        public void onResponseReceived(Request req, Response res) {
		                        	MessageBox.hide();  
			                        MessageBox.getDialog().close();
		                           if(res.getText().indexOf("errores") !=-1){
		                        	   MessageBox.alert("Error", res.getText()); 
		                           }else if(res.getText().equals("")){
		                        	   MessageBox.hide();
			                           MessageBox.alert("Error", "Error interno"); 
		                           }else{
		                           MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());
		                           MainPanel.resetTimer();
		                           editRoleWindow.hide();
		                           store.reload();
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
		        	
		        });
		        saveButton.setIconCls("save-icon");
		        proxyPanel.addButton(saveButton);
		     
		        
		        Button cancel = new Button("Cancelar");
		        cancel.setId("cancelRoleButton");
		        cancel.addListener(new ButtonListenerAdapter(){
		        	public void onClick(Button button, EventObject e){
		        		editRoleWindow.hide();
		        	}
		        	
		        });
		        cancel.setIconCls("cancel-icon");
		        proxyPanel.addButton(cancel);  
			
		        roleForm.add(proxyPanel);
			
	      roleForm.doLayout();
	      if(!isNew){
	    	  roleForm.getForm().load("/semrs/roleServlet?roleEdit=load&id="+getRoleId(), null, Connection.GET, "Cargando...");
	    	  roleForm.getForm().addListener(new FormListenerAdapter(){
		    	   public void onActionComplete(Form form, int httpStatus, String responseText) {
		    		  if(form.findField("roleLoadSuccess").getValueAsString().equals("false")){
		    				MessageBox.show(new MessageBoxConfig() {  
		    					{  
		    						setTitle("Error");
		    						setMsg("Este rol no existe");
		    						setIconCls(MessageBox.ERROR);
		    					    setModal(true);
		    					    setButtons(MessageBox.OK);
		    					    setCallback(new MessageBox.PromptCallback() { 
		    						public void execute(
		    								String btnID,
		    								String text) {
		    							store.reload();
		    							editRoleWindow.close();
		    							
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
							setMsg("Ocurrio un error al tratar de obtener este rol");
							setIconCls(MessageBox.ERROR);
						    setModal(true);
						    setButtons(MessageBox.OK);
						    setCallback(new MessageBox.PromptCallback() { 
							public void execute(
									String btnID,
									String text) {
								store.reload();
								editRoleWindow.close();
								
							}  
	                        });  
						}  
					});
		    	    }
		    	   
		       });
		      }
	      /*
	      editRoleWindow.addListener(new ContainerListenerAdapter() {
	            public void onResize(BoxComponent component, int adjWidth, int adjHeight, int rawWidth, int rawHeight) {
	            	roleForm.getEl().center();
	            }
	        });
	        */
	      editRoleWindow.add(roleForm, new BorderLayoutData(RegionPosition.CENTER));  
	      editRoleWindow.doLayout();
		  return editRoleWindow;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}


}
